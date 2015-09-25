package com.github.bluzwong.myflux.lib;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class MaintainFactory {
    public static IMaintain create(Object target) {
        try {
            return (IMaintain) Class.forName(target.getClass().getCanonicalName() + "$$Maintain").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
