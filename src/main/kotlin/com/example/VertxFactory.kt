package com.example

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageCodec
import javax.inject.Singleton

@Factory
class VertxFactory {

    @Bean
    @Singleton
    fun vertx(): Vertx {
        val vertx = Vertx.vertx()
        vertx.registerLocalCodec()
        return vertx
    }

    @Bean
    @Singleton
    fun eventbus(vertx: Vertx): EventBus {
        val eventBus = vertx.eventBus()
        eventBus.unregisterCodec("local")
        eventBus.registerCodec(object : MessageCodec<Any, Any> {
            override fun decodeFromWire(pos: Int, buffer: Buffer?) = throw NotImplementedError()
            override fun encodeToWire(buffer: Buffer?, s: Any?) = throw NotImplementedError()
            override fun transform(s: Any?) = s
            override fun name() = "local"
            override fun systemCodecID(): Byte = -1
        })
        return eventBus
    }

}

fun Vertx.registerLocalCodec() {
    eventBus().unregisterCodec("local")
    eventBus().registerCodec(object : MessageCodec<Any, Any> {
        override fun decodeFromWire(pos: Int, buffer: Buffer?) = throw NotImplementedError()
        override fun encodeToWire(buffer: Buffer?, s: Any?) = throw NotImplementedError()
        override fun transform(s: Any?) = s
        override fun name() = "local"
        override fun systemCodecID(): Byte = -1
    })
}

fun <T> EventBus.localRequest(address: String,
                              message: Any,
                              options: DeliveryOptions? = null,
                              replyHandler: ((AsyncResult<Message<T>>) -> Unit)? = null) {
    val deliveryOptions = options?.let { DeliveryOptions(options) } ?: DeliveryOptions()
    deliveryOptions.apply {
        codecName = "local"
        isLocalOnly = true
    }
    request(address, message, deliveryOptions, replyHandler)
}

fun <T: Any> Message<T>.localReply(reply: Any, options: DeliveryOptions? = null) {
    val deliveryOptions = options?.let { DeliveryOptions(options) } ?: DeliveryOptions()
    deliveryOptions.apply {
        codecName = "local"
        isLocalOnly = true
    }
    reply(reply, deliveryOptions)
}