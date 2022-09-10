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

public const val GAME_SOURCE: String = "https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master"

public var SERVER: ServerType = ServerType.CN

public typealias Server<T> = Map<ServerType, T>

public fun Double.intercept(decimal: Int = 2): String = "%.${decimal}f".format(this)

public fun Double.percentage(decimal: Int = 2): String = "${(this * 100).intercept(decimal)}%"