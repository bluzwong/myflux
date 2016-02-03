package com.github.bluzwong.myflux.lib.switchtype;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class DispatcherFactory {

    private static final Map<String, ReceiveTypeDispatcher> implementMap = new HashMap<String, ReceiveTypeDispatcher>();
    public static ReceiveTypeDispatcher create(Object target) {

        Class<?> targetClass = target.getClass();
        String clzName = targetClass.getCanonicalName();
        if (implementMap.containsKey(clzName)) {
            ReceiveTypeDispatcher iMaintain = implementMap.get(clzName);
            if (iMaintain != null) {
                return iMaintain;
            }
        }
        ReceiveTypeDispatcher findOutClass = findImplementClass(targetClass);
        implementMap.put(clzName, findOutClass);
        return findOutClass;
    }

    private static ReceiveTypeDispatcher findImplementClass(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        ReceiveTypeDispatcher implementClass = null;
        try {
            Class<?> maintainClz = Class.forName(clz.getCanonicalName() + "_Flux_Dispatcher");
            implementClass = (ReceiveTypeDispatcher) maintainClz.newInstance();
        } catch (ClassNotFoundException e) {
            implementClass = findImplementClass(clz.getSuperclass());
            //e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return implementClass;
    }
}
