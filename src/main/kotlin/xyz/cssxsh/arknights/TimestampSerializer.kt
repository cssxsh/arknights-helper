package xyz.cssxsh.arknights

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

internal object TimestampSerializer : KSerializer<OffsetDateTime> {

    fun timestamp(second: Long): OffsetDateTime {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(second), ZoneId.systemDefault())
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return timestamp(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toEpochSecond())
    }
}