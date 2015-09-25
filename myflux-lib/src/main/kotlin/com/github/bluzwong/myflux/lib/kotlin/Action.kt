package com.github.bluzwong.myflux.lib.kotlin

/**
 * Created by Bruce on 15/9/3.
 */
public class Action(val type: String,val owner:Int, val dataMap: MutableMap<String, Any>) {
    public fun getData(key: String): Any? = dataMap get key ?: null
}