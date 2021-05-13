package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import java.io.File
import java.time.ZoneId
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
}

interface GameDataDownloader {
    val dir: File
    val types: Iterable<GameDataType>
    suspend fun download(flush: Boolean) = types.load(dir, flush)
}

internal inline fun <reified T> File.read(type: GameDataType): T = CustomJson.decodeFromString(resolve(type.path).readText())

suspend fun <T : GameDataType> Iterable<T>.load(dir: File, flush: Boolean): List<File> {
    return useHttpClient { client ->
        map { type ->
            dir.resolve(type.path).also { file ->
                if (flush || file.exists().not()) {
                    file.parentFile.mkdirs()
                    file.writeText(client.get(type.url))
                }
            }
        }
    }
}

internal val SIGN = """<[^>]*>""".toRegex()

fun String.remove(regex: Regex) = replace(regex, "")

fun Double.intercept(decimal: Int = 2) = "%.${decimal}f%%".format(this * 100)