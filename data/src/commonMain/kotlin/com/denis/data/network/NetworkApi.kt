package com.denis.data.network

import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface NetworkApi {
    suspend fun< T: Any> get(url: String, query: Map<String, Any?> = emptyMap(), type: KType): Result<T>
    suspend fun< T: Any> post(url: String, body: Any? = null, headers: Map<String, String> = emptyMap(), type: KType): Result<T>
}

suspend inline fun <reified T : Any> NetworkApi.get(url: String, query: Map<String, Any?> = emptyMap()): Result<T> {
    return get(url, query, typeOf<T>())
}

suspend inline fun<reified T: Any> NetworkApi.post(url: String, body: Any? = null, headers: Map<String, String> = emptyMap()): Result<T> {
    return post(url, body, headers, typeOf<T>())
}