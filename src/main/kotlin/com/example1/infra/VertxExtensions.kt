package com.example1.infra

import io.vertx.core.AsyncResult
import io.vertx.core.Promise

fun <T> AsyncResult<T>.handle(promise: Promise<T>) {
    return if (this.failed()) promise.fail(this.cause()) else promise.complete(this.result())
}

fun <T> AsyncResult<T>.handleVoid(promise: Promise<Void>) {
    return if (this.failed()) promise.fail(this.cause()) else promise.complete()
}
