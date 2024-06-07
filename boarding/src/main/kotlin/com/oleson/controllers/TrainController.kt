package com.oleson.controllers

import com.oleson.exceptions.TrainException
import com.oleson.models.dto.RailCar
import com.oleson.models.dto.Train
import com.oleson.services.TrainService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import reactor.core.publisher.Mono

@Controller("Train")
class TrainController(
    private val trainService: TrainService
) {

    @Get("/")
    fun getAll() : Mono<HttpResponse<*>> {
        return Mono.just(trainService.getAllTrains())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }

    @Get("/{id}")
    fun get(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(trainService.getTrain(id))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }

    @Post("/createTable")
    fun createRailroadCarTable() : Mono<HttpResponse<*>> {
        return Mono.just(trainService.createTrainTable())
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }

    @Post("/")
    @Put("/")
    fun create(@Body train: Train) : Mono<HttpResponse<*>> {
        if (train.name.isBlank() || train.destination.isBlank()) {
            return Mono.error(TrainException("Train name and destination must be provided"))
        }

        return Mono.just(trainService.upsertTrain(train))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train not created")))
    }

    @Post("/{id}/car")
    @Put("/{id}/car")
    fun attachCar(@Body car: RailCar, id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(trainService.attachCar(car, id))
            .map<HttpResponse<*>> { HttpResponse.ok(it) }
            .switchIfEmpty(Mono.error(TrainException("Train not created")))
    }

    @Delete("/{id}")
    fun delete(id: Int) : Mono<HttpResponse<*>> {
        return Mono.just(trainService.deleteTrain(id))
            .map<HttpResponse<*>> { HttpResponse.ok("Deleted Train $id") }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }

    @Delete("/{id}/car/{carId}")
    fun deleteCar(id: Int, carId: Int) : Mono<HttpResponse<*>> {
        return Mono.just(trainService.deleteCar(carId, id))
            .map<HttpResponse<*>> { HttpResponse.ok("Deleted Car $carId from Train $id") }
            .switchIfEmpty(Mono.error(TrainException("Train table not created")))
    }
}