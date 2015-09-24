package com.github.bluzwong.myflux.lib

import android.os.Bundle
import org.simple.eventbus.Subscriber

private var stickyCount = 0

/**
 * Created by Bruce on 15/9/3.
 */
public abstract class Store(val dispatcher: Dispatcher) {

    var owner = -1 // ����store��Ӧ�� view hash
    var savedOwner = -1 // �ָ�״̬ʱ��õ�view hash
    var restoreViewFunc:(() -> Unit)? = null
    //    @Subscribe(threadMode = ThreadMode.MainThread)
    @Subscriber
    public fun onReceiveRequestDone(action: Action): Unit {
        if (owner != action.owner) {
            return
        }
        onRequestDone(action.type, action.dataMap)
    }

    //    Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    @Subscriber
    public fun onReceiveRestoreData(savedData: SavedData) {
        if (savedOwner != savedData.ownerHashCode) {
            return
        }
        //println("removeStickyEvent sticky count ${stickyCount - 1}")
        if (--stickyCount == 0) {
            dispatcher.eventBus.removeStickyEvent(savedData.javaClass)
        }
//        RemainUtils.autoRestore(this, savedData)
        onDataRestored(savedData)
        restoreViewFunc?.invoke()
        restoreViewFunc = null
    }

    private var haveSavedBundle = false
    public fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("ownerHashCode", owner)
        val savedData = SavedData(owner)
        onPreSavingData(savedData)
//        RemainUtils.autoSave(this, savedData)
        //dispatcher.unregister(this)
        dispatcher.eventBus.postSticky(savedData)
        stickyCount++
        //println("onSaveInstanceState sticky count ${stickyCount}")
        haveSavedBundle = true
    }

    private fun handleBundle(bundle: Bundle?): Boolean {
        bundle ?: return false
        savedOwner = bundle.getInt("ownerHashCode", -1)
        return savedOwner != -1
    }

    public fun onResume() {
        if (!haveSavedBundle) {
            return
        }
        haveSavedBundle = false
        //println("haveSavedBundle sticky count ${stickyCount - 1}")
        if (--stickyCount == 0) {
            dispatcher.eventBus.removeStickyEvent(javaClass<SavedData>())
        }
    }

    protected abstract fun onRequestDone(type: String, dataMap: Map<String, Any>)

    protected abstract fun onDataRestored(savedData: SavedData)
    protected fun onPreSavingData(savingData: SavedData){}

    open fun responseToUIWithType(type: String): Dispatcher.ResponseBuilder {
        return dispatcher.postResponseToUIWithType(type).from(owner) as Dispatcher.ResponseBuilder
    }

    fun responsePostToUi(type:String) {
        responseToUIWithType(type).post(null)
    }

    public fun register(activity: Any, savedInstanceState: Bundle? = null, ifRestoreDo: () -> Unit): Boolean {
        this.owner = activity.hashCode()
        val success = handleBundle(savedInstanceState)
        dispatcher.register(activity)
        dispatcher.register(this)
        restoreViewFunc = ifRestoreDo
        return success
    }


    public fun unregister(activity: Any) {
        this.owner = -1
        dispatcher.unregister(activity)
        dispatcher.unregister(this)
    }
}