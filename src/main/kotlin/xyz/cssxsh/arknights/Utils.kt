package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.ZoneId
import java.util.*

val ARKNIGHTS_SERVERS = mapOf(
    Locale.US to ZoneId.of("GMT-05:00"),
    Locale.JAPAN to ZoneId.of("GMT+09:00"),
    Locale.KOREA to ZoneId.of("GMT+09:00"),
    Locale.CHINA to ZoneId.of("GMT+08:00"),
    Locale.TAIWAN to ZoneId.of("GMT+08:00")
)

var GITHUB_REPO = "Kengxxiao/ArknightsGameData"

var SERVER: Locale = Locale.CHINA
    set(value) { check(value in ARKNIGHTS_SERVERS); field = value }

val SERVER_ZONE: ZoneId get() = ARKNIGHTS_SERVERS.getValue(SERVER)

enum class GameDataType {
    ART,
    BUILDING,
    EXCEL,
    LEVELS,
    STORY
}

private fun path(name: String, type: GameDataType): String = "${SERVER}/gamedata/${type.name.toLowerCase()}/${name}"

private fun File.resolve(name: String, type: GameDataType): File = resolve(path(name, type))

internal fun raw(path: String): Url {
    return Url("https://raw.githubusercontent.com/${GITHUB_REPO}/master/${path}")
}

internal fun jsdelivr(path: String): Url {
    return Url("https://cdn.jsdelivr.net/gh/${GITHUB_REPO}@master/${path}")
}

internal inline fun <reified T> File.read(name: String, type: GameDataType): T {
    return Json.decodeFromString(resolve(name = name, type = type).readText())
}

typealias ResourceMap = Map<GameDataType, Set<String>>

typealias ResourceFiles = Map<GameDataType, Map<String, File>>

suspend fun download(dir: File, map: ResourceMap, raw: Boolean = false): ResourceFiles {
    return useHttpClient { client ->
        map.mapValues { (type, list) ->
            dir.resolve(name = "", type = type).mkdirs()
            list.associateWith { name ->
                val url = if (raw) raw(path(name = name, type = type)) else jsdelivr(path(name = name, type = type))
                dir.resolve(name = name, type = type).also { file ->
                    val last = (client.head<HttpMessage>(url).headers.date()?.toEpochSecond() ?: 0) * 1_000
                    if (last > file.lastModified() || file.exists().not()) {
                        file.writeText(client.get(url))
                    }
                }
            }
        }
    }
}

private val SIGN = """<[^>]*>""".toRegex()

fun String.removeSign() = replace(SIGN, "")