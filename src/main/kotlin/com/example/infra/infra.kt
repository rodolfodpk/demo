package com.example.infra

import io.github.crabzilla.core.AggregateRootName
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf

fun privateTopic(name: AggregateRootName?): String {
    return "private.events.${name?.value ?: "*"}".toLowerCase()
}
fun publicTopic(name: AggregateRootName?): String {
    return "public.events.${name?.value ?: "*"}".toLowerCase()
}
fun wrapMessage(eventId: Long, id: Int, eventAsJson: JsonObject): ByteArray? {
    val metadata = jsonObjectOf(Pair("eventId", eventId), Pair("aggregateId", id))
    return jsonObjectOf(Pair("metadata", metadata), Pair("eventPayload", eventAsJson)).toBuffer().bytes
}