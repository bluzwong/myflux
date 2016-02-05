package com.github.bluzwong.myflux.lib.switchtype;

import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxResponse;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Bruce-Home on 2016/2/5.
 */
public class FluxInvocationHandler implements InvocationHandler {

    private WeakReference<Object> targetReference;

    public FluxInvocationHandler(Object target) {
        targetReference = new WeakReference<Object>(target);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (targetReference.get() == null) {
            return null;
        }
        String methodName = method.getName();
        if (methodName.equals("onReceive") && objects.length == 1) {
            FluxCore.switchReceiveType(targetReference.get(), ((FluxResponse) objects[0]));
        }/* else if (methodName.equals("getTarget")) {
                return target;
            }*/
        return null;
    }
}