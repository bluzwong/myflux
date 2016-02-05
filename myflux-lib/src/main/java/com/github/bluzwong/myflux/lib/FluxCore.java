package com.github.bluzwong.myflux.lib;

import com.github.bluzwong.myflux.lib.switchtype.DispatcherFactory;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveTypeDispatcher;
import com.github.bluzwong.myflux.lib.switchtype.TargetHolder;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bluzwong on 2016/2/3.
 */
public enum FluxCore {
    INSTANCE;

    public void init() {
        RxBus.get().register(this);
    }

    public void destroy() {
        RxBus.get().unregister(this);
    }
    // init
    {
        init();
    }

    final Map<String, WeakReference<FluxReceiver>> receiverMaps = new ConcurrentHashMap<String, WeakReference<FluxReceiver>>();

    public void register(String receiverId,FluxReceiver receiver) {
        receiverMaps.put(receiverId, new WeakReference<FluxReceiver>(receiver));
    }

    public void unregister(String receiverId, FluxReceiver receiver) {
        if (receiver == null) {
            return;
        }
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }

        WeakReference<FluxReceiver> receiverHolder = receiverMaps.get(receiverId);
        if (receiverHolder == null || receiverHolder.get() == null) {
            receiverMaps.remove(receiverId);
            return;
        }
        FluxReceiver targetByID = receiverHolder.get();

        // UUID target all match
        if (targetByID == receiver) {
            receiverMaps.remove(receiverId);
        } else {
            // UUID has a new receiver
        }
    }

    static class FluxInvocationHandler implements InvocationHandler {

        private Object target;

        public FluxInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("onReceive") && objects.length == 1) {
                switchReceiveType(target, ((FluxResponse) objects[0]));
            } else if (methodName.equals("getTarget")) {
                return target;
            }
            return null;
        }
    }

    /**
     * should receive response at the receiver class by this:
     * // annotation args    type: distinguish request type
     * // method args     FluxResponse response: must be the only arg, response of request
     *
     *  \@ReceiveType(type = {"Custom Request Type"})
        public void doCcf(FluxResponse response) { }

     * @param receiverId unique receiver id
     * @param target receiver which receive response with receiverId
     */
    public void register(String receiverId, final Object target) {
        FluxReceiver instance = (FluxReceiver) Proxy.newProxyInstance(FluxReceiver.class.getClassLoader(), new Class[]{FluxReceiver.class, TargetHolder.class}, new FluxInvocationHandler(target));
        register(receiverId, instance);
    }

    public void unregister(String receiverId, Object target) {
        if (target == null) {
            return;
        }
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }

        WeakReference<FluxReceiver> receiverHolder = receiverMaps.get(receiverId);
        if (receiverHolder == null || receiverHolder.get() == null) {
            receiverMaps.remove(receiverId);
            return;
        }

        FluxReceiver targetByID = receiverHolder.get();
        if (targetByID instanceof TargetHolder) {
            Object realTarget = ((TargetHolder) targetByID).getTarget(); // maybe activity
            if (realTarget == target) {
                // targetByID is the fluxrespose proxy of target, should be removed
                receiverMaps.remove(receiverId);
            }
        }
    }


    @Subscribe
    public void onReceiveResponse(FluxResponse fluxResponse) {
        String receiverId = fluxResponse.getReceiverId();
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }
        WeakReference<FluxReceiver> receiverHolder = receiverMaps.get(receiverId);
        FluxReceiver receiver = receiverHolder.get();
        if (receiver == null) {
            receiverMaps.remove(receiverId);
            return;
        }

        receiver.onReceive(fluxResponse);
    }

    public static void switchReceiveType(Object target, FluxResponse response) {
        ReceiveTypeDispatcher dispatcher = DispatcherFactory.create(target);
        if (dispatcher == null) {
            switchReceiveTypeReflect(target, response);
            return;
        }
        dispatcher.dispatchType(target, response);
    }

    public static void switchReceiveTypeApt(Object target, FluxResponse response) {
        ReceiveTypeDispatcher dispatcher = DispatcherFactory.create(target);
        if (dispatcher == null) {
            return;
        }
        dispatcher.dispatchType(target, response);
    }

    public static void switchReceiveTypeReflect(Object target, FluxResponse response) {
        for (Method method : target.getClass().getDeclaredMethods()) {
            ReceiveType annotation = method.getAnnotation(ReceiveType.class);
            if (annotation == null) {
                continue;
            }
            String[] types = annotation.type();
            boolean needReceive = false;
            for (String t : types) {
                if (t.equals(response.getType())) {
                    needReceive = true;
                    break;
                }
            }
            if (!needReceive) {
                continue;
            }
            // right method
            Class<?>[] parameterTypes = method.getParameterTypes();

            int paramsLength = parameterTypes.length;
            if (paramsLength != 1) {
                continue;
            }

            if (parameterTypes[0] != FluxResponse.class) {
                continue;
            }


            method.setAccessible(true);
            try {
                method.invoke(target, response);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
