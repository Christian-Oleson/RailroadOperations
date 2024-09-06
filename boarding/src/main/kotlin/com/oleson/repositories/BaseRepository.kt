package com.oleson.repositories

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface BaseRepository {

    companion object {

        fun <T> getAllPages(
            pageDelegate: (lastEvaluatedKey: Map<String, AttributeValue>?) -> Mono<Page<T>>,
        ): Flux<T> {
            return pageDelegate(null)
                .expand { page ->
                    val lastEvaluatedKey = page.lastEvaluatedKey()
                    if (lastEvaluatedKey == null) {
                        Mono.empty()
                    } else {
                        pageDelegate(lastEvaluatedKey)
                    }
                }.flatMapIterable { it.items() }
        }
    }
}