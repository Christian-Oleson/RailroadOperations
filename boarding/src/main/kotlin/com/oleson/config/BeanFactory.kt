package com.oleson.config

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

@Factory
class BeanFactory(
    @Value("\${dynamodb.endpoint:}") val dynamoDBEndpoint: String,
    private val applicationContext: ApplicationContext,
    ) {

    @Singleton
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient {
        val dynamoDbAsyncClientBuilder = DynamoDbAsyncClient.builder()
        if (dynamoDBEndpoint.isNotEmpty()) {
            dynamoDbAsyncClientBuilder.endpointOverride(URI.create(dynamoDBEndpoint))
        }
        return dynamoDbAsyncClientBuilder.build()
    }

    @Singleton
    fun dynamoDbEnhancedAsyncClient(): DynamoDbEnhancedAsyncClient {
        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(applicationContext.getBean(DynamoDbAsyncClient::class.java))
            .build()
    }
}