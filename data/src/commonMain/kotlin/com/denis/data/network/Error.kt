package com.denis.data.network

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val errorCode: Int,
    val errorMessage: String
): Throwable()

@Serializable
data class SimpleError(
    val error: String
): Throwable()