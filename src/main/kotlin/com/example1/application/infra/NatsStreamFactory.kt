package com.example1.application.infra

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Value
import io.nats.streaming.Options
import io.nats.streaming.StreamingConnection
import io.nats.streaming.StreamingConnectionFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Factory
class NatsStreamFactory {

    private val connections = AtomicInteger(0)

    @Prototype
    fun createNatConnection(config: NatsStreamingConfig): StreamingConnection {
        config.clientId = "nats-connection-${connections.incrementAndGet()}"
        val cf = StreamingConnectionFactory(config.toOptions())
        return try {
            cf.createConnection()
        } catch (e: Exception) {
            throw RuntimeException("Fail to connect to the server", e)
        }
    }

    @Singleton
    class NatsStreamingConfig{

        val NATS_PROTOCOL = "nats://"

        @Value("\${nats.host}")
        lateinit var host :String

        @Value("\${nats.port}")
        var port :Int = 4222

        @Value("\${nats.user}")
        lateinit var user :String

        @Value("\${nats.password}")
        lateinit var password :String

        @Value("\${nats.client-id}")
        lateinit var clientId :String

        @Value("\${nats.cluster-id}")
        lateinit var clusterId :String


        fun toOptions(): Options? {
            return Options.Builder()
                .clusterId(clusterId)
                .clientId(clientId)
                .natsUrl(getUrl())
                .build()
        }

        fun getUrl(): String? {
            var url: String? = null
            url = if (Objects.nonNull(user) || Objects.nonNull(password)) {
                "$NATS_PROTOCOL$user:$password@$host:$port"
            } else {
                "$NATS_PROTOCOL$host:$port"
            }
            return url
        }
    }

}