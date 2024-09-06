package com.oleson.models

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class ReceiverDbModel {

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute(value = "pk")
    var pk: String? = null

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute(value = "sk")
    var sk: String? = null
}