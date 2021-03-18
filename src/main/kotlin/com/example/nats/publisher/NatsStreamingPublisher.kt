package com.example.nats.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import io.nats.streaming.AckHandler
import io.nats.streaming.StreamingConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NatsStreamingPublisher {
    @Inject
    private lateinit var connection: StreamingConnection

    @Inject
    private lateinit var mapper: ObjectMapper

    fun <T> publish(subject: String?, paylod: T, akHandler: AckHandler? = null) {
        try {
            val json = mapper!!.writeValueAsString(paylod)
            println("Json gerado payload=$json")
            connection!!.publish(subject, json.toByteArray(), akHandler)
        } catch (e: Exception) {
            throw RuntimeException("Faill to send message", e)
        }
    }
}