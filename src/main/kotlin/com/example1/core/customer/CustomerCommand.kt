package com.example1.core.customer

import io.github.crabzilla.core.Command
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Customer commands
 */
@Serializable
sealed class CustomerCommand : Command() {
  @Serializable
  @SerialName("RegisterCustomer")
  data class RegisterCustomer(val customerId: Int, val name: String) : CustomerCommand()

  @Serializable
  @SerialName("ActivateCustomer")
  data class ActivateCustomer(val reason: String) : CustomerCommand()

  @Serializable
  @SerialName("DeactivateCustomer")
  data class DeactivateCustomer(val reason: String) : CustomerCommand()

  @Serializable
  @SerialName("RegisterAndActivateCustomer")
  data class RegisterAndActivateCustomer(val customerId: Int, val name: String, val reason: String) : CustomerCommand()
}