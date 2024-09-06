package com.oleson.models

import io.micronaut.serde.annotation.Serdeable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
@Serdeable
class ClassificationDbModel {

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute(value = "pk")
    var pk: String? = null

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute(value = "sk")
    var sk: String? = null
}