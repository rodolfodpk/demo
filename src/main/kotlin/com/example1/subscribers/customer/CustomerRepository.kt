package com.example1.subscribers.customer

import io.vertx.core.Future

interface CustomerRepository {
    fun upsert(id: Int, name: String, isActive: Boolean): Future<Void>
    fun updateStatus(id: Int, isActive: Boolean): Future<Void>
}