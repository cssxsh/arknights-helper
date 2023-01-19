package xyz.cssxsh.arknights

import kotlinx.serialization.json.*
import java.util.*

internal const val IGNORE_UNKNOWN_KEYS = "xyz.cssxsh.arknights.ignore"

public val CustomJson: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = System.getProperty(IGNORE_UNKNOWN_KEYS, "false").toBoolean()
    isLenient = true
    allowStructuredMapKeys = true
//    coerceInputValues = true
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