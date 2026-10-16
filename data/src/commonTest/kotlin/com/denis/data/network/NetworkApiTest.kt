package com.denis.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkApiTest {

    @Serializable
    data class TestData(val id: Int, val name: String)

    private fun createClient(engine: MockEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `get should return data on success`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"id": 1, "name": "Test"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = NetworkApiImpl(createClient(mockEngine))

        val result = api.get<TestData>("http://example.com")

        assertTrue(result.isSuccess)
        assertEquals(TestData(1, "Test"), result.getOrNull())
    }

    @Test
    fun `get should return failure on error status`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"error": "Not Found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = NetworkApiImpl(createClient(mockEngine))

        val result = api.get<TestData>("http://example.com")

        assertTrue(result.isFailure)
    }
}
