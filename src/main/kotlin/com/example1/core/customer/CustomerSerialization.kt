package com.example1.core.customer

import io.github.crabzilla.core.AggregateRoot
import io.github.crabzilla.core.Command
import io.github.crabzilla.core.DomainEvent
import io.github.crabzilla.core.javaModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object CustomerSerialization {

  /**
   * kotlinx.serialization
   */
  @kotlinx.serialization.ExperimentalSerializationApi
  val customerJsonModule = SerializersModule {
    include(javaModule)
    polymorphic(DomainEvent::class) {
      subclass(CustomerEvent.CustomerRegistered::class, CustomerEvent.CustomerRegistered.serializer())
      subclass(CustomerEvent.CustomerActivated::class, CustomerEvent.CustomerActivated.serializer())
      subclass(CustomerEvent.CustomerDeactivated::class, CustomerEvent.CustomerDeactivated.serializer())
    }
    polymorphic(AggregateRoot::class) {
      subclass(Customer::class, Customer.serializer())
    }
    polymorphic(Command::class) {
      subclass(CustomerCommand.RegisterCustomer::class, CustomerCommand.RegisterCustomer.serializer())
      subclass(CustomerCommand.ActivateCustomer::class, CustomerCommand.ActivateCustomer.serializer())
      subclass(CustomerCommand.DeactivateCustomer::class, CustomerCommand.DeactivateCustomer.serializer())
      subclass(CustomerCommand.RegisterAndActivateCustomer::class, CustomerCommand.RegisterAndActivateCustomer.serializer())
    }
  }

  val customerJson = Json { serializersModule = customerJsonModule }

}
