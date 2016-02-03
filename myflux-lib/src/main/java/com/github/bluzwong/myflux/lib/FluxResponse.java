package com.github.bluzwong.myflux.lib;

import com.hwangjr.rxbus.RxBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluzwong on 2016/2/3.
 */
public final class FluxResponse {
    private Map<String, Object> dataMap;
    private String type;
    private String requestUUID;
    private String receiverId;

    private FluxResponse(String receiverId, String type, String requestUUID) {
        this.type = type;
        this.receiverId = receiverId;
        this.requestUUID = requestUUID;
        dataMap = new HashMap<String, Object>();
    }

    public static FluxResponse create(String receiverId, String type, String requestUUID) {
        return new FluxResponse(receiverId, type, requestUUID);
    }

    public FluxResponse setData(String key, Object data) {
        dataMap.put(key, data);
        return this;
    }

    public void post() {
        RxBus.get().post(this);
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }


    public String getType() {
        return type;
    }


    public String getRequestUUID() {
        return requestUUID;
    }


    public String getReceiverId() {
        return receiverId;
    }

}
