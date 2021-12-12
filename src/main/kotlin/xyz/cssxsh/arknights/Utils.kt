package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import java.io.File
import java.time.*
import java.util.*

enum class ServerType(val locale: Locale, val zone: ZoneId) {
    CN(Locale.CHINA, ZoneId.of("GMT+08:00")),
    US(Locale.US, ZoneId.of("GMT-05:00")),
    JP(Locale.JAPAN, ZoneId.of("GMT+09:00")),
    KR(Locale.KOREA, ZoneId.of("GMT+09:00")),
    TW(Locale.TAIWAN, ZoneId.of("GMT+08:00"));
}

var GITHUB_REPO = "Kengxxiao/ArknightsGameData"

var SERVER: ServerType = ServerType.CN

val SERVER_ZONE: ZoneId get() = SERVER.zone

typealias Server<T> = Map<ServerType, T>

interface GameDataType {
    val path: String
    val url: Url
    val duration: Long get() = 0
    val readable: (ByteArray) -> Boolean get() = { it.isNotEmpty() }
}

interface GameDataDownloader {
    val dir: File
    val types: Iterable<GameDataType>
    suspend fun download(flush: Boolean) = types.load(dir, flush)
}

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T> File.read(type: GameDataType): T =
    CustomJson.decodeFromString(resolve(type.path).readText())

suspend fun <T : GameDataType> Iterable<T>.load(dir: File, flush: Boolean): List<File> {
    return map { type ->
        dir.resolve(type.path).also { file ->
            if (flush || file.exists().not()) {
                file.parentFile.mkdirs()
                Downloader.useHttpClient { client ->
                    val bytes = client.get<ByteArray>(type.url)
                    check(type.readable(bytes)) { "$type 下载内容不可读 ${bytes.decodeToString()}" }
                    file.writeBytes(bytes)
                }
                delay(type.duration)
            }
        }
    }
}

internal val SIGN = """<[^>]*>""".toRegex()

fun String.remove(regex: Regex) = replace(regex, "")

fun Double.intercept(decimal: Int = 2) = "%.${decimal}f".format(this)

fun Double.percentage(decimal: Int = 2) = "${(this * 100).intercept(decimal)}%"