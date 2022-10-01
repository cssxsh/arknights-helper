package xyz.cssxsh.arknights

import kotlinx.serialization.json.*
import java.util.*

public val CustomJson: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
}

public enum class ServerType(public val locale: Locale) {
    CN(Locale.CHINA),
    US(Locale.US),
    JP(Locale.JAPAN),
    KR(Locale.KOREA),
    TW(Locale.TAIWAN);
}

public var SERVER: ServerType = ServerType.CN

public typealias Server<T> = Map<ServerType, T>