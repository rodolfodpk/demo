package com.example1.core.customer

import com.example1.core.customer.CustomerCommand.ActivateCustomer
import com.example1.core.customer.CustomerCommand.DeactivateCustomer
import com.example1.core.customer.CustomerCommand.RegisterAndActivateCustomer
import com.example1.core.customer.CustomerCommand.RegisterCustomer
import com.example1.core.customer.CustomerEvent.CustomerActivated
import com.example1.core.customer.CustomerEvent.CustomerDeactivated
import com.example1.core.customer.CustomerEvent.CustomerRegistered
import com.example1.core.customer.CustomerSerialization.customerJson
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

val customerConfig = AggregateRootConfig(
  AggregateRootName("Customer"),
  SnapshotTableName("customer_snapshots"),
  customerEventHandler,
  customerCmdValidator,
  CustomerCommandHandler,
  customerJson
)
