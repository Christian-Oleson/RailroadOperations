package com.oleson.controllers

import com.oleson.exceptions.TrainException
import com.oleson.models.dto.RailCar
import com.oleson.models.dto.Train
import com.oleson.services.TrainService
import io.micronaut.core.annotation.Blocking
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import reactor.core.publisher.Mono

@Blocking
@Controller("Train")
class TrainController(
    private val trainService: TrainService
) {

    @Blocking
    @Get("/")
    fun getAll() : Mono<HttpResponse<*>> {
        return trainService.getAllTrains()
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>> {
        return trainService.getTrain(id)
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Post("/createTable")
    fun createRailroadCarTable() : Mono<HttpResponse<*>> {
        return Mono.just(trainService.createTrainTable())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }

    @Blocking
    @Post("/")
    @Put("/")
    fun create(@Body train: Train) : Mono<HttpResponse<*>> {
        if (train.name.isBlank() || train.destination.isBlank()) {
            return Mono.error(TrainException("Train name and destination must be provided"))
        }

        return trainService.upsertTrain(train)
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Post("/{id}/car")
    @Put("/{id}/car")
    fun attachCar(@Body car: RailCar, id: Int) : Mono<HttpResponse<*>> {
        return trainService.attachCar(car, id)
            .map { HttpResponse.ok(it) }
    }

    @Blocking
    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return trainService.deleteTrain(id)
            .map { HttpResponse.ok("Deleted Train $id") }
    }

    @Blocking
    @Delete("/{id}/car/{carId}")
    fun deleteCar(id: Int, carId: Int) : Mono<HttpResponse<*>> {
        return trainService.deleteCar(carId, id)
            .map { HttpResponse.ok("Deleted Car $carId from Train $id") }
    }
}