package com.example

import com.example.domain.customer.Customer
import com.example.domain.customer.CustomerCommand
import com.example.domain.customer.CustomerEvent
import com.example.domain.customer.CustomerReadModelProjector
import io.github.crabzilla.core.CommandController
import io.github.crabzilla.core.CommandMetadata
import io.github.crabzilla.core.Either
import io.github.crabzilla.core.StatefulSession
import io.github.crabzilla.pgc.PgcEventsPublisher
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.reactivex.Single
import javax.inject.Inject
import kotlin.random.Random

@Controller("/hello")
class CustomerController(@Inject private val controller: CommandController<Customer, CustomerCommand, CustomerEvent>) {

    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): Single<String> {
        val id = Random(1000).nextInt()
        val metadata = CommandMetadata(id)
        val command = CustomerCommand.RegisterCustomer(id, "customer#$id")
        return Single.create { emitter ->
            controller.handle(metadata, command)
                    .onFailure {
                        println("Deu ruim")
                        it.printStackTrace()
                        emitter.onError(it)
                    }
                    .onSuccess { ok: Either<List<String>, StatefulSession<Customer, CustomerEvent>> ->
                        println("Deu bôa")
                        when (ok) {
                            is Either.Left -> {
                                println("Validation error: " + ok.value.toString())
                                emitter.onSuccess(ok.value.toString())
                            }
                            is Either.Right -> {
                                println("Deu bôa mesmo: " + ok.value.currentState)
                                emitter.onSuccess(ok.value.currentState.toString())
                            }
                        }
                    }
        }
    }

}