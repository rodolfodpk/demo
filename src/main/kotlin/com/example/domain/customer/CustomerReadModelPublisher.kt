package com.example.domain.customer

import io.github.crabzilla.core.EventPublisher
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple
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
    private val log = LoggerFactory.getLogger(CustomerReadModelPublisher::class.java)
  }

  init {
      log.info("I'm up")
  }

  override fun publish(id: Int, event: CustomerEvent): Future<Void> {
    log.info("Will publish event $event to read model")
    return when (event) {
      is CustomerEvent.CustomerRegistered -> repo.upsert(id, event.name, false)
      is CustomerEvent.CustomerActivated -> repo.updateStatus(id, true)
      is CustomerEvent.CustomerDeactivated -> repo.updateStatus(id, false)
    }
  }

  /**
   * Read model repository
   */
  @Singleton
  class CustomerRepository(@Named("readDb") private val pool: PgPool) {

    fun upsert(id: Int, name: String, isActive: Boolean): Future<Void> {
      val promise = Promise.promise<Void>()
      pool
        .preparedQuery(
          "INSERT INTO customer_summary (id, name, is_active) VALUES ($1, $2, $3) ON CONFLICT (id) DO UPDATE " +
                  "SET name = $2, is_active = $3"
        )
        .execute(Tuple.of(id, name, isActive)) { ar ->
          if (ar.succeeded()) {
            val rows: RowSet<Row> = ar.result()
            println("Got " + rows.size().toString() + " rows ")
            promise.complete()
          } else {
            println("Failure: " + ar.cause().message)
            promise.fail(ar.cause().message)
          }
        }
      return promise.future()
    }

    fun updateStatus(id: Int, isActive: Boolean): Future<Void> {
      val promise = Promise.promise<Void>()
      pool
        .preparedQuery("UPDATE customer_summary set is_active = $2 where id = $1")
        .execute(Tuple.of(id)) { ar ->
          if (ar.succeeded()) {
            val rows: RowSet<Row> = ar.result()
            println("Got " + rows.size().toString() + " rows ")
            promise.complete()
          } else {
            println("Failure: " + ar.cause().message)
            promise.fail(ar.cause().message)
          }
        }
      return promise.future()
    }
  }


}

