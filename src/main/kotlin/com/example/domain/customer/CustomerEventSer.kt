package com.example.domain.customer

import io.github.crabzilla.core.EventSerializer
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 * To export domain events into integration events
 */

class CustomerEventSer : EventSerializer<CustomerEvent> {
  override fun toJson(e: CustomerEvent): Result<JsonObject> {
    return runCatching {
      when (e) {
        is CustomerEvent.CustomerRegistered -> jsonObjectOf(Pair("companyId", e.id), Pair("name", e.name))
        is CustomerEvent.CustomerActivated -> jsonObjectOf(Pair("reason", e.reason))
        is CustomerEvent.CustomerDeactivated -> jsonObjectOf(Pair("reason", e.reason))
      }
    }
  }
}