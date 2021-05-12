package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.RFC_1123_DATE_TIME

internal fun Headers.date(): OffsetDateTime? = this[HttpHeaders.Date].let { OffsetDateTime.parse(it, formatter) }

internal fun Headers.expires(): OffsetDateTime? = this[HttpHeaders.Expires].let { OffsetDateTime.parse(it, formatter) }

internal fun Headers.lastModified(): OffsetDateTime? = this[HttpHeaders.LastModified].let { OffsetDateTime.parse(it, formatter) }

internal val CustomJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

private val KOTLINX_SERIALIZER = KotlinxSerializer(CustomJson)

private fun client() = HttpClient(OkHttp) {
    Json {
        serializer = KOTLINX_SERIALIZER
        acceptContentTypes = acceptContentTypes + ContentType.Text.Plain
    }
    install(HttpTimeout) {
        socketTimeoutMillis = 10_000
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 10_000
    }
    BrowserUserAgent()
    ContentEncoding {
        gzip()
        deflate()
        identity()
    }
}

private val DEFAULT_IGNORE: (exception: Throwable) -> Boolean = { it is IOException || it is HttpRequestTimeoutException }

internal suspend fun <T> useHttpClient(
    ignore: (exception: Throwable) -> Boolean = DEFAULT_IGNORE,
    block: suspend (HttpClient) -> T
): T = client().use {
    var result: T? = null
    while (result === null) {
        result = runCatching {
            block(it)
        }.onFailure {
            if (ignore(it).not()) throw it
        }.getOrNull()
    }
    result
}

internal fun timestamp(value: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(value), SERVER_ZONE)

object TimestampSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return timestamp(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toEpochSecond())
    }
}