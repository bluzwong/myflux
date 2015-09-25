package com.github.bluzwong.myflux.lib.kotlin

import java.util.*

/**
 * Created by Bruce-Home on 2015/9/11.
 */
public class SavedData(val ownerHashCode:Int, val dataMap:MutableMap<String, Any> = HashMap<String, Any>()) {
    public fun put(key:String, data:Any) {
        dataMap.put(key, data)
    }

    public fun get(key:String):Any? {
        if (dataMap containsKey key) {
            return dataMap.get(key)
        }
        return null
    }

    public fun doIt<T>(key:String, doWhat:(T) -> Unit): Boolean{
        val tmp = get(key) ?: return false
        doWhat.invoke(tmp as T)
        return true
    }
}