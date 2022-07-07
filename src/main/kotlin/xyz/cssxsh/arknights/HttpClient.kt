package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import java.io.IOException
import java.time.*

internal val CustomJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
}

private val DefaultIgnore: suspend (exception: Throwable) -> Boolean = {
    it is IOException || it is HttpRequestTimeoutException
}

internal object Downloader : Closeable {

    private fun client() = HttpClient(OkHttp) {
        install(HttpTimeout) {
            socketTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = null
        }
        BrowserUserAgent()
    }

    override fun close() = clients.forEach { it.close() }

    var ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore

    private val clients = MutableList(3) { client() }

    internal suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            try {
                return@supervisorScope block(clients.random())
            } catch (e: Throwable) {
                if (ignore(e).not()) throw e
            }
        }
        throw CancellationException()
    }
}


