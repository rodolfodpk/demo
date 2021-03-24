package com.example1

import com.example1.domain.customer.Customer
import com.example1.domain.customer.CustomerCommand
import com.example1.domain.customer.CustomerEvent
import com.example1.domain.customer.customerConfig
import io.github.crabzilla.core.BoundedContextName
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.pgc.CommandControllerFactory
import io.github.crabzilla.pgc.PgcOutboxPublisher
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.pgclient.PgPool
import javax.inject.Named
import javax.inject.Singleton

@Factory
private class AppFactory {

    @Bean
    @Singleton
    fun outboxPublisher(publisher: AppOutboxPublisher, @Named("writeDb") writeDb: PgPool): PgcOutboxPublisher {
        return PgcOutboxPublisher(boundedContextName.name, publisher, writeDb)
    }

    @Bean
    @Singleton
    fun customerCommandController(@Named("writeDb") writeDb: PgPool):
            CommandController<Customer, CustomerCommand, CustomerEvent> {
        return CommandControllerFactory.createPublishingTo(boundedContextName.name, customerConfig, writeDb)
    }

}
