package com.github.bluzwang.myflux.example;


import com.github.bluzwong.myflux.lib.FluxDispatcher;
import com.github.bluzwong.myflux.lib.FluxStore;
import com.github.bluzwong.myflux.lib.*;
import com.github.bluzwong.myflux.lib.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class MainStore extends FluxStore {

    @Maintain
    List<String> datas = new ArrayList<>();

    @Maintain
    String text;

    @Maintain
    boolean isRequesting = false;

    FluxDispatcher dispatcher;
    public MainStore(FluxDispatcher dispatcher) {
        super(dispatcher);
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onRequestDone(String type, Map<String, Object> dataMap) {
        switch (type) {
            case RequestType.REQUEST_ADD:
                String result = (String) dataMap.get(RequestType.KEY_DATA);
                datas.add(result);
                createString();

                responsePostToUi(type);
                break;
            default:break;
        }
    }

    @Override
    protected void onDataRestored(SavedData savedData) {
        responsePostToUi(RequestType.RESTORE_UI);
    }

    public String createString() {
        StringBuilder buffer = new StringBuilder();
        for (String data : datas) {
            buffer.append(data).append("\n");
        }
        text = buffer.toString();
        return text;
    }
}
