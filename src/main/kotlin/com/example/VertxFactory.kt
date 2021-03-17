package com.example

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus

@Factory
class VertxFactory {

    @Bean
    fun vertx(): Vertx {
        return Vertx.vertx()
    }

    @Bean
    fun eventbus(vertx: Vertx): EventBus {
        return vertx.eventBus()
    }

}