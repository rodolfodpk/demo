package com.example.nats.connection

import io.micronaut.context.annotation.Factory
import io.nats.streaming.StreamingConnection
import io.nats.streaming.StreamingConnectionFactory
import javax.inject.Singleton

@Factory
class NatsConnection {

    @Singleton
    fun createNatConnection(natsStreamingConnectionConfig: NatsStreamingConnectionConfig): StreamingConnection? {
        val options = natsStreamingConnectionConfig.toOptions()
        val cf = StreamingConnectionFactory(options)
        return try {
            cf.createConnection()
        } catch (e: Exception) {
            throw RuntimeException("Fail to connect to the server", e)
        }
    }

}