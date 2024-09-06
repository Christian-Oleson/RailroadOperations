package com.oleson.services

import com.oleson.models.ReceiverDbModel
import com.oleson.models.dto.ReceiverType
import com.oleson.repositories.ReceiverRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Singleton
class ReceiverService(
    private val receiverRepository: ReceiverRepository
) {
    private val logger = LoggerFactory.getLogger(ReceiverService::class.java)

    fun createTable() : Flux<ReceiverType?> {
        logger.info("Creating Receiver Table")
        return receiverRepository.createReceiverTable().mapNotNull { mapReceiver(it) }
    }

    private fun mapReceiver(receiver: ReceiverDbModel) : ReceiverType? {
        return receiver.pk?.let {
            ReceiverType.fromInt(it.split("receiver#").last().toInt())
        }
    }

    fun createReceiver(id: Int) : Mono<ReceiverType?>? {
        val receiver = ReceiverType.fromInt(id)

        if (receiver != null) {
            val receiverDbModel = ReceiverDbModel()
            receiverDbModel.pk = "${receiver.ordinal}"
            receiverDbModel.sk = receiver.name
            return receiverRepository.createReceiver(receiverDbModel).mapNotNull { mapReceiver(it) }
        } else {
            val receiverDbModel = ReceiverDbModel()
            receiverDbModel.pk = "$id"
            receiverDbModel.sk = "Unknown"
            return receiverRepository.createReceiver(receiverDbModel).mapNotNull { mapReceiver(it) }
        }
    }

    fun deleteReceiver(id: Int) : Mono<Unit> {
        return Mono.just(receiverRepository.deleteReceiver(id))
    }

    fun getReceiver(receiver: ReceiverType) : Mono<ReceiverType> {
        return receiverRepository.getReceiver(receiver)
            .mapNotNull { mapReceiverDbRowModel(it) }
            .map {
                (it ?: ReceiverType.Unknown) as ReceiverType
            }
    }

    private fun mapReceiverDbRowModel(receiver: ReceiverDbModel) : Mono<ReceiverType>? {
        return receiver.pk?.let {
            ReceiverType.fromInt(it.split("receiver#").last().toInt())
        }?.let { Mono.just(it) }
    }

    fun getReceivers() : Mono<Map<Int, String>> {
        return Mono.just(ReceiverType.entries.associate { it.ordinal to it.name })
    }
}