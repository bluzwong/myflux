package com.github.bluzwong.myflux.lib.switchtype;

import java.util.Map;

/**
 * Created by bluzwong on 2016/2/3.
 */
public interface ReceiveTypeDispatcher {
    void dispatchType(Object target, Map<String, Object> dataMap, String type);
}
