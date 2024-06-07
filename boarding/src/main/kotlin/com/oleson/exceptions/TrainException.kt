package com.oleson.exceptions


class TrainException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)