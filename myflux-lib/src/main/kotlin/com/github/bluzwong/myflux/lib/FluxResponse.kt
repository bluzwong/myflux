package com.github.bluzwong.myflux.lib

import org.simple.eventbus.Subscriber

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/8.
 */
public interface FluxResponse {
    //Subscribe(threadMode = ThreadMode.MainThread)
    @Subscriber
    public fun onReceiveResponse(response: Response) {
        if (response.owner != hashCode()) {
            return
        }
        onResponse(response.type, response.dataMap)
    }
    fun onResponse(type:String, dataMap:Map<String, Any>);
}