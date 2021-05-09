package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
}

internal inline fun <reified T> File.read(type: GameDataType): T = Json.decodeFromString(resolve(type.path).readText())

suspend fun <T : GameDataType> Iterable<T>.load(dir: File, flush: Boolean, build: (path: T) -> Url): List<File> {
    return useHttpClient { client ->
        map { type ->
            dir.resolve(type.path).also { file ->
                if (flush || file.exists().not()) {
                    file.parentFile.mkdirs()
                    file.writeText(client.get(build(type)))
                }
            }
        }
    }
}

internal val SIGN = """<[^>]*>""".toRegex()

fun String.remove(regex: Regex) = replace(regex, "")