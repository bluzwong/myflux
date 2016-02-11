package com.github.bluzwong.myflux.lib;

import com.github.bluzwong.myflux.lib.switchtype.*;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bluzwong on 2016/2/3.
 */
public enum FluxCore {
    INSTANCE;

    public static FluxCore getInstance() {
        return INSTANCE;
    }

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

    final Map<String, ReceiverHolder> receiverMaps = new ConcurrentHashMap<String, ReceiverHolder>();

    // register with a receiver implements interface
    public void register(String receiverId, FluxReceiver receiver) {
        if (receiver == null || receiverId == null) {
            // todo null check
            return;
        }
        receiverMaps.put(receiverId, ReceiverHolder.createNormal(receiver));
    }


    // register with a Object
    /**
     * should receive response at the receiver class by this:
     * // annotation args    type: distinguish request type
     * // method args     FluxResponse response: must be the only arg, response of request
     *
     * \@ReceiveType(type = {"Custom Request Type"})
     * public void doCcf(FluxResponse response) { }
     *
     * @param receiverId unique receiver id
     * @param receiver   which receive response with receiverId
     */
    public void register(String receiverId, final Object receiver) {
        if (receiver == null || receiverId == null) {
            // todo null check
            return;
        }

        FluxReceiver instance = (FluxReceiver) Proxy.newProxyInstance(FluxReceiver.class.getClassLoader(), new Class[]{FluxReceiver.class},
                new FluxInvocationHandler(receiver));
        receiverMaps.put(receiverId, ReceiverHolder.createProxy(instance, receiver));
    }

    // unregister a object or receiver interface
    public void unregister(String receiverId, Object receiver) {
        if (receiver == null || receiverId == null) {
            return;
        }
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }

        ReceiverHolder receiverHolder = receiverMaps.get(receiverId);
        if (receiverHolder == null || receiverHolder.getReceiver() == null) {
            receiverMaps.remove(receiverId);
            return;
        }
        Object registeredReceiver = receiverHolder.getRegisteredReceiver();

        // UUID target all match
        if (registeredReceiver == receiver) {
            receiverMaps.remove(receiverId);
        } else {
            // UUID has a new receiver
        }
    }



    /*public void unregister(String receiverId, Object target) {
        if (target == null || receiverId == null) {
            return;
        }
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }

        ReceiverHolder receiverHolder = receiverMaps.get(receiverId);
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
    }*/


    @Subscribe
    public void onReceiveResponse(FluxResponse fluxResponse) {
        String receiverId = fluxResponse.getReceiverId();
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }
        ReceiverHolder receiverHolder = receiverMaps.get(receiverId);

        if (receiverHolder == null || receiverHolder.getReceiver() == null) {
            receiverMaps.remove(receiverId);
            return;
        }

        FluxReceiver receiver = receiverHolder.getReceiver();
        if (receiver == null) {
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
