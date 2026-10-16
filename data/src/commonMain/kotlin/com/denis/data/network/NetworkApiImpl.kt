package com.denis.data.network

import com.denis.domain.model.NetworkError
import com.denis.domain.model.NoResultsFound
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readLine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

class NetworkApiImpl(val httpClient: HttpClient): NetworkApi {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override suspend fun< T: Any> get(url: String, query: Map<String, Any?>, type: KType) =
        runCatching {
            val http = httpClient
                .get(url) {
                    contentType(ContentType.Application.Json)
                    query.forEach {
                        if (it.value != null)
                            parameter(it.key, it.value)
                    }
                }
            if (!(200..299).contains(http.status.value)) {
                throw getError(http.bodyAsChannel(), http.status.value)
            }
            val serializer = json.serializersModule.serializer(type)
            @Suppress("UNCHECKED_CAST")
            json.decodeFromString(serializer as KSerializer<T>, http.bodyAsText())
        }
    override suspend fun< T: Any> post(url: String, body: Any?, headers: Map<String, String>, type: KType) =
        runCatching {
            val http = httpClient
                .post(url) {
                    contentType(ContentType.Application.Json)
                    if (body != null) {
                        setBody(body)
                    }
                    headers.forEach {
                        header(it.key, it.value)
                    }
                }

            val serializer = json.serializersModule.serializer(type)
            @Suppress("UNCHECKED_CAST")
            json.decodeFromString(serializer as KSerializer<T>, http.bodyAsText())
        }

    suspend fun getError(responseContent: ByteReadChannel, code: Int): Throwable {
        responseContent.readLine()?.let { errorText ->
            val error = runCatching { Json.decodeFromString<Error>(errorText) }
                .recoverCatching {
                    Error(code, Json.decodeFromString<SimpleError>(errorText).error)
                }.getOrThrow()

            return when(error.errorCode) {
                404 -> NoResultsFound(error.errorMessage)
                else -> NetworkError(error.errorCode, error.errorMessage)
            }
        }
        return Throwable("Unable to serialize error")
    }
}