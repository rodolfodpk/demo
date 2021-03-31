package com.example1.application

import com.example1.core.customer.Customer
import com.example1.core.customer.CustomerCommand
import com.example1.core.customer.CustomerEvent
import com.example1.core.customer.customerConfig
import com.example1.publisher.AppEventsPublisher
import com.example1.subscribers.customer.CustomerProjectorVerticle
import io.github.crabzilla.core.BoundedContextName
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.pgc.CommandControllerFactory
import io.github.crabzilla.pgc.PgcEventsScanner
import io.github.crabzilla.pgc.PgcPoolingPublisherVerticle
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.pgclient.PgPool
import javax.inject.Named

@Factory
private class AppFactory {

    val boundedContextName = BoundedContextName("example1")

    @Bean
    @Context
    fun eventsPublisherVerticle(vertx: Vertx,
                                appEventsPublisher: AppEventsPublisher,
                                @Named("writeDb") writeDb: PgPool,
                                customerProjectorVerticle: CustomerProjectorVerticle
    ):
            PgcPoolingPublisherVerticle {
        val eventScanner = PgcEventsScanner(writeDb)
        val verticle = PgcPoolingPublisherVerticle(eventScanner, appEventsPublisher)
        vertx.deployVerticle(verticle)
        vertx.deployVerticle(customerProjectorVerticle)
        return verticle
    }

    @Bean
    @Context
    fun customerCommandController(@Named("writeDb") writeDb: PgPool):
            CommandController<Customer, CustomerCommand, CustomerEvent> {
        return CommandControllerFactory.createPublishingTo(boundedContextName.name, customerConfig, writeDb)
    }

}
