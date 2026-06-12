package com.claudijusapchy.ratprotection.events

object EventBus {

    private val handlers = HashMap<Class<*>, MutableList<(Any) -> Unit>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> subscribe(eventClass: Class<T>, handler: (T) -> Unit) {
        handlers.getOrPut(eventClass) { mutableListOf() }
            .add(handler as (Any) -> Unit)
    }

    fun post(event: Any) {
        handlers[event::class.java]?.forEach { it(event) }
    }
}