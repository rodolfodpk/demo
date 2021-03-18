package com.example

import com.example.domain.customer.Customer
import com.example.domain.customer.CustomerCommand
import com.example.domain.customer.CustomerEvent
import com.example.domain.customer.CustomerService
import io.github.crabzilla.core.Command
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
import io.github.crabzilla.core.Either
import io.github.crabzilla.core.StatefulSession
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.reactivex.Single
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@Controller("/hello")
class CustomerController(@Inject private val eventBus: EventBus) {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerController::class.java)
    }

    val id = AtomicInteger()

    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): Single<String> {
        val newId = id.incrementAndGet()
        log.info("Vai gerar um comando $newId")
        val metadata = CommandMetadata(newId)
        val command = CustomerCommand.RegisterCustomer(newId, "customer#$newId")
        return Single.create { emitter ->
            eventBus.localRequest<CustomerCommandResult>("customer-command-handler", Pair(metadata, command)) {
                if (it.failed()) {
                    emitter.onError(it.cause())
                    return@localRequest
                }
                when (val result = it.result().body()) {
                    is Either.Left -> {
                        log.info("Validation error: " + result.value.toString())
                        emitter.onError(IllegalArgumentException(result.value.toString()))
                    }
                    is Either.Right -> {
                        log.info("Deu bôa mesmo: " + result.value.currentState)
                        emitter.onSuccess(result.value.currentState.toString())
                    }
                }
            }
        }
    }
}

typealias CustomerCommandResult = Either<List<String>, StatefulSession<Customer, CustomerEvent>>

