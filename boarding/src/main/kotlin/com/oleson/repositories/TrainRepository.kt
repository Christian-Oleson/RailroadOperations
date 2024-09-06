package com.oleson.repositories

import com.oleson.models.TrainDbModel
import com.oleson.repositories.BaseRepository.Companion.getAllPages
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Singleton
open class TrainRepository (
    private val dynamoDbAsyncClient: DynamoDbAsyncClient,
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    @Value("\${dynamodb.train-table-name}") val trainTableName: String,
) {
    private val logger: Logger = LoggerFactory.getLogger(TrainRepository::class.java)
    private val tableSchema = TableSchema.fromBean(TrainDbModel::class.java)
    private val table = dynamoDbEnhancedAsyncClient.table(trainTableName, tableSchema)

    fun createRailroadCarTable(): Mono<Void> {
        return table.createTable().toMono()
    }

    fun createCar(train: TrainDbModel, railCar: TrainDbModel): Mono<TrainDbModel> {
        val putItemRequest = PutItemRequest.builder()
            .tableName(trainTableName)
            .item(mapOf(
                "pk" to AttributeValue.builder().s("train#${train.pk}").build(),
                "sk" to AttributeValue.builder().s("train#${train.pk}#car#${railCar.pk}").build(),
                "name" to AttributeValue.builder().s(railCar.name).build(),
                "destination" to AttributeValue.builder().s(railCar.destination).build(),
                "receiver" to AttributeValue.builder().s(railCar.receiver).build(),
                "type" to AttributeValue.builder().s(railCar.railCarType.toString()).build(),
            ))
            .build()

        logger.info("Created the putItemRequest: $putItemRequest")

        return Mono.fromFuture(dynamoDbAsyncClient.putItem(putItemRequest))
            .map { train }
            .toMono()
    }

    fun createTrain(train: TrainDbModel): Mono<TrainDbModel> {
        val putItemRequest = PutItemRequest.builder()
            .tableName(trainTableName)
            .item(mapOf(
                "pk" to AttributeValue.builder().s("train#${train.pk}").build(),
                "sk" to AttributeValue.builder().s("train#${train.pk}").build(),
                "name" to AttributeValue.builder().s(train.name).build(),
                "destination" to AttributeValue.builder().s(train.destination).build(),
                "carCount" to AttributeValue.builder().n(train.carCount.toString()).build(),
            ))
            .build()

        return Mono.fromFuture(dynamoDbAsyncClient.putItem(putItemRequest))
            .map { train }
            .toMono()
    }

    fun deleteTrain(id: Int) {
        val deleteItemRequest = DeleteItemRequest.builder()
            .tableName(trainTableName)
            .key(mapOf(
                "pk" to AttributeValue.builder().s("train#$id").build(),
                "sk" to AttributeValue.builder().s("train#$id").build(),
            ))
            .build()

        Mono.fromFuture(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
            .subscribe(
                { logger.info("Deleted train: $id") },
                { logger.error("Failed to delete train: $id") }
            )
    }

    fun deleteCar(id: Int, trainID: Int) {
        val deleteItemRequest = DeleteItemRequest.builder()
            .tableName(trainTableName)
            .key(mapOf(
                "pk" to AttributeValue.builder().s("train#$trainID").build(),
                "sk" to AttributeValue.builder().s("train#$trainID#car#$id").build(),
            ))
            .build()

        Mono.fromFuture(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
            .subscribe(
                { logger.info("Deleted car: $id") },
                { logger.error("Failed to delete car: $id") }
            )
    }

    fun getCar(id: Int): Mono<TrainDbModel> {
        val key = Key.builder()
            .partitionValue(AttributeValue.builder().s("car#${id}").build())
            .sortValue(AttributeValue.builder().s("car#${id}").build())
            .build()

        val getItemRequest = table.getItem { r -> r.key(key) }

        return Mono.fromFuture(getItemRequest)
            .toMono()
    }

    fun getTrain(id: Int): Mono<TrainDbModel> {
        val key = Key.builder()
            .partitionValue(AttributeValue.builder().s("train#${id}").build())
            .sortValue(AttributeValue.builder().s("train#${id}").build())
            .build()

        val getItemRequest = table.getItem { r -> r.key(key) }

        return Mono.fromFuture(getItemRequest)
            .map { it }
            .toMono()
    }

    fun queryTrainCars(trainID: Int): Mono<List<TrainDbModel>> {
        val conditionalQuery = QueryConditional.sortBeginsWith {
            it.partitionValue(AttributeValue.builder().s("train#$trainID").build())
            it.sortValue(AttributeValue.builder().s("train#$trainID#car#").build())
        }

        return getAllPages {
            val queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(conditionalQuery)

            val builtRequest = queryRequest.build()
            table.query(builtRequest).toMono()
        }.collectList()
    }

    fun getAllTrains(): Mono<List<TrainDbModel>> {
        return getAllPages {
            table.scan().toMono()
        }.collectList()
    }
}