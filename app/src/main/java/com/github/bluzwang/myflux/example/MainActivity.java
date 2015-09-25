package com.github.bluzwang.myflux.example;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.github.bluzwang.myflux_kotlin.R;

import com.github.bluzwong.myflux.lib.BaseFluxActivity;
import com.github.bluzwong.myflux.lib.FluxStore;
import com.github.bluzwong.myflux.processor.annotation.Maintain;

import java.util.Map;

public class MainActivity extends BaseFluxActivity {

    MainStore store = new MainStore(DispatcherHolder.dispatcher);
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
        store.wsd = "hehe";
        Log.i("bruce", "new view");
    }

    @Override
    protected void onResponse(String type, Map<String, Object> dataMap) {

    }
}
