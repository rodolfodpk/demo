package com.example.domain.customer

import io.github.crabzilla.core.CommandController
import io.github.crabzilla.pgc.CommandControllerFactory
import io.github.crabzilla.pgc.PgcEventsJsonPublisher
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.pgclient.PgPool
import javax.inject.Named
import javax.inject.Singleton

@Factory
private class CustomerComponentsFactory {


    @Bean
    @Singleton
    fun controller(@Named("writeDb") writeDb: PgPool): CommandController<Customer, CustomerCommand, CustomerEvent> {
        return CommandControllerFactory.create(customerConfig, writeDb)
    }

    @Bean
    @Singleton
    @Named("read-model")
    fun readModelPublisher(@Named("writeDb") writeDb: PgPool, readModelPublisher: CustomerDomainEventsPublisher)
    : PgcEventsJsonPublisher {
        return PgcEventsJsonPublisher(readModelPublisher, customerConfig.name, writeDb)
    }

    @Bean
    @Singleton
    @Named("nats")
    fun natsPublisher(@Named("writeDb") writeDb: PgPool, natsPublisher: CustomerIntegrationEventsPublisher)
            : PgcEventsJsonPublisher {
        return PgcEventsJsonPublisher(natsPublisher, customerConfig.name, writeDb)
    }


}
