package com.example

import com.example.domain.customer.Customer
import com.example.domain.customer.CustomerCommand
import com.example.domain.customer.CustomerEvent
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
import io.github.crabzilla.core.Either
import io.github.crabzilla.core.StatefulSession
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.reactivex.Single
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@Controller("/hello")
class CustomerController(@Inject private val controller: CommandController<Customer, CustomerCommand, CustomerEvent>) {

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
        val command = CustomerCommand.RegisterCustomer(newId, "customer#$id")
        return Single.create { emitter ->
            controller.handle(metadata, command)
                .onFailure {
                    log.error("Deu ruim", it)
                    emitter.onError(it)
                }
                .onSuccess { result: CustomerCommandResult ->
                    log.info("Deu bôa?")
                    when (result) {
                        is Either.Left -> {
                            val validationErrors: List<String> = result.value
                            log.warn("Validation errors: $validationErrors")
                            emitter.onError(IllegalArgumentException(validationErrors.toString()))
                        }
                        is Either.Right -> {
                            val session: StatefulSession<Customer, CustomerEvent> = result.value
                            log.info("Deu bôa mesmo: ${session.currentState}")
                            val response = """
                                    Original version: ${session.originalVersion}
                                    New version: ${session.originalVersion + 1}
                                    New state: ${session.currentState}
                                    Events: ${session.appliedEvents()} 
                                """
                            emitter.onSuccess(response)
                        }
                    }
                }
        }
    }

}

typealias CustomerCommandResult = Either<List<String>, StatefulSession<Customer, CustomerEvent>>