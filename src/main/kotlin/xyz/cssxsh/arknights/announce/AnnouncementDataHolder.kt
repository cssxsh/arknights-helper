package xyz.cssxsh.arknights.announce

import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File

public class AnnouncementDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<AnnounceType, Announcement>() {

    public companion object {
        private val html: Mutex = Mutex()
    }

    public override val loaded: MutableSet<AnnounceType> = HashSet()

    override suspend fun load(key: AnnounceType): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)

        loaded.add(key)
    }

    override suspend fun raw(): List<Announcement> = mutex.withLock {
        val cache: MutableMap<Int, Announcement> = HashMap()
        for (type in loaded) {
            try {
                val json = type.file.readText()
                val meta = CustomJson.decodeFromString<AnnouncementMeta>(json)
                for (announcement in meta.list) {
                    cache[announcement.id] = announcement
                }
            } catch (_: Throwable) {
                //
            }
        }

        cache.values.toList()
    }

    override suspend fun clear(): Unit = html.withLock {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    public suspend fun download(url: String): File = html.withLock {
        val file = folder.resolve("html/${url.substringAfterLast('/')}")
        if (file.exists().not()) {
            file.parentFile.mkdirs()
            http.prepareGet(url).copyTo(target = file)
        }
        file
    }
}