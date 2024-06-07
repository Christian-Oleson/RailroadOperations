package com.oleson.controllers

import com.oleson.models.dto.ReceiverType
import com.oleson.services.ReceiverService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import reactor.core.publisher.Mono

@Controller("Receiver")
class ReceiverController(
    private val receiverService: ReceiverService
) {

    @Post("/createTable")
    fun createReceiverTable() : Mono<HttpResponse<*>> {
        return Mono.just(receiverService.createTable())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(Exception("Receiver table not created")))
    }

    @Post("/{id}")
    fun create(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(receiverService.createReceiver(id))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(Exception("Receiver not created")))
    }

    @Get("/")
    fun getReceivers() : Mono<HttpResponse<*>> {
        return Mono.just(receiverService.getReceivers())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(Exception("No receivers found")))
    }

    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>>? {
        val receiver = ReceiverType.fromInt(id);

        return receiver?.let { receiverService.getReceiver(it) }?.let {
            Mono.just(it)
                .map<HttpResponse<*>> { HttpResponse.ok(it) }
                .switchIfEmpty(Mono.error(Exception("Receiver not found")))
        }
    }

    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(receiverService.deleteReceiver(id))
            .map<HttpResponse<*>> { HttpResponse.ok("Deleted Receiver $id") }
            .switchIfEmpty(Mono.error(Exception("Receiver not deleted")))
    }
}