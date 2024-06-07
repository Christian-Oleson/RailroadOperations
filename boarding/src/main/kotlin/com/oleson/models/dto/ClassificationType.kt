package com.oleson.models.dto

enum class ClassificationType {
    Unknown,
    Houston,
    Chicago,
    LosAngeles;

    companion object {
        fun fromInt(value: Int): ClassificationType? =
            entries.firstOrNull { it.ordinal == value }
    }
}