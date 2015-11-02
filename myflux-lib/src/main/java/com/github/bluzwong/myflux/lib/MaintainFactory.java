package com.github.bluzwong.myflux.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class MaintainFactory {

    private static final Map<String, IMaintain> maintainMap = new HashMap<>();
    public static IMaintain create(Object target) {

        Class<?> targetClass = target.getClass();
        String clzName = targetClass.getCanonicalName();
        if (maintainMap.containsKey(clzName)) {
            IMaintain iMaintain = maintainMap.get(clzName);
            if (iMaintain != null) {
                return iMaintain;
            }
        }
        IMaintain findOutMaintain = findMaintainClass(targetClass);
        maintainMap.put(clzName, findOutMaintain);
        return findOutMaintain;
    }

    private static IMaintain findMaintainClass(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        IMaintain iMaintain = null;
        try {
            Class<?> maintainClz = Class.forName(clz.getCanonicalName() + "_Maintain");
            iMaintain = (IMaintain) maintainClz.newInstance();
        } catch (ClassNotFoundException e) {
            iMaintain = findMaintainClass(clz.getSuperclass());
            //e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return iMaintain;
    }
}
