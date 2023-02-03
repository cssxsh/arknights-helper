package xyz.cssxsh.arknights.excel

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.*
import java.util.*

public class ExcelDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<ExcelDataType, CacheInfo>() {
    public companion object {
        internal const val GAME_SOURCE_HOST_KEY = "xyz.cssxsh.arknights.source"
    }
    override val http: HttpClient = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        expectSuccess = true
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            requestTimeoutMillis = null
        }
        engine {
            config {
                val url = System.getProperty("xyz.cssxsh.arknights.doh", "https://public.dns.iij.jp/dns-query")
                val ipv6 = System.getProperty("xyz.cssxsh.arknights.ipv6", "true").toBoolean()
                doh(urlString = url, ipv6 = ipv6)
            }
        }
    }

    override val cache: MutableMap<ExcelDataType, Any> = EnumMap(ExcelDataType::class.java)

    private suspend inline fun <reified T : Any> ExcelDataType.get(): T = mutex.withLock {
        val raw = cache[this]
        return if (raw == null) {
            val read = CustomJson.decodeFromString<T>(file.readText())
            cache[this] = read
            read
        } else {
            raw as T
        }
    }

    override suspend fun load(key: ExcelDataType): Unit = mutex.withLock {
        http.prepareGet(key.url) {
            val host = System.getProperty(GAME_SOURCE_HOST_KEY, "raw.githubusercontent.com")
            if (this.host != host) this.host = host
        }.copyTo(target = key.file)

        cache.remove(key)
    }

    @Deprecated(message = "raw is empty", level = DeprecationLevel.HIDDEN)
    override suspend fun raw(key: ExcelDataType): List<CacheInfo> = emptyList()

    override suspend fun clear(): Unit = Unit

    public suspend fun version(): ExcelDataVersion = mutex.withLock {
        val file = ExcelDataType.VERSION.file
        if (file.exists().not()) {
            return@withLock ExcelDataVersion(
                stream = "",
                change = "",
                versionControl = "0.0.0"
            )
        }
        val text = file.readText()
        var stream: String? = null
        var change: String? = null
        var versionControl: String? = null
        for (line in text.lineSequence()) {
            if (line.isBlank()) continue
            val (name, value) = line.split(":")
            when (name) {
                "Stream" -> stream = value
                "Change" -> change = value
                "VersionControl" -> versionControl = value
                else -> Unit
            }
        }
        ExcelDataVersion(
            stream = stream ?: "",
            change = change ?: "",
            versionControl = versionControl ?: "0.0.0"
        )
    }

    public suspend fun building(): Building = ExcelDataType.BUILDING.get()

    public suspend fun character(): CharacterTable = ExcelDataType.CHARACTER.get()

    public suspend fun const(): ConstInfo = ExcelDataType.CONST.get()

    public suspend fun enemy(): EnemyTable = ExcelDataType.ENEMY.get()

    public suspend fun gacha(): GachaTable = ExcelDataType.GACHA.get()

    public suspend fun handbook(): HandbookTable = ExcelDataType.HANDBOOK.get()

    public suspend fun word(): Word = ExcelDataType.WORD.get()

    public suspend fun skill(): SkillTable = ExcelDataType.SKILL.get()

    public suspend fun story(): StoryTable = ExcelDataType.STORY.get()

    public suspend fun team(): TeamTable = ExcelDataType.TEAM.get()

    public suspend fun zone(): ZoneTable = ExcelDataType.ZONE.get()

    public suspend fun skin(): SkinTable = ExcelDataType.SKIN.get()

    public suspend fun activity(): ActivityTable = ExcelDataType.ACTIVITY.get()

    public suspend fun equip(): EquipTable = ExcelDataType.EQUIP.get()
}