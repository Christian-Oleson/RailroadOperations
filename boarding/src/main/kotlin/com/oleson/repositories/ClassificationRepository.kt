package com.oleson.repositories

import com.oleson.models.ClassificationDbModel
import com.oleson.models.dto.ClassificationType
import com.oleson.repositories.BaseRepository.Companion.getAllPages
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

@Singleton
open class ClassificationRepository (
    private val dynamoDbAsyncClient: DynamoDbAsyncClient,
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    @Value("\${dynamodb.train-table-name}") val tableName: String,
) {
    private val logger: Logger = LoggerFactory.getLogger(ClassificationRepository::class.java)
    private val tableSchema = TableSchema.fromBean(ClassificationDbModel::class.java)
    private val table = dynamoDbEnhancedAsyncClient.table(tableName, tableSchema)

    fun createClassificationTable(): Flux<ClassificationDbModel> {
        val classifications = listOf(
            ClassificationType.Unknown,
            ClassificationType.Houston,
            ClassificationType.Chicago,
            ClassificationType.LosAngeles
        )

        val classificationDbModels: MutableList<ClassificationDbModel> = mutableListOf()

        classifications.forEach {
            val classificationDbModel = ClassificationDbModel()
            classificationDbModel.pk = "${it.ordinal}"
            classificationDbModel.sk = it.name

            classificationDbModels.add(classificationDbModel)
        }

        return Flux.fromIterable(classificationDbModels)
            .flatMap { createClassification(it) }

    }

    fun createClassification(classification: ClassificationDbModel): Mono<ClassificationDbModel> {
        val classificationAttributeMap = mapOf(
            "pk" to AttributeValue.builder().s("classification#${classification.pk}").build(),
            "sk" to AttributeValue.builder().s("${classification.sk}").build(),
        )

        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(classificationAttributeMap)
            .build()

        logger.info("Created the putItemRequest: $putItemRequest")

        return Mono.fromFuture(dynamoDbAsyncClient.putItem(putItemRequest))
            .map { classification }
            .toMono()
    }

    fun deleteClassification(id: Int) {
        val deleteItemRequest = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(mapOf(
                "pk" to AttributeValue.builder().s("classification#$id").build(),
            ))
            .build()

        Mono.fromFuture(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
            .subscribe(
                { logger.info("Deleted classification: $id") },
                { logger.error("Failed to delete classification: $id") }
            )
    }

    fun getClassification(id: Int): Mono<ClassificationDbModel> {
        val queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("pk = :pk")
            .expressionAttributeValues(mapOf(
                ":pk" to AttributeValue.builder().s("classification#$id").build()
            ))
            .build()

        dynamoDbAsyncClient.query(queryRequest).get().items().first().let {
            val classificationDbModel = ClassificationDbModel()
            classificationDbModel.pk = it["pk"]?.s()?.split("classification#")?.last()
            classificationDbModel.sk = it["sk"]?.s()
            return Mono.just(classificationDbModel)
        }
    }

    fun getAllClassifications(): Mono<List<ClassificationDbModel>> {
        return getAllPages {
            table.scan().toMono()
        }.collectList()
    }
}