package com.example1.infra

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.pgclient.PgPool
import io.zero88.jooqx.ReactiveJooqx
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import javax.inject.Named
import javax.inject.Singleton


@Factory
class JooqFactory {

    @Bean
    @Singleton
    fun dsl(): DSLContext {
        return DSL.using(DefaultConfiguration().set(SQLDialect.POSTGRES))
    }

    @Bean
    @Singleton
    fun jooq(vertx: Vertx, dsl: DSLContext, @Named("readDb") readDb: PgPool): ReactiveJooqx {
        return ReactiveJooqx.builder().vertx(vertx)
            .dsl(dsl)
            .sqlClient(readDb)
            .build()
    }

}