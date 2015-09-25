package com.github.bluzwong.myflux.lib

import android.app.Fragment
import android.os.Bundle
import android.view.View

/**
 * Created by Bruce-Home on 2015/9/20.
 */
public abstract class BaseFluxFragment:Fragment(), FluxResponse {
    protected abstract fun provideStore(): Store?
    protected abstract fun onRestoreView()
    protected abstract fun onNewView()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        if (provideStore()?.register(this, savedInstanceState, { onRestoreView() }) ?: false) {

        } else {
            onNewView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<Fragment>.onSaveInstanceState(outState)
        provideStore()?.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super<Fragment>.onResume()
        provideStore()?.onResume()
    }

    override fun onDestroyView() {
        provideStore()?.unregister(this)
        super<Fragment>.onDestroyView()
    }
}