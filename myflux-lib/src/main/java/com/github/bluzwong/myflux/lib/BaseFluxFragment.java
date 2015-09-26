package com.github.bluzwong.myflux.lib;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import org.simple.eventbus.Subscriber;

import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public abstract class BaseFluxFragment extends Fragment {
    protected abstract FluxStore provideStore();

    protected abstract void onRestoreView();

    protected abstract void onNewView();

    protected abstract void onResponse(String type, Map<String, Object> dataMap);

    private int hashCode = hashCode();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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


    @Subscriber
    public void onReceiveResponse(FluxResponse response) {
        if (response.getOwner() != hashCode) {
            return;
        }
        onResponse(response.getType(), response.getDataMap());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (provideStore() != null) {
            provideStore().onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroyView() {
        if (provideStore() != null) {
            provideStore().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (provideStore() != null) {
            provideStore().onResume();
        }
    }
}
