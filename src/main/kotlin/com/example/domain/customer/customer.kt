package com.example.domain.customer

import com.example.domain.customer.CustomerCommand.*
import com.example.domain.customer.CustomerEvent.*
import io.github.crabzilla.core.AggregateRootConfig
import io.github.crabzilla.core.AggregateRootName
import io.github.crabzilla.core.CommandValidator
import io.github.crabzilla.core.EventHandler
import io.github.crabzilla.core.SnapshotTableName

/**
 * A command validator. You could use https://github.com/konform-kt/konform
 */
val customerCmdValidator = CommandValidator<CustomerCommand> { command ->
  when (command) {
    is RegisterCustomer -> listOf()
    is RegisterAndActivateCustomer -> listOf()
    is ActivateCustomer -> listOf()
    is DeactivateCustomer -> listOf()
  }
}

/**
 * This function will apply an event to customer state
 */
val customerEventHandler = EventHandler<Customer, CustomerEvent> { state, event ->
  when (event) {
    is CustomerRegistered -> Customer.create(id = event.id, name = event.name).state
    is CustomerActivated -> state!!.copy(isActive = true, reason = event.reason)
    is CustomerDeactivated -> state!!.copy(isActive = false, reason = event.reason)
  }
}

/**
 * Customer errors
 */
class CustomerAlreadyExists(val id: Int) : IllegalStateException("Customer $id already exists")

// TODO class CustomerEventDes : EventDeserializer<IntegrationEvent>

val customerConfig = AggregateRootConfig(
  AggregateRootName("Customer"), SnapshotTableName("customer_snapshots"),
  customerEventHandler, customerCmdValidator, CustomerCommandHandler, customerJson
)
