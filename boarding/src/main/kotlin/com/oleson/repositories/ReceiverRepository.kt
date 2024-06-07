package com.oleson.repositories

import com.oleson.models.ReceiverDbModel
import com.oleson.models.dto.ReceiverType
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Singleton
open class ReceiverRepository (
    private val dynamoDbAsyncClient: DynamoDbAsyncClient,
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    @Value("\${dynamodb.train-table-name}") val tableName: String,
) {
    private val logger: Logger = LoggerFactory.getLogger(ReceiverRepository::class.java)
    private val tableSchema = TableSchema.fromBean(ReceiverDbModel::class.java)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    fun createReceiverTable(): Flux<ReceiverDbModel> {
        val receivers = listOf(
            ReceiverType.Unknown,
            ReceiverType.UPS,
            ReceiverType.FedEx,
            ReceiverType.OldDominion,
            ReceiverType.USPS,
        )

        val receiverDbModels: MutableList<ReceiverDbModel> = mutableListOf()

        receivers.forEach {
            val receiverDbModel = ReceiverDbModel()
            receiverDbModel.pk = "${it.ordinal}"
            receiverDbModel.sk = it.name

            receiverDbModels.add(receiverDbModel)
        }

        return Flux.fromIterable(receiverDbModels)
            .flatMap { createReceiver(it) }
    }

    fun createReceiver(receiver: ReceiverDbModel): Mono<ReceiverDbModel> {
        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapOf(
                "pk" to AttributeValue.builder().s("receiver#${receiver.pk}").build(),
                "sk" to AttributeValue.builder().s("receiver#${receiver.sk}").build(),
            ))
            .build()

        logger.info("Created the putItemRequest: $putItemRequest")

        return Mono.fromFuture(dynamoDbAsyncClient.putItem(putItemRequest))
            .map { receiver }
            .toMono()
    }

    fun deleteReceiver(id: Int) {
        val deleteItemRequest = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(mapOf(
                "pk" to AttributeValue.builder().s("receiver#${id}").build(),
            ))
            .build()

        Mono.fromFuture(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
            .subscribe(
                { logger.info("Deleted receiver with id: $id") },
                { logger.error("Failed to delete receiver with id: $id") }
            )
    }

    fun getReceiver(receiver: ReceiverType): Mono<ReceiverDbModel> {
        val key = Key.builder()
            .partitionValue(AttributeValue.builder().s("receiver#${receiver.ordinal}").build())
            .build()

        val getItemRequest = table.getItem{ r -> r.key(key) }

        return Mono.fromFuture(getItemRequest)
            .toMono()
    }
}