package com.denis.data

import com.denis.data.network.NetworkApi
import kotlinx.serialization.json.Json
import kotlin.reflect.KType

class FakeNetworkApi: NetworkApi {
    val map: MutableMap<String, Any?> = mutableMapOf()
    val json = Json { ignoreUnknownKeys = true }

    fun clear() { map.clear() }

    inline fun <reified T> queueGet(
        url: String,
        query: Map<String, Any?>,
        result: Result<T>
    ) {
        map[url + query.toString()] = result
    }

    inline fun <reified T> queuePost(
        url: String,
        body: Any?,
        headers: Map<String, String>,
        result: Result<T>
    ) {
        map[url + body.toString() + headers.toString()] = result
    }

    override suspend fun <T : Any> get(
        url: String,
        query: Map<String, Any?>,
        type: KType
    ): Result<T> {
        return map[url + query.toString()] as Result<T>
    }

    override suspend fun <T : Any> post(
        url: String,
        body: Any?,
        headers: Map<String, String>,
        type: KType
    ): Result<T> {
        return map[url + body.toString() + headers.toString()] as Result<T>
    }
}