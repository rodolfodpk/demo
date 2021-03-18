package com.example.domain.customer

import io.github.crabzilla.core.EventPublisher
import io.vertx.core.Future
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * To update customer read model given events
 */
@Singleton
@Named("read-model-publisher")
class CustomerReadModelPublisher(private val repo: CustomerRepository) : EventPublisher<CustomerEvent> {

  companion object {
    private val log = LoggerFactory.getLogger(CustomerNatsPublisher::class.java)
  }

  override fun publish(id: Int, event: CustomerEvent): Future<Void> {
    log.info("Will publish event $event to read model")
    return when (event) {
      is CustomerEvent.CustomerRegistered -> repo.upsert(id, event.name, false)
      is CustomerEvent.CustomerActivated -> repo.updateStatus(id, true)
      is CustomerEvent.CustomerDeactivated -> repo.updateStatus(id, false)
    }
  }
}