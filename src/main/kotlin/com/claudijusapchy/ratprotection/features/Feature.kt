package com.claudijusapchy.ratprotection.features

import com.claudijusapchy.ratprotection.events.EventBus

abstract class Feature(
    val id: String,
    val name: String,
    val enabled: () -> Boolean
) {

    fun isEnabled(): Boolean = enabled()

    abstract fun onActivate()

    protected fun <T : Any> subscribe(eventClass: Class<T>, handler: (T) -> Unit) {
        EventBus.subscribe(eventClass, handler)
    }
}