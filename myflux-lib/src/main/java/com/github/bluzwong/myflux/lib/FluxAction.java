package com.github.bluzwong.myflux.lib;

import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class FluxAction {
    private String type;
    private int owner;
    private Map<String, Object> dataMap;
    private String requestUUID;
    public FluxAction(String type, int owner, Map<String, Object> dataMap, String requestUUID) {
        this.type = type;
        this.owner = owner;
        this.dataMap = dataMap;
        this.requestUUID = requestUUID;
    }

    public FluxAction(String type, int owner, Map<String, Object> dataMap) {
        this(type, owner, dataMap, "");
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
