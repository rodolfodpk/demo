package com.example1.application

import com.example1.core.customer.Customer
import com.example1.core.customer.CustomerCommand
import com.example1.core.customer.CustomerEvent
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
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
        log.info("*** Will generate a new command $newId")
        val metadata = CommandMetadata(newId)
        val command = CustomerCommand.RegisterCustomer(newId, "customer#$id")
        return Single.create { emitter ->
            controller.handle(metadata, command)
                .onFailure {
                    log.error("Error", it)
                    emitter.onError(it)
                }
                .onSuccess { session: StatefulSession<Customer, CustomerEvent> ->
                    log.info("Result: ${session.toSessionData()}")
                    emitter.onSuccess(session.toSessionData())
                }
        }
    }

}
