package com.example.domain.customer


import com.example.nats.publisher.NatsStreamingPublisher
import io.github.crabzilla.core.EventPublisher
import io.vertx.core.Future
import io.vertx.core.Promise
import javax.inject.Singleton

@Singleton
class CustomerNatsProjector(val natsPublihser : NatsStreamingPublisher) : EventPublisher<CustomerEvent> {

    override fun project(id: Int, event: CustomerEvent): Future<Void> {
        val promise = Promise.promise<Void>()
        natsPublihser.publish("foo", event)
        return promise.future()
    }
}