package com.example1


import com.example1.infra.wrapMessage
import io.github.crabzilla.core.BoundedContextName
import io.github.crabzilla.core.EventRecord
import io.github.crabzilla.core.JsonEventPublisher
import io.nats.streaming.StreamingConnection
import io.vertx.core.Future
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Publishes domain events to NATS (single writer process)
 */
@Singleton
class AppOutboxPublisher(private val nats: StreamingConnection) : JsonEventPublisher {

    companion object {
        private val log = LoggerFactory.getLogger(AppOutboxPublisher::class.java)
    }

    private val boundedContextName = BoundedContextName("example1")

    init {
        log.info("I'm up and will publish events to ${boundedContextName.name}")
    }

    override fun publish(eventRecord: EventRecord): Future<Void> {
        log.info("Will publish $eventRecord to ${boundedContextName.name}")
        return try {
            nats.publish(boundedContextName.name, wrapMessage(eventRecord))
            return Future.succeededFuture()
        } catch (e: Exception) {
            Future.failedFuture(e)
        }
    }
}