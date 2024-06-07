package com.oleson.controllers

import com.oleson.exceptions.ClassificationException
import com.oleson.services.ClassificationService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import reactor.core.publisher.Mono

@Controller("Classification")
class ClassificationController(
    private val classificationService: ClassificationService
) {

    @Post("/createTable")
    fun createClassificationTable() : Mono<HttpResponse<*>> {
        return Mono.just(classificationService.createClassificationTable())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(ClassificationException("Classification table not created")))
    }

    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(classificationService.getClassification(id))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(ClassificationException("Classification $id not found")))
    }

    @Post("/{id}/name/{name}")
    fun create(id: Int, name: String) : Mono<HttpResponse<*>> {
        return Mono.just(classificationService.createClassification(id, name))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(ClassificationException("Classification not created")))
    }

    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(classificationService.deleteClassification(id))
            .map<HttpResponse<*>> { HttpResponse.ok("Deleted Classification $id") }
            .switchIfEmpty(Mono.error(ClassificationException("Could not delete classification $id")))
    }

    @Get("/")
    fun getClassifications() : Mono<HttpResponse<*>> {
        return Mono.just(classificationService.getClassifications())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(ClassificationException("Classifications not found")))
    }
}