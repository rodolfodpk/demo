package com.example.domain.customer

import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
import io.github.crabzilla.core.Either
import io.github.crabzilla.core.SessionData
import io.github.crabzilla.core.StatefulSession
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Single
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@Controller("/hello")
class CustomerController(@Inject private val controller: CommandController<Customer, CustomerCommand, CustomerEvent>) {

    companion object {
        private val log = LoggerFactory.getLogger(CustomerController::class.java)
    }

    val id = AtomicInteger()

    @Get("/")
    fun index(): Single<SessionData> {
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
                            val session = result.value.toSessionData()
                            log.info("Deu bôa mesmo: $session")
                            emitter.onSuccess(session)
                        }
                    }
                }
        }
    }

}

typealias CustomerCommandResult = Either<List<String>, StatefulSession<Customer, CustomerEvent>>
