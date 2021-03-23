package com.example.domain.customer

import com.example.domain.customer.CustomerEvent.CustomerActivated
import com.example.domain.customer.CustomerEvent.CustomerDeactivated
import com.example.domain.customer.CustomerEvent.CustomerRegistered
import com.example.infra.handleVoid
import com.example.infra.privateTopic
import com.example.read.tables.CustomerSummary.CUSTOMER_SUMMARY
import io.github.crabzilla.core.DOMAIN_EVENT_SERIALIZER
import io.nats.streaming.Message
import io.nats.streaming.StreamingConnection
import io.nats.streaming.SubscriptionOptions
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Tuple
import io.zero88.jooqx.DSLAdapter
import io.zero88.jooqx.ReactiveJooqx
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * To update customer read model given events.
 * Single writer?
 */
@Singleton
class CustomerSubscriber(nats: StreamingConnection, @Named("jooq-style") private val repo: ICustomerRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerSubscriber::class.java)
    }

    init {
        val opt = SubscriptionOptions.Builder().deliverAllAvailable().build()
        log.info("I'm up and subscribing to ${privateTopic(customerConfig.name)}")
        nats.subscribe(privateTopic(customerConfig.name),  { msg: Message ->
            log.info("I received $msg")
            val asJson = JsonObject(String(msg.data))
            log.info("Message: ${asJson.encodePrettily()}")
            val metadata = asJson.getJsonObject("metadata")
            val aggregateId = metadata.getInteger("aggregateId")
            val eventId = metadata.getLong("eventId")
            val eventAsJson = asJson.getJsonObject("eventPayload")
            consume(eventId, aggregateId, eventAsJson)
        }, opt)
    }

    private fun consume(eventId: Long, id: Int, eventAsJson: JsonObject): Future<Void> {
        val event = customerJson.decodeFromString(DOMAIN_EVENT_SERIALIZER, eventAsJson.toString()) as CustomerEvent
        log.info("Will publish event $event to read model")
        return when (event) {
            is CustomerRegistered -> repo.upsert(id, event.name, false)
            is CustomerActivated -> repo.updateStatus(id, true)
            is CustomerDeactivated -> repo.updateStatus(id, false)
        }
    }

    interface ICustomerRepository {
        fun upsert(id: Int, name: String, isActive: Boolean): Future<Void>
        fun updateStatus(id: Int, isActive: Boolean): Future<Void>
    }

    /**
     * Read model repository
     */
    @Singleton
    @Named("pg-client-style")
    class PgClientCustomerRepository(@Named("readDb") private val pool: PgPool) : ICustomerRepository {

        override fun upsert(id: Int, name: String, isActive: Boolean): Future<Void> {
            val promise = Promise.promise<Void>()
            pool.preparedQuery("INSERT INTO customer_summary (id, name, is_active) VALUES ($1, $2, $3) " +
                    "ON CONFLICT (id) DO UPDATE SET name = $2, is_active = $3")
                .execute(Tuple.of(id, name, isActive)) { it.handleVoid(promise) }
            return promise.future()
        }

        override fun updateStatus(id: Int, isActive: Boolean): Future<Void> {
            val promise = Promise.promise<Void>()
            pool.preparedQuery("UPDATE customer_summary set is_active = $2 where id = $1")
                .execute(Tuple.of(id)) { it.handleVoid(promise) }
            return promise.future()
        }
    }

    @Singleton
    @Named("jooq-style")
    class JooqCustomerRepository(val jooqx: ReactiveJooqx) : ICustomerRepository {

        override fun upsert(id: Int, name: String, isActive: Boolean): Future<Void> {
            val promise = Promise.promise<Void>()
            val sql = jooqx.dsl()
                .insertInto(CUSTOMER_SUMMARY)
                .columns(CUSTOMER_SUMMARY.ID, CUSTOMER_SUMMARY.NAME, CUSTOMER_SUMMARY.IS_ACTIVE)
                .values(id, name, isActive)
                .onDuplicateKeyUpdate()
                .set(CUSTOMER_SUMMARY.NAME, name)
                .set(CUSTOMER_SUMMARY.IS_ACTIVE, isActive)
            jooqx.execute(sql, DSLAdapter.fetchOne(CUSTOMER_SUMMARY)) { it.handleVoid(promise) }
            return promise.future()
        }

        override fun updateStatus(id: Int, isActive: Boolean): Future<Void> {
            val promise = Promise.promise<Void>()
            val sql = jooqx.dsl()
                .update(CUSTOMER_SUMMARY)
                .set(CUSTOMER_SUMMARY.IS_ACTIVE, isActive)
                .where(CUSTOMER_SUMMARY.ID.eq(id))
            jooqx.execute(sql, DSLAdapter.fetchOne(CUSTOMER_SUMMARY)) { it.handleVoid(promise) }
            return promise.future()
        }

    }

}

