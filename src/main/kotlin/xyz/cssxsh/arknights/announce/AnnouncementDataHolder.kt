package xyz.cssxsh.arknights.announce

import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import xyz.cssxsh.arknights.*
import java.io.*
import java.util.*

public class AnnouncementDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<AnnounceType, Announcement>() {

    public companion object {
        private val html: Mutex = Mutex()
    }

    override val cache: MutableMap<AnnounceType, List<Announcement>> = EnumMap(AnnounceType::class.java)

    override suspend fun load(key: AnnounceType): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)
        cache.remove(key)
    }

    override suspend fun raw(key: AnnounceType): List<Announcement> = mutex.withLock {
        return cache[key] ?: try {
            key.read<AnnouncementMeta>().list
        } catch (_: FileNotFoundException) {
            emptyList()
        }
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