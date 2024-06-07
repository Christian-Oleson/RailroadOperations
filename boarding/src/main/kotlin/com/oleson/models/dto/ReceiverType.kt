package com.oleson.models.dto

enum class ReceiverType {
    Unknown,
    UPS,
    FedEx,
    OldDominion,
    USPS;

    companion object {
        fun fromInt(value: Int): ReceiverType? =
            entries.firstOrNull { it.ordinal == value }
    }
}