package com.github.bluzwong.myflux.lib;

import android.text.TextUtils;
import org.simple.eventbus.EventBus;

import java.util.*;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public enum FluxDispatcher {
    INSTANCE;
    private EventBus eventBus = EventBus.getDefault();
    private List<Object> registeredObj = new ArrayList<Object>();
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }
    public void register(Object obj) {
        if (!registeredObj.contains(obj)) {
            registeredObj.add(obj);
            if (obj instanceof FluxStore) {
                eventBus.registerSticky(obj);
            } else {
                eventBus.register(obj);
            }
        }
    }

    public void unregister(Object obj) {
        if (registeredObj.contains(obj)) {
            eventBus.unregister(obj);
            registeredObj.remove(obj);
        }
    }

    public void postToBus(Object event) {
        eventBus.post(event);
    }

    /**
     * when your action creator have received data from data source(database or webapi)
     * use this method to dispatch to its store
     * called only in action creator
     * @param type
     * @param owner
     * @return
     */
    public ActionBuilder dispatchRequestToStoreWithType(String type, int owner) {
        return new ActionBuilder(type, owner);
    }

    /**
     * when store handled data (hold them and easy logic) from creator(by this dispatcher)
     * tell ui to show them
     * called only in store
     * @param type
     * @param owner
     * @return
     */
    public ResponseBuilder postResponseToUIWithType(String type, int owner) {
        return new ResponseBuilder(type, owner);
    }

    public class ActionBuilder {
        private String type;
        private int owner = -1;

        public ActionBuilder(String type, int owner) {
            this.type = type;
            this.owner = owner;
        }

        private Map<String, Object> data = new HashMap<String, Object>();

        public ActionBuilder withData(String key, Object obj) {
            if (TextUtils.isEmpty(key)) {
                throw new IllegalArgumentException("key is null");
            }
            data.put(key, obj);
            return this;
        }
        public void post(String requestUUID) {
            postToBus(new FluxAction(type, owner, data, requestUUID));
        }

        public void post() {
            post("");
        }
    }
    public class ResponseBuilder {
        private String type;
        private int owner = -1;

        public ResponseBuilder(String type, int owner) {
            this.type = type;
            this.owner = owner;
        }

        private Map<String, Object> data = new HashMap<String, Object>();

        public ResponseBuilder withData(String key, Object obj) {
            if (TextUtils.isEmpty(key)) {
                throw new IllegalArgumentException("key is null");
            }
            data.put(key, obj);
            return this;
        }

        public ResponseBuilder withDatas(Map<String, Object> datas) {
            data.putAll(datas);
            return this;
        }
        public void post() {
            postToBus(new FluxResponse(type, owner, data));
        }
    }

}
