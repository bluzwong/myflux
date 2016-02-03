package com.github.bluzwong.myflux.lib;

import com.github.bluzwong.myflux.lib.switchtype.DispatcherFactory;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveTypeDispatcher;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    final Map<String, FluxReceiver> receiverMaps = new HashMap<String, FluxReceiver>();

    public void register(String receiverId,FluxReceiver receiver) {
        receiverMaps.put(receiverId, receiver);
    }

    public void unregister(String receiverId, FluxReceiver receiver) {
        if (receiver == null) {
            return;
        }
        if (!receiverMaps.containsKey(receiverId)) {
            return;
        }

        Object target = receiverMaps.get(receiverId);
        if (target == null) {
            receiverMaps.remove(receiverId);
            return;
        }

        // UUID target all match
        if (target == receiver) {
            receiverMaps.remove(receiverId);
        } else {
            // UUID has a new receiver
        }
    }

    @Subscribe
    public void onReceiveResponse(FluxResponse fluxResponse) {
        if (!receiverMaps.containsKey(fluxResponse.getReceiverId())) {
            return;
        }

        FluxReceiver receiver = receiverMaps.get(fluxResponse.getReceiverId());
        if (receiver == null) {
            return;
        }

        receiver.onReceive(fluxResponse.getDataMap(), fluxResponse.getType(), fluxResponse.getRequestUUID());
    }

    public static void switchReceiveType(Object target, Map<String, Object> dataMap, String type) {
        ReceiveTypeDispatcher dispatcher = DispatcherFactory.create(target);
        if (dispatcher == null) {
            switchReceiveTypeReflect(target, dataMap, type);
            return;
        }
        dispatcher.dispatchType(target, dataMap, type);
    }

    public static void switchReceiveTypeApt(Object target, Map<String, Object> dataMap, String type) {
        ReceiveTypeDispatcher dispatcher = DispatcherFactory.create(target);
        if (dispatcher == null) {
            return;
        }
        dispatcher.dispatchType(target, dataMap, type);
    }

    public static void switchReceiveTypeReflect(Object target, Map<String, Object> dataMap, String type) {
        for (Method method : target.getClass().getDeclaredMethods()) {
            ReceiveType annotation = method.getAnnotation(ReceiveType.class);
            if (annotation == null) {
                continue;
            }
            String[] types = annotation.type();
            boolean needReceive = false;
            for (String t : types) {
                if (t.equals(type)) {
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
            if (paramsLength != 2 && paramsLength != 1) {
                continue;
            }

            if (parameterTypes[0] != Map.class) {
                continue;
            }
            if (paramsLength == 2 && parameterTypes[1] != String.class) {
                continue;
            }

            method.setAccessible(true);
            try {
                if (paramsLength == 2) {
                    method.invoke(target, dataMap, type);
                } else {
                    method.invoke(target, dataMap);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
