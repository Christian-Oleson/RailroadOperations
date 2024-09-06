package com.oleson.models.dto

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Deserializable
@Serdeable.Serializable
data class RailCar (
    val id: Int,
    val name: String,
    val destination: String,
    val receiver: String,
    val railCarType: RailCarType,
)