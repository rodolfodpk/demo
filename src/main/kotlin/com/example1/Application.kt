package com.example1

import io.github.crabzilla.core.BoundedContextName
import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {

	build()
	    .args(*args)
			.eagerInitSingletons(true)
			.packages("com.example1")
		.start()
}

val boundedContextName =  BoundedContextName("example1")
