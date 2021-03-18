package com.example.domain.customer

import com.example.localReply
import io.github.crabzilla.core.Command
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
import io.github.crabzilla.core.Either
import io.github.crabzilla.core.StatefulSession
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CustomerService(
    @Inject private val eventBus: EventBus,
    @Inject private val controller: CommandController<Customer, CustomerCommand, CustomerEvent>
) {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerService::class.java)
    }

    @PostConstruct
    fun register() {
        eventBus.localConsumer<Pair<CommandMetadata, Command>>("customer-command-handler") { message ->
            log.info("Received ${message.body()}")
            controller.handle(message.body().first, message.body().second as CustomerCommand)
                .onFailure {
                    log.info("Deu ruim ${it.message}")
                    it.printStackTrace()
                    message.fail(500, it.cause?.message ?: "I'm sorry")
                }
                .onSuccess { result: Either<List<String>, StatefulSession<Customer, CustomerEvent>> ->
                    log.info("Deu bôa ?")
                    message.localReply(result)
                }
        }
    }

}