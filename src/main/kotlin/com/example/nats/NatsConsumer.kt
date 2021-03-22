package com.example.nats

import com.example.domain.customer.CustomerEvent
import io.micronaut.http.annotation.Body
import io.micronaut.natsstreaming.annotation.NatsStreamingListener
import io.micronaut.natsstreaming.annotation.Subject

@NatsStreamingListener
class NatsConsumer {

    @Subject("foo")
    fun listneing(@Body event: CustomerEvent.CustomerRegistered){
        println("Consumiu a mensage ${event.toString()}")
    }
}