package com.example.nats.connection

import io.micronaut.context.annotation.Value
import io.nats.streaming.Options
import java.util.*
import javax.inject.Singleton

@Singleton
 class NatsStreamingConnectionConfig{

    val NATS_PROTOCOL = "nats://"

    @Value("\${nats.host}")
    private lateinit var host :String

    @Value("\${nats.port}")
    private var port :Int = 4222

    @Value("\${nats.user}")
    private lateinit var user :String

    @Value("\${nats.password}")
    private lateinit var password :String

    @Value("\${nats.client-id}")
    private lateinit var clientId :String

    @Value("\${nats.cluster-id}")
    private lateinit var clusterId :String


    fun toOptions(): Options? {
        return Options.Builder()
                .clusterId(clusterId)
                .clientId(clientId)
                .natsUrl(getUrl())
                .build()
    }

    private fun getUrl(): String? {
        var url: String? = null
        url = if (Objects.nonNull(user) || Objects.nonNull(password)) {
            "$NATS_PROTOCOL$user:$password@$host:$port"
        } else {
            "$NATS_PROTOCOL$host:$port"
        }
        return url
    }
}