package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import java.io.IOException
import java.time.*

internal val CustomJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

private fun client() = HttpClient(OkHttp) {
    install(HttpTimeout) {
        socketTimeoutMillis = 10_000
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 10_000
    }
    BrowserUserAgent()
}

private val DEFAULT_IGNORE: (exception: Throwable) -> Boolean = {
    it is IOException || it is HttpRequestTimeoutException
}

internal suspend fun <T> useHttpClient(
    ignore: (exception: Throwable) -> Boolean = DEFAULT_IGNORE,
    block: suspend (HttpClient) -> T
): T = supervisorScope {
    client().use {
        while (isActive) {
            runCatching {
                block(it)
            }.onFailure {
                if (ignore(it).not()) throw it
            }.onSuccess {
                return@use it
            }
        }
        throw CancellationException()
    }
}

internal fun timestamp(value: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(value), SERVER_ZONE)

object TimestampSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return timestamp(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toEpochSecond())
    }
}