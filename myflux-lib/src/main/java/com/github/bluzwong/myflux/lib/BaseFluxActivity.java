package com.github.bluzwong.myflux.lib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public abstract class BaseFluxActivity extends AppCompatActivity {
    protected abstract int provideContentId();

    protected abstract FluxStore provideStore();

    protected abstract void init();

    protected abstract void onRestoreView();

    protected abstract void onNewView();

    protected abstract void onResponse(String type, Map<String, Object> dataMap);

    private int hashCode = hashCode();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentId());
        init();
        FluxStore store = provideStore();
        if (store == null || !store.register(this, savedInstanceState, new Runnable() {
            @Override
            public void run() {
                onRestoreView();
            }
        })) {
            onNewView();
        }
    }

    public void onReceiveResponse(FluxResponse response) {
        if (response.getOwner() != hashCode) {
            return;
        }
        onResponse(response.getType(), response.getDataMap());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (provideStore() != null) {
            provideStore().onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        if (provideStore() != null) {
            provideStore().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (provideStore() != null) {
            provideStore().onResume();
        }
    }
}
