package com.example1.subscribers.customer

import com.example.read.tables.CustomerSummary
import com.example1.application.infra.handleVoid
import io.vertx.core.Future
import io.vertx.core.Promise
import io.zero88.jooqx.DSLAdapter
import io.zero88.jooqx.ReactiveJooqx
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Named("jooq-style")
class JooqCustomerRepository(private val jooqx: ReactiveJooqx) : CustomerRepository {

    override fun upsert(id: Int, name: String, isActive: Boolean): Future<Void> {
        val promise = Promise.promise<Void>()
        val sql = jooqx.dsl()
            .insertInto(CustomerSummary.CUSTOMER_SUMMARY)
            .columns(CustomerSummary.CUSTOMER_SUMMARY.ID, CustomerSummary.CUSTOMER_SUMMARY.NAME, CustomerSummary.CUSTOMER_SUMMARY.IS_ACTIVE)
            .values(id, name, isActive)
            .onDuplicateKeyUpdate()
            .set(CustomerSummary.CUSTOMER_SUMMARY.NAME, name)
            .set(CustomerSummary.CUSTOMER_SUMMARY.IS_ACTIVE, isActive)
        jooqx.execute(sql, DSLAdapter.fetchOne(CustomerSummary.CUSTOMER_SUMMARY)) { it.handleVoid(promise) }
        return promise.future()
    }

    override fun updateStatus(id: Int, isActive: Boolean): Future<Void> {
        val promise = Promise.promise<Void>()
        val sql = jooqx.dsl()
            .update(CustomerSummary.CUSTOMER_SUMMARY)
            .set(CustomerSummary.CUSTOMER_SUMMARY.IS_ACTIVE, isActive)
            .where(CustomerSummary.CUSTOMER_SUMMARY.ID.eq(id))
        jooqx.execute(sql, DSLAdapter.fetchOne(CustomerSummary.CUSTOMER_SUMMARY)) { it.handleVoid(promise) }
        return promise.future()
    }

}