package com.github.bluzwang.myflux.example;

import android.util.Log;
import com.github.bluzwang.myflux_kotlin.R;

import com.github.bluzwong.myflux.lib.BaseFluxActivity;
import com.github.bluzwong.myflux.lib.FluxStore;

import java.util.Map;

public class MainActivity extends BaseFluxActivity {

    MainStore store = new SonMainStore(DispatcherHolder.dispatcher);
    @Override
    protected int provideContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected FluxStore provideStore() {
        return store;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onRestoreView() {

    }

    @Override
    protected void onNewView() {
        store.fuckingCcf = "hehe";
        store.ccf = 250;
        Log.i("bruce", "new view =========================");
    }

    @Override
    protected void onResponse(String type, Map<String, Object> dataMap) {

    }
}
