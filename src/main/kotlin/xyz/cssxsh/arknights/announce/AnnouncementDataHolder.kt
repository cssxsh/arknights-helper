package xyz.cssxsh.arknights.announce

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.arknights.*
import java.io.File

public class AnnouncementDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<AnnounceType, Announcement>() {

    public override val loaded: MutableSet<AnnounceType> = HashSet()

    override suspend fun load(key: AnnounceType) {
        val response = http.get(key.url)
        val json = folder.resolve(key.filename)
        json.writeBytes(response.readBytes())

        loaded.add(key)
    }

    override suspend fun raw(): List<Announcement> {
        val cache: MutableMap<Int, Announcement> = HashMap()
        for (type in loaded) {
            try {
                val meta = folder.resolve(type.filename)
                    .readText()
                    .let { CustomJson.decodeFromString<AnnouncementMeta>(it) }
                for (announcement in meta.list) {
                    cache[announcement.id] = announcement
                }
            } catch (_: Throwable) {
                //
            }
        }

        return cache.values.toList()
    }

    override suspend fun clear() {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    public suspend fun download(url: Url): File = mutex.withLock {
        val file = folder.resolve("html/${url.filename}")
        if (file.exists().not()) {
            file.writeBytes(useHttpClient { client -> client.get(url).body() })
        }
        return file
    }
}