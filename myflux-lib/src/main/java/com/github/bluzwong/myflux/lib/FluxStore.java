package com.github.bluzwong.myflux.lib;


import android.os.Bundle;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
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
    private List<Integer> historyOwnerList = new ArrayList<>(); // view before
    private int savedOwner = -1; // last before recreate
    private Runnable restoreViewFunc; // view restore func to run after data restored
    private boolean haveSavedBundle = false;

    /**
     * called when dispatcher request finish
     * @param type request type
     * @param dataMap datamap
     */
    protected abstract void onRequestDone(String type, Map<String, Object> dataMap);

    @Subscriber
    public void onReceiveRequestDone(FluxAction action) {
        int actionOwner = action.getOwner();
        if (actionOwner <= 0) {
            return;
        }
        if (actionOwner == owner || actionOwner == savedOwner || historyOwnerList.contains(actionOwner)) {
            onRequestDone(action.getType(), action.getDataMap());
        }
    }

    /**
     * called when data is restored after recreate
     * @param savedData
     */
    protected abstract void onDataRestored(SavedData savedData);

    @Subscriber
    public void onReceiveRestoreData(SavedData savedData) {
        if (savedOwner != savedData.getOwnerHashCode()) {
            return;
        }
        if (--stickyCount == 0) {
            dispatcher.getEventBus().removeStickyEvent(SavedData.class);
        }

        historyOwnerList.addAll((List<Integer>) savedData.get("historyOwnerList"));
        // maintain is created by apt
        if (maintain != null) {
            maintain.autoRestore(this, savedData);
        }
        onDataRestored(savedData);
        if (restoreViewFunc != null) {
            restoreViewFunc.run();
        }
    }

    /**
     * called before saving data (onSaveInstanceState)
     * @param savingData
     */
    protected void onPreSavingData(SavedData savingData) {
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("ownerHashCode", owner);
        SavedData savedData = new SavedData(owner);
        onPreSavingData(savedData);
        // maintain is created by apt
        if (maintain != null) {
            maintain.autoSave(this, savedData);
        }
        savedData.put("historyOwnerList", historyOwnerList);
        dispatcher.getEventBus().postSticky(savedData);
        stickyCount++;
        haveSavedBundle = true;
    }

    private boolean handleBundle(Bundle bundle) {
        if (bundle == null) {
            return false;
        }
        savedOwner = bundle.getInt("ownerHashCode", -1);
        historyOwnerList.add(savedOwner);
        return savedOwner != -1;
    }

    /**
     * must be called onresume()
     */
    public void onResume() {
        if (!haveSavedBundle) {
            return;
        }
        haveSavedBundle = false;
        if (--stickyCount == 0) {
            dispatcher.getEventBus().removeStickyEvent(SavedData.class);
        }
    }

    /**
     * response to view (activity or fragment) that request is finished, show the data to ui
     * need call post().
     * @param type
     * @return
     */
    public FluxDispatcher.ResponseBuilder responseToUiWithType(String type) {
        return dispatcher.postResponseToUIWithType(type, owner);
    }

    /**
     * response to view (activity or fragment) that request is finished, show the data to ui
     * auto  call post().
     * @param type
     */
    public void responsePostToUi(String type) {
        responseToUiWithType(type).post();
    }

    // if is restore (restart activity) returns true else false

    /**
     * must be called in oncreate() or store wont receive any thing
     * @param activity
     * @param savedInstanceState
     * @param ifRestoreDo
     * @return
     */
    public boolean register(Object activity, Bundle savedInstanceState, Runnable ifRestoreDo) {
        this.owner = activity.hashCode();
        boolean isRestore = handleBundle(savedInstanceState);
        dispatcher.register(activity);
        dispatcher.register(this);
        restoreViewFunc = ifRestoreDo;
        return isRestore;
    }

    /**
     * must called in ondestroy()
     * @param activity
     */
    public void unregister(Object activity) {
        this.owner = -1;
        dispatcher.unregister(activity);
        dispatcher.unregister(this);
    }
}
