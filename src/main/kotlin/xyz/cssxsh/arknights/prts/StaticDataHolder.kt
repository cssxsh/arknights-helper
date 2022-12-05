package xyz.cssxsh.arknights.prts

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
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

    override suspend fun clear(): Unit = Unit

    @Deprecated(message = "raw is empty", level = DeprecationLevel.HIDDEN)
    public override suspend fun raw(key: StaticData): List<CacheInfo> = emptyList()

    override suspend fun load(key: StaticData): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)
    }

    public suspend fun voice(word: CharWord): File {
        val key = StaticData.Voice(word = word)
        val file = key.file
        if (file.exists().not()) load(key = key)
        return file
    }
}

