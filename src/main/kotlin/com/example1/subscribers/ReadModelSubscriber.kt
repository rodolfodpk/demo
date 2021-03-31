package com.example1.subscribers

import io.micronaut.context.annotation.Context
import io.nats.streaming.Message
import io.nats.streaming.StreamingConnection
import io.nats.streaming.SubscriptionOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * Subscribes events from example1 topic then inform projections.
 */
@Context
class ReadModelSubscriber(nats: StreamingConnection, eventBus: EventBus) {

    companion object {
        private val log = LoggerFactory.getLogger(ReadModelSubscriber::class.java)
        private const val topic = "example1"
    }

    init {
        val opt = SubscriptionOptions.Builder().deliverAllAvailable().build()
        log.info("I'm up and subscribing to events from topic $topic")
        nats.subscribe(topic,  { msg: Message ->
            log.info("I received $msg")
            val asJson = JsonObject(String(msg.data))
            val aggregateName = asJson.getString("aggregateName")
            eventBus.request<Void>(aggregateName, asJson) { ar ->
                if (ar.failed()) {
                    log.error("When projecting $asJson to $aggregateName", ar.cause())
                } else {
                    log.info("$asJson successfully projected to $aggregateName")
                }
            }
            // TODO ack
        }, opt)
    }


}

