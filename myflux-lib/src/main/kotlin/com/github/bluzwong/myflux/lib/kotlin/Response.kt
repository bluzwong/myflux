package com.github.bluzwong.myflux.lib.kotlin

/**
 * Created by Bruce on 15/9/3.
 */
public class Response(val type: String,val owner:Int, val dataMap: MutableMap<String, Any>) {
    public fun getData(key: String): Any? = dataMap get key ?: null
}
//public fun new(type:String, dataMap:MutableMap<String, Any>):Response = Response(type, dataMap)
