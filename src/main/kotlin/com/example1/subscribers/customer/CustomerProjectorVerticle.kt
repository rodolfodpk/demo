package com.example1.subscribers.customer

import com.example1.core.customer.CustomerEvent
import com.example1.core.customer.CustomerSerialization.customerJson
import io.github.crabzilla.core.DOMAIN_EVENT_SERIALIZER
import io.micronaut.context.annotation.Context
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import javax.inject.Named

@Context
class CustomerProjectorVerticle(@Named("jooq-style") private val repo: CustomerRepository) : AbstractVerticle() {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerProjectorVerticle::class.java)
        private const val topic = "Customer"
    }

    override fun start() {
        vertx.eventBus()
            .consumer<JsonObject>(topic) { msg ->
                val asJson = msg.body()
                val aggregateId = asJson.getInteger("aggregateId")
                val eventId = asJson.getLong("eventId") // TODO use cache for idempotency
                val eventAsJson = asJson.getJsonObject("eventAsjJson")
                project(eventId, aggregateId, eventAsJson)
                    .onFailure { msg.fail(500, it.message) }
                    .onSuccess { msg.reply(true)}
            }
        log.info("Started consuming events from topic [$topic]")
    }

    private fun project(eventId: Long, id: Int, eventAsJson: JsonObject): Future<Void> {
        val event = customerJson.decodeFromString(DOMAIN_EVENT_SERIALIZER, eventAsJson.toString()) as CustomerEvent
        log.info("Will project event $event to read model")
        return when (event) {
            is CustomerEvent.CustomerRegistered -> repo.upsert(id, event.name, false)
            is CustomerEvent.CustomerActivated -> repo.updateStatus(id, true)
            is CustomerEvent.CustomerDeactivated -> repo.updateStatus(id, false)
        }
    }

}