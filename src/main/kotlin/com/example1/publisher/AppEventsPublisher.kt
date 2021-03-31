package com.example1.publisher


import io.github.crabzilla.core.BoundedContextName
import io.github.crabzilla.core.EventRecord
import io.github.crabzilla.core.EventsPublisher
import io.micronaut.context.annotation.Context
import io.nats.streaming.StreamingConnection
import io.vertx.core.Future
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

/**
 * Publishes domain events to NATS (single writer process)
 */
@Context
class AppEventsPublisher(private val nats: StreamingConnection) : EventsPublisher {

    companion object {
        private val log = LoggerFactory.getLogger(AppEventsPublisher::class.java)
        val boundedContextName = BoundedContextName("example1")
    }

    init {
        log.info("I'm up and will publish events to ${boundedContextName.name}")
    }

    override fun publish(eventRecords: List<EventRecord>): Future<Long> {
        val promise = Promise.promise<Long>()
        var lastPublished: Long? = null
        var error = false
        for (event in eventRecords) {
            try {
                log.info("Will publish $event to ${boundedContextName.name}")
                nats.publish(boundedContextName.name, event.toJsonObject().toBuffer().bytes)
                if (log.isDebugEnabled) log.debug("Published $event to ${boundedContextName.name}")
                lastPublished = event.eventId
            } catch (e: Exception) {
                log.error("When publishing $event", e)
                promise.fail(e)
                error = true
                break
            }
        }
        if (!error) {
            promise.complete(lastPublished)
        }
        return promise.future()
    }

}