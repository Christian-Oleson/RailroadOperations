package com.oleson.models.dto

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Deserializable
@Serdeable.Serializable
data class Train(
    val id: Int,
    val name: String,
    val destination: String,
    var cars: List<RailCar>
)
