package com.oleson.models

import com.oleson.models.dto.RailCarType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class TrainDbModel {

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute(value = "pk")
    var pk: String? = null

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute(value = "sk")
    var sk: String? = null

    @get:DynamoDbAttribute(value = "name")
    var name: String? = null

    @get:DynamoDbAttribute(value = "destination")
    var destination: String? = null

    @get:DynamoDbAttribute(value = "carCount")
    var carCount: Int? = null

    @get:DynamoDbAttribute(value = "receiver")
    var receiver: String? = null

    @get:DynamoDbAttribute(value = "railCarType")
    var railCarType: RailCarType? = null
}