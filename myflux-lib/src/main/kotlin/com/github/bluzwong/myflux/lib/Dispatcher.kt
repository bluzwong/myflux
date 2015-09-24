package com.github.bluzwong.myflux.lib

import org.simple.eventbus.EventBus
import java.util.*

/**
 * Created by Bruce on 15/9/3.
 */
public class Dispatcher(val eventBus: EventBus) {
    private val registeredObj:MutableList<Any> = ArrayList<Any>()
    public fun register(obj: Any): Unit {
        if (!registeredObj.contains(obj)) {
            registeredObj.add(obj)
            if (obj is Store) {
                eventBus.registerSticky(obj)
            } else eventBus.register(obj)
        }
    }
    public fun unregister(obj: Any): Unit {
        if (registeredObj.contains(obj)) {
            eventBus.unregister(obj)
            registeredObj.remove(obj)
        }
    }
    fun postToBus(event: Any): Unit = eventBus.post(event)

    public fun dispatchRequestToStoreWithType(type: String): ActionBuilder = ActionBuilder(type)
    public fun postResponseToUIWithType(type: String): ResponseBuilder = ResponseBuilder(type)

    public open inner class Builder(val type: String, val postObj: (String,Int, MutableMap<String, Any>) -> Any) {
        val data: MutableMap<String, Any> = HashMap()
        var tmpKey: String? = null
        var owner = -1
        public open fun key(key: String): Builder {
            tmpKey = key
            return this
        }

        public open fun toValue(obj: Any): Builder {
            data.put(tmpKey?:throw IllegalArgumentException("Key has not set! call key() first.") , obj)
            tmpKey = null
            return this
        }

        public fun from(owner:Int):Builder {
            this.owner = owner
            return this
        }
        public fun post(nul: Any? = null): Unit = postToBus(postObj(type,owner, data))
    }

    public inner class ActionBuilder(actionType: String) : Builder(actionType, ::Action) {
        override fun key(key: String): ActionBuilder = super.key(key) as ActionBuilder
        override fun toValue(obj: Any): ActionBuilder = super.toValue(obj) as ActionBuilder
    }

    public inner class ResponseBuilder(responseType: String) : Builder(responseType, ::Response) {
        override fun key(key: String): ResponseBuilder = super.key(key) as ResponseBuilder
        override fun toValue(obj: Any): ResponseBuilder = super.toValue(obj) as ResponseBuilder
    }
}