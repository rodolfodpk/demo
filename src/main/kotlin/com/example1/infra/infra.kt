package com.example1.infra

import io.github.crabzilla.core.EventRecord
import io.vertx.kotlin.core.json.jsonObjectOf

fun wrapMessage(eventRecord: EventRecord): ByteArray? {
    val metadata = jsonObjectOf(
        Pair("eventId", eventRecord.eventId),
        Pair("aggregateName", eventRecord.aggregateName),
        Pair("aggregateId", eventRecord.aggregateId))
    return jsonObjectOf(
        Pair("metadata", metadata),
        Pair("eventPayload", eventRecord.eventAsjJson))
        .toBuffer().bytes
}