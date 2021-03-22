package com.example.domain.customer


import io.github.crabzilla.core.EventPublisher
import io.micronaut.natsstreaming.publisher.NatsStreamingPublisher
import io.vertx.core.Future
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Named("nats-publisher")
class CustomerNatsPublisher(private val natsPublisher : NatsStreamingPublisher) : EventPublisher<CustomerEvent> {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerNatsPublisher::class.java)
        const val targetTopic = "foo"
    }

    init {
        log.info("I'm up")
    }

    override fun publish(id: Int, event: CustomerEvent): Future<Void> {
        log.info("Will publish event $event to topic $targetTopic")
        natsPublisher.publish(targetTopic, event)
        return Future.succeededFuture()
    }
}