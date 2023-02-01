package xyz.cssxsh.arknights.prts

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.excel.*
import java.io.*

/**
 * [PRTS WIKI](https://prts.wiki/)
 */
public class StaticDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<StaticData, CacheInfo>() {
    override val http: HttpClient = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        expectSuccess = true
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            requestTimeoutMillis = null
        }
    }

    override val cache: MutableMap<StaticData, File> = HashMap()

    override suspend fun clear(): Unit = mutex.withLock {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    @Deprecated(message = "raw is empty", level = DeprecationLevel.HIDDEN)
    public override suspend fun raw(key: StaticData): List<CacheInfo> = emptyList()

    override suspend fun load(key: StaticData): Unit = mutex.withLock {
        val response = http.get(key.url)
        require(response.contentType() == key.type) { "${key.url} content type <${response.contentType()}> is not <${key.type}>" }
        response.bodyAsChannel().copyAndClose(key.file.writeChannel())
    }

    public suspend fun voice(character: Character, word: CharacterWord): File {
        val key = StaticData.Voice(character = character, word = word)
        val file = key.file
        if (file.exists().not()) {
            file.parentFile.mkdirs()
            load(key = key)
        }
        return file
    }
}

