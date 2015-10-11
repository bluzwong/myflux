package com.github.bluzwang.myflux.example;

import android.util.Log;

import com.github.bluzwong.myflux.lib.FluxDispatcher;
import com.github.bluzwong.myflux.lib.FluxStore;
import com.github.bluzwong.myflux.lib.SavedData;
import com.github.bluzwong.myflux.lib.Maintain;

import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public class MainStore extends FluxStore {
    @Maintain
    String fuckingCcf;
    @Maintain
    int ccf;

    @Maintain
    double wsd;

    @Maintain
    String wsdccf;
    public MainStore(FluxDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void onRequestDone(String type, Map<String, Object> dataMap) {

    }

    @Override
    protected void onDataRestored(SavedData savedData) {
        Log.i("bruce", "date restrored -------------------> \nwsd = " + fuckingCcf + " \nccf = "  + ccf);
    }

}
