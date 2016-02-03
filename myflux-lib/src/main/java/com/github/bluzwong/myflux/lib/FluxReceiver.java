package com.github.bluzwong.myflux.lib;

import java.util.Map;

/**
 * Created by bluzwong on 2016/2/3.
 */
public interface FluxReceiver {
    void onReceive(Map<String, Object> dataMap, String type, String requestUUID);
}
