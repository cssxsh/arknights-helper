package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import java.io.File
import java.time.*
import java.util.*

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

public interface GameDataType {
    public val path: String
    public val url: Url
    public val duration: Long get() = 0
    public val readable: (ByteArray) -> Boolean get() = { it.isNotEmpty() }
}

public interface GameDataDownloader {
    public val dir: File
    public val types: Iterable<GameDataType>
    public suspend fun download(flush: Boolean): List<File> = types.load(dir, flush)
}

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T> File.read(type: GameDataType): T =
    CustomJson.decodeFromString(resolve(type.path).readText())

public suspend fun <T : GameDataType> Iterable<T>.load(dir: File, flush: Boolean): List<File> {
    return map { type ->
        dir.resolve(type.path).also { file ->
            if (flush || file.exists().not()) {
                file.parentFile.mkdirs()
                Downloader.useHttpClient { client ->
                    val bytes = client.get(type.url).readBytes()
                    check(type.readable(bytes)) { "$type 下载内容不可读 ${bytes.decodeToString()}" }
                    file.writeBytes(bytes)
                }
                delay(type.duration)
            }
        }
    }
}

internal val SIGN = """<[^>]*>""".toRegex()

public fun String.remove(regex: Regex): String = replace(regex, "")

public fun Double.intercept(decimal: Int = 2): String = "%.${decimal}f".format(this)

public fun Double.percentage(decimal: Int = 2): String = "${(this * 100).intercept(decimal)}%"