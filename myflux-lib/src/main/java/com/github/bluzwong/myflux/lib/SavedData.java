package com.github.bluzwong.myflux.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class SavedData {
    interface IDoWhat<T> {
        void what(T data);
    }
    private int ownerHashCode;
    private Map<String,Object> dataMap = new HashMap<String,Object>();
    public void put(String key, Object data) {
        dataMap.put(key, data);
    }
    public Object get(String key) {
        if (dataMap.containsKey(key)) {
            return dataMap.get(key);
        }
        return null;
    }

    public <T> boolean doIt(String key, IDoWhat<T> doWhat) {
        Object obj = get(key);
        if (obj == null) {
            return false;
        }
        if (doWhat != null) {
            doWhat.what((T)obj);
        }
        return true;
    }

    public SavedData(int ownerHashCode) {
        this.ownerHashCode = ownerHashCode;
    }

    public int getOwnerHashCode() {
        return ownerHashCode;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }
}
