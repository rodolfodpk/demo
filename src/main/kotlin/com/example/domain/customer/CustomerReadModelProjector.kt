package com.example.domain.customer

import com.example.domain.customer.CustomerEvent.*
import io.github.crabzilla.core.EventPublisher
import io.vertx.core.Future
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * To update customer read model given events
 */
@Singleton
@Named("read-model")
class CustomerReadModelProjector(@Inject private val repo: CustomerRepository) : EventPublisher<CustomerEvent> {
  override fun publish(id: Int, event: CustomerEvent): Future<Void> {
    return when (event) {
      is CustomerRegistered -> repo.upsert(id, event.name, false)
      is CustomerActivated -> repo.updateStatus(id, true)
      is CustomerDeactivated -> repo.updateStatus(id, false)
    }
  }
}

