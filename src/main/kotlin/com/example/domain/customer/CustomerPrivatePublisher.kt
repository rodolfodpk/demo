package com.example.domain.customer


import com.example.infra.privateTopic
import com.example.infra.wrapMessage
import io.github.crabzilla.core.JsonEventPublisher
import io.nats.streaming.StreamingConnection
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Publishes domain events to NATS
 */
@Singleton
class CustomerPrivatePublisher(private val nats: StreamingConnection) : JsonEventPublisher {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerPrivatePublisher::class.java)
    }

    private val targetTopic = privateTopic(customerConfig.name)

    init {
        log.info("I'm up and will publish events to $targetTopic")
    }

    override fun publish(eventId: Long, id: Int, eventAsJson: JsonObject): Future<Void> {
        log.info("Will publish $eventAsJson to $targetTopic")
        return try {
            nats.publish(targetTopic, wrapMessage(eventId, id, eventAsJson))
            return Future.succeededFuture()
        } catch (e: Exception) {
            Future.failedFuture(e)
        }
    }
}