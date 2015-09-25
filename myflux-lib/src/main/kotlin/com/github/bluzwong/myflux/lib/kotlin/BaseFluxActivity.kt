package com.github.bluzwong.myflux.lib.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/15.
 */
public abstract class BaseFluxActivity : android.support.v7.app.AppCompatActivity(), FluxResponse {

    /**
     * provide layout resource id
     */
    protected abstract fun provideContentId(): Int

    /**
     * provide store to hold and control view state and data
     */
    protected abstract fun provideStore(): Store?

    /**
     * init views
     */
    protected abstract fun init():Unit

    /**
     * called when activity is being restored
     * check the info in store and refresh the view
     */
    protected abstract fun onRestoreView():Unit

    /**
     * called when is the first time showing
     */
    protected abstract fun onNewView():Unit

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(provideContentId())
        init()
        if (provideStore()?.register(this, savedInstanceState, { onRestoreView() })?:false) {

        } else {
            onNewView()
        }
    }

    override public fun onSaveInstanceState(outState: android.os.Bundle?) {
        super<AppCompatActivity>.onSaveInstanceState(outState)
        provideStore()?.onSaveInstanceState(outState ?: return)
    }

    override protected fun onDestroy() {
        provideStore()?.unregister(this)
        super<AppCompatActivity>.onDestroy()
    }

    override protected fun onResume() {
        super<AppCompatActivity>.onResume()
        provideStore()?.onResume()
    }
}