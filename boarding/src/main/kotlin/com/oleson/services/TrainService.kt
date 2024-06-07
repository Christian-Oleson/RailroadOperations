package com.oleson.services

import com.oleson.models.TrainDbModel
import com.oleson.models.dto.RailCar
import com.oleson.models.dto.RailCarType
import com.oleson.models.dto.Train
import com.oleson.repositories.TrainRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Singleton
class TrainService(
    private val trainRepository: TrainRepository
) {
    private val logger = LoggerFactory.getLogger(TrainService::class.java)

    fun createTrainTable() : Mono<Void> {
        logger.info("Creating Train Table")
        return trainRepository.createRailroadCarTable()
    }

    fun deleteTrain(id: Int) : Mono<Unit> {
        return Mono.just(trainRepository.deleteTrain(id))
    }

    fun deleteCar(id: Int, trainID: Int) : Mono<Unit> {
        return Mono.just(trainRepository.deleteCar(id, trainID))
    }

    fun upsertTrain(train: Train): Mono<Train> {
        val cars = train.cars.map { car -> mapCarToDbRowModel(car)}
        val dbTrain = mapTrainToDbRowModel(train)

        if (cars.isEmpty()) {
            return trainRepository.createTrain(dbTrain).map {
                mapDbRowModelToTrain(it)
            }
        }

        return Flux.fromIterable(cars.map { trainRepository.createCar(dbTrain, it) })
            .flatMap { trainRepository.createTrain(dbTrain).map {
                mapDbRowModelToTrain(it)
            }}.toMono()
    }

    fun attachCar(car: RailCar, id: Int): Mono<Train> {
        val dbCar = mapCarToDbRowModel(car)
        val dbTrain = TrainDbModel()
        dbTrain.pk = id.toString()
        dbTrain.sk = "TRAIN"

        return trainRepository.createCar(dbTrain, dbCar)
            .map { mapDbRowModelToTrain(it) }
    }

    fun mapCarToDbRowModel(railCar: RailCar) : TrainDbModel {
        val carDbRowModel = TrainDbModel()
        carDbRowModel.pk = railCar.id.toString()
        carDbRowModel.sk = "CAR"
        carDbRowModel.name = railCar.name
        carDbRowModel.destination = railCar.destination
        carDbRowModel.receiver = railCar.receiver
        carDbRowModel.railCarType = railCar.railCarType
        return carDbRowModel
    }

    private fun mapTrainToDbRowModel(train: Train): TrainDbModel {
        val trainDbModel = TrainDbModel()
        trainDbModel.pk = train.id.toString()
        trainDbModel.sk = "TRAIN"
        trainDbModel.name = train.name
        trainDbModel.destination = train.destination
        trainDbModel.carCount = train.cars.size
        return trainDbModel
    }

    private fun mapDbRowModelToCar(trainDbModel: TrainDbModel): RailCar {
        val sk = trainDbModel.sk?.split("car#")?.last()

        return RailCar(
            id = sk?.toInt() ?: 0,
            name = trainDbModel.name ?: "",
            destination = trainDbModel.destination ?: "",
            receiver = "",
            railCarType = RailCarType.FLATBED
        )
    }

    private fun mapDbRowModelToTrain(trainDbModel: TrainDbModel): Train {
        val pk = trainDbModel.pk?.replace("train#", "") ?: ""

        return Train(
            id = pk.toInt(),
            name = trainDbModel.name ?: "",
            destination = trainDbModel.destination ?: "",
            cars = mutableListOf()
        )
    }

    private fun mapDbRowModelToTrain(trainDbModels: MutableList<TrainDbModel>): Train {
        val trainDbRowModel = trainDbModels.first { it.carCount != null }
        val cars = trainDbModels.filter { it.carCount == null }

        val railCars = cars.map { car ->
            mapDbRowModelToCar(car)
        }

        val pk = trainDbRowModel.pk?.replace("train#", "") ?: ""

        return Train(
            id = pk.toInt(),
            name = trainDbRowModel.name ?: "",
            destination = trainDbRowModel.destination ?: "",
            cars = railCars
        )
    }

    fun getTrain(id: Int) : Mono<Train> {
        logger.info("Getting Train Items by id: $id")
        val cars = trainRepository.queryTrainCars(id)
            .map {
                trainDbRowModels ->
                    trainDbRowModels.map {
                        mapDbRowModelToCar(it)
                }
            }

        val getTrain = trainRepository.getTrain(id)
            .map { mapDbRowModelToTrain(it) }

        return getTrain.zipWith(cars).map {
            val train = it.t1
            val railCars = it.t2
            train.cars = railCars.toList()
            return@map train
        }
    }

    fun getAllTrains() : Mono<List<Train>> {
        logger.info("Getting All Train Items")
        return trainRepository.getAllTrains()
            .map {
                trainDbRowModels ->
                    trainDbRowModels.filter { it.carCount?.let { count -> count > 0 } == true }.map {
                        mapDbRowModelToTrain(it)
                }
            }
    }
}