package com.oleson.controllers

import com.oleson.services.ClassificationService
import io.micronaut.core.annotation.Blocking
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Blocking
@Controller("Classification")
class ClassificationController(
    private val classificationService: ClassificationService
) {

    @Blocking
    @Post("/createTable")
    fun createClassificationTable() : Flux<HttpResponse<*>> {
        return classificationService.createClassificationTable()
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
    }

    @Blocking
    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>> {
        return classificationService.getClassification(id)
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Post("/{id}/name/{name}")
    fun create(id: Int, name: String) : Mono<HttpResponse<*>> {
        return classificationService.createClassification(id, name)
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return classificationService.deleteClassification(id)
            .map { HttpResponse.ok("Deleted Classification $id") }
    }

    @Blocking
    @Get("/")
    fun getClassifications() : Mono<HttpResponse<*>> {
        return classificationService.getClassifications()
            .map { HttpResponse.ok(it) }
    }
}