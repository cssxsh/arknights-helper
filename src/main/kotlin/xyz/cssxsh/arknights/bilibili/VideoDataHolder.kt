package xyz.cssxsh.arknights.bilibili

import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.io.*
import java.security.*
import java.util.*
import kotlin.collections.*
import kotlin.properties.*

public class VideoDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<VideoType, Video>() {

    private inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
        thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
    }

    private val HttpCookies.storage: CookiesStorage by reflect()

    private val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()

    private val external: File by lazy {
        File(
            System.getProperty(
                "xyz.cssxsh.arknights.bilibili.external",
                "data/xyz.cssxsh.mirai.plugin.bilibili-helper"
            )
        )
    }

    private val salt: String
        get() = kotlin.run {
            val file = external.resolve("salt.txt")
            if (file.exists()) {
                file.readText()
            } else {
                ""
            }
        }

    init {
        http.launch {
            val cookies = with(external.resolve("cookies.json")) {
                if (exists().not()) return@with emptyList()
                Json.decodeFromString<List<EditThisCookie>>(readText().ifBlank { "[]" })
                    .map { it.toCookie() }
            }
            (http.plugin(HttpCookies).storage as AcceptAllCookiesStorage).container.addAll(cookies)
        }
    }

    public override val cache: MutableMap<VideoType, List<Video>> = EnumMap(VideoType::class.java)

    override suspend fun load(key: VideoType) {
        val videos: MutableList<Video> = raw(key).toMutableList()
        http.get("https://www.bilibili.com/").bodyAsText()
        http.get("https://space.bilibili.com/161775300/video").bodyAsText()
        for (index in 1..3) {
            val history = http.prepareGet(key.url) {
                parameter("mid", BILIBILI_ID)
                parameter("ps", 50)
                parameter("pn", index)
                parameter("order", "pubdate")
                parameter("tid", key.tid)
                parameter("jsonp", "jsonp")
                parameter("wts", System.currentTimeMillis() / 1000)

                val digest = MessageDigest.getInstance("MD5")
                val parameters = url.parameters.entries()
                    .flatMap { e -> e.value.map { e.key to it } }
                    .sortedBy { it.first }.formUrlEncode()

                val md5 = digest.digest((parameters + salt).encodeToByteArray())

                parameter("w_rid", md5.joinToString("") { "%02x".format(it) })

                header(HttpHeaders.Origin, "https://space.bilibili.com")
                header(HttpHeaders.Referrer, "https://space.bilibili.com/161775300/video")
            }.execute { response ->
                val json = response.bodyAsText()
                val temp = CustomJson.decodeFromString<Temp>(json)
                if (temp.code != 0) throw ResponseException(response, json)
                CustomJson.decodeFromJsonElement<VideoHistory>(temp.data)
            }
            history.page ?: break

            for (video in history.list.videos) {
                if (videos.any { it.bvid == video.bvid }) continue
                videos.add(video)
            }
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