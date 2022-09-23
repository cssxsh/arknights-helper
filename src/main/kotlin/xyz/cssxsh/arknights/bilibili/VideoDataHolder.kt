package xyz.cssxsh.arknights.bilibili

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.*
import java.util.*
import kotlin.collections.*

public class VideoDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<VideoType, Video>() {

    public override val cache: MutableMap<VideoType, List<Video>> = EnumMap(VideoType::class.java)

    override suspend fun load(key: VideoType) {
        val videos: MutableList<Video> = ArrayList()
        for (index in 1..3) {
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

            videos.addAll(history.list.videos)

            history.page ?: break
            if (videos.size == history.page.count) break
        }
        key.write(videos)

        cache[key] = videos
    }

    override suspend fun raw(key: VideoType): List<Video> {
        return cache[key] ?: try {
            key.read()
        } catch (_: FileNotFoundException) {
            emptyList()
        }
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