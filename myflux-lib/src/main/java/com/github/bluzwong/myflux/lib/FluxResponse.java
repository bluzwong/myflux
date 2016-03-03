package com.github.bluzwong.myflux.lib;

import com.hwangjr.rxbus.RxBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
    public static final String FLUX_KEY_ONLY = "$FLUX_KEY_ONLY$";
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

    public FluxResponse put(String key, Object data) {
        dataMap.put(key, data);
        return this;
    }

    public <T> T get(String key) {
        return (T) dataMap.get(key);
    }

    public FluxResponse putOnly(Object data) {
        dataMap.put(FLUX_KEY_ONLY, data);
        return this;
    }

    public <T> T getOnly() {
        return (T) dataMap.get(FLUX_KEY_ONLY);
    }


    public void post() {
        Observable.just(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FluxResponse>() {
                    @Override
                    public void call(FluxResponse fluxResponse) {
                        RxBus.get().post(fluxResponse);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public Object getData(String key) {
        return dataMap.get(key);
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
