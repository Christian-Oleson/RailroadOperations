package com.oleson.controllers

import com.oleson.models.dto.ReceiverType
import com.oleson.services.ReceiverService
import io.micronaut.core.annotation.Blocking
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Blocking
@Controller("Receiver")
class ReceiverController(
    private val receiverService: ReceiverService
) {

    @Blocking
    @Post("/createTable")
    fun createReceiverTable() : Flux<HttpResponse<*>> {
        return receiverService.createTable()
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Post("/{id}")
    fun create(id: Int) : Mono<HttpResponse<*>> {
        return receiverService.createReceiver(id)
            ?.map { HttpResponse.ok(it) } ?: Mono.error(Exception("Receiver not created"))
    }

    @Blocking
    @Get("/")
    fun getReceivers() : Mono<HttpResponse<*>> {
        return receiverService.getReceivers()
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>>? {
        val receiver = ReceiverType.fromInt(id);

        return receiver?.let { receiverService.getReceiver(it) }?.let {
            it.map { receiverType -> HttpResponse.ok(receiverType) }
        }
    }

    @Blocking
    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return receiverService.deleteReceiver(id)
            .map { HttpResponse.ok("Deleted Receiver $id") }
    }
}