package com.github.bluzwong.myflux.lib;


import android.os.Bundle;
import org.simple.eventbus.Subscriber;

import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public abstract class FluxStore {
    private static int stickyCount = 0;
    private FluxDispatcher dispatcher;

    public FluxStore(FluxDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private IMaintain maintain = MaintainFactory.create(this);
    private int owner = -1; // current view
    private int savedOwner = -1; // view before recreate
    private Runnable restoreViewFunc;
    private boolean haveSavedBundle = false;

    protected abstract void onRequestDone(String type, Map<String, Object> dataMap);

    @Subscriber
    public void onReceiveRequestDone(FluxAction action) {
        if (owner != action.getOwner()) {
            return;
        }
        onRequestDone(action.getType(), action.getDataMap());
    }

    protected abstract void onDataRestored(SavedData savedData);

    @Subscriber
    public void onReceiveRestoreData(SavedData savedData) {
        if (savedOwner != savedData.getOwnerHashCode()) {
            return;
        }
        if (--stickyCount == 0) {
            dispatcher.getEventBus().removeStickyEvent(SavedData.class);
        }
        maintain.autoRestore(this, savedData);
        onDataRestored(savedData);
        if (restoreViewFunc != null) {
            restoreViewFunc.run();
        }
    }

    protected void onPreSavingData(SavedData savingData) {
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("ownerHashCode", owner);
        SavedData savedData = new SavedData(owner);
        onPreSavingData(savedData);
        maintain.autoSave(this, savedData);
        dispatcher.getEventBus().postSticky(savedData);
        stickyCount++;
        haveSavedBundle = true;
    }

    private boolean handleBundle(Bundle bundle) {
        if (bundle == null) {
            return false;
        }
        savedOwner = bundle.getInt("ownerHashCode", -1);
        return savedOwner != -1;
    }

    public void onResume() {
        if (!haveSavedBundle) {
            return;
        }
        haveSavedBundle = false;
        if (--stickyCount == 0) {
            dispatcher.getEventBus().removeStickyEvent(SavedData.class);
        }
    }

    public FluxDispatcher.ResponseBuilder responseToUiWithType(String type) {
        return dispatcher.postResponseToUIWithType(type, owner);
    }

    public void responsePostToUi(String type) {
        responseToUiWithType(type).post();
    }

    // if is restore (restart activity) returns true else false
    public boolean register(Object activity, Bundle savedInstanceState, Runnable ifRestoreDo) {
        this.owner = activity.hashCode();
        boolean isRestore = handleBundle(savedInstanceState);
        dispatcher.register(activity);
        dispatcher.register(this);
        restoreViewFunc = ifRestoreDo;
        return isRestore;
    }

    public void unregister(Object activity) {
        this.owner = -1;
        dispatcher.unregister(activity);
        dispatcher.unregister(this);
    }
}
