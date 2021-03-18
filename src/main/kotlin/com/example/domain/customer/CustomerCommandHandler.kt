package com.example.domain.customer

import io.github.crabzilla.core.CommandHandler
import io.github.crabzilla.core.Snapshot
import io.github.crabzilla.core.StatefulSession
import org.slf4j.LoggerFactory

/**
 * Customer command handler
 */
object CustomerCommandHandler : CommandHandler<Customer, CustomerCommand, CustomerEvent> {

    private val log = LoggerFactory.getLogger(CustomerCommandHandler::class.java)
    
    override fun handleCommand(command: CustomerCommand, snapshot: Snapshot<Customer>?):
            Result<StatefulSession<Customer, CustomerEvent>> {
        log.info("vai processar commando $command")
        val result = runCatching {
            when (command) {

                is CustomerCommand.RegisterCustomer -> {
                    if (snapshot == null)
                        with(Customer.create(id = command.customerId, name = command.name), customerEventHandler)
                    else throw CustomerAlreadyExists(command.customerId)
                }

                is CustomerCommand.RegisterAndActivateCustomer -> {
                    if (snapshot == null)
                        with(Customer.create(id = command.customerId, name = command.name), customerEventHandler)
                                .execute { it.activate(command.reason) }
                    else throw CustomerAlreadyExists(command.customerId)
                }

                is CustomerCommand.ActivateCustomer -> {
                    with(snapshot!!, customerEventHandler)
                            .execute { it.activate(command.reason) }
                }

                is CustomerCommand.DeactivateCustomer -> {
                    with(snapshot!!, customerEventHandler)
                            .execute { it.deactivate(command.reason) }
                }
            }
        }
        log.info("result $result")
        return result
    }
}