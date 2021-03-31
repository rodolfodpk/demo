package com.example1.core.customer

import io.github.crabzilla.core.AggregateRoot
import io.github.crabzilla.core.CommandHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Customer aggregate root
 */
@Serializable
@SerialName("Customer")
data class Customer (
  val id: Int,
  val name: String,
  val isActive: Boolean = false,
  val reason: String? = null
) : AggregateRoot() {

  companion object {
    fun create(id: Int, name: String): CommandHandler.ConstructorResult<Customer, CustomerEvent> {
      return CommandHandler.ConstructorResult(
          Customer(id = id, name = name),
        CustomerEvent.CustomerRegistered(id = id, name = name)
      )
    }
  }

  fun activate(reason: String): List<CustomerEvent> {
    return listOf(CustomerEvent.CustomerActivated(reason))
  }

  fun deactivate(reason: String): List<CustomerEvent> {
    return listOf(CustomerEvent.CustomerDeactivated(reason))
  }
}