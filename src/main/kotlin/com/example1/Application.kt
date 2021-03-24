package com.example1

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {

	build()
	    .args(*args)
			.eagerInitSingletons(true)
			.packages("com.example1")
		.start()
}

