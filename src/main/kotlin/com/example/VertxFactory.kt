package com.example

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import javax.inject.Singleton

@Factory
class VertxFactory {

    @Bean
    @Singleton
    fun vertx(): Vertx {
        return Vertx.vertx()
    }

    @Bean
    @Singleton
    fun eventbus(vertx: Vertx): EventBus {
        return vertx.eventBus()
    }

}