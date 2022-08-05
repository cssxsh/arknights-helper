package xyz.cssxsh.arknights.bilibili

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File

public class VideoDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<VideoType, Video>() {

    override val loaded: MutableSet<VideoType> = HashSet()

    override suspend fun load(key: VideoType) {
        val cache: MutableList<Video> = ArrayList()
        for (index in 1..5) {
            val history = http.prepareGet(key.url) {
                parameter("mid", BILIBILI_ID)
                parameter("ps", 50)
                parameter("pn", index)
                parameter("order", "pubdate")
                parameter("tid", key.tid)
                parameter("jsonp", "jsonp")
            }.execute { response ->
                val json = response.bodyAsText()
                val temp = CustomJson.decodeFromString<Temp>(json)
                temp.data ?: throw ResponseException(response, json)
            }

            cache.addAll(history.list.videos)

            if (cache.size == history.page.count) break
        }
        key.write(cache)

        loaded.add(key)
    }

    override suspend fun raw(): List<Video> {
        val cache: MutableList<Video> = ArrayList()
        for (type in loaded) {
            try {
                val videos = type.read<List<Video>>()

                cache.addAll(videos)
            } catch (_: Throwable) {
                //
            }
        }

        return cache
    }

    override suspend fun clear(): Unit = mutex.withLock {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    private fun Video.folder(): File = folder.resolve(created.toLocalDate().toString()).apply { mkdirs() }

    public suspend fun cover(video: Video): File = mutex.withLock {
        val file = video.folder().resolve(video.pic.substringAfterLast('/'))
        if (file.exists().not()) {
            http.prepareGet(video.pic).copyTo(target = file)
        }
        return file
    }
}