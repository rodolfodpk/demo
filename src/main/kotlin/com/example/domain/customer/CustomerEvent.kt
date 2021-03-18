package com.example.domain.customer

import io.github.crabzilla.core.DomainEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Customer events
 */
@Serializable
sealed class CustomerEvent : DomainEvent() {
  @Serializable
  @SerialName("CustomerRegistered")
  data class CustomerRegistered(val id: Int, val name: String) : CustomerEvent()

  @Serializable
  @SerialName("CustomerActivated")
  data class CustomerActivated(val reason: String) : CustomerEvent()

  @Serializable
  @SerialName("CustomerDeactivated")
  data class CustomerDeactivated(val reason: String) : CustomerEvent()
}