package com.oleson

import io.micronaut.runtime.Micronaut.build

object Application {
	@JvmStatic
	fun main(args: Array<String>) {
		build().args(*args)
			.packages("com.oleson")
			.mainClass(Application.javaClass)
			.start()
	}
}
