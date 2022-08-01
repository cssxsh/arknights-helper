package xyz.cssxsh.arknights.weibo

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File

public class MicroBlogDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<BlogUser, MicroBlog>() {

    public override val loaded: MutableSet<BlogUser> = HashSet()

    private fun timestamp(id: Long): Long = (id shr 22) + 515483463L

    override suspend fun load(key: BlogUser): Unit = mutex.withLock {
        folder.resolve(key.filename)
            .writeBytes(http.get(key.url).readBytes())

        folder.resolve(key.filename2)
            .writeBytes(http.get(key.picture).readBytes())

        loaded.add(key)
    }

    override suspend fun raw(): List<MicroBlog> = mutex.withLock {
        val cache: MutableMap<Long, MicroBlog> = HashMap()
        for (user in loaded) {
            try {
                val picture = folder.resolve(user.filename2)
                    .readText()
                    .let { CustomJson.decodeFromString<Temp<PictureData>>(it) }

                for (blog in picture.data().blogs()) {
                    cache.compute(blog.id) { _, old ->
                        old?.copy(pictures = old.pictures + blog.pictures) ?: blog
                    }
                }

                cache.replaceAll { _, blog ->
                    // TODO: MicroBlogUser
                    blog.copy(created = TimestampSerializer.timestamp(timestamp(id = blog.id)))
                }
            } catch (_: Throwable) {
                //
            }

            try {
                val blog = folder.resolve(user.filename)
                    .readText()
                    .let { CustomJson.decodeFromString<Temp<WeiboData>>(it) }
                for (card in blog.data().cards) {
                    val b = card.blog ?: continue
                    cache[b.id] = b
                }
            } catch (_: Throwable) {
                //
            }
        }

        cache.values.toList()
    }

    override suspend fun clear(): Unit = mutex.withLock {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    private val MicroBlog.folder: File get() = folder.resolve(created.toLocalDate().toString()).apply { mkdirs() }

    public suspend fun images(blog: MicroBlog): List<File> = mutex.withLock {
        val cache = ArrayList<File>(blog.pictures.size)
        val folder = blog.folder
        for (pid in blog.pictures) {
            val url = image(pid = pid)
            val file = folder.resolve(url.substringAfterLast('/'))
            if (file.exists().not()) {
                file.writeBytes(useHttpClient { client -> client.get(url).body() })
            }
            cache.add(file)
        }
        cache
    }

    public suspend fun content(blog: MicroBlog): String = mutex.withLock {
        val file = blog.folder.resolve("${blog.id}.content.json")
        val content = when {
            !blog.isLongText -> blog.raw ?: blog.text
            file.exists() -> file.readText()
            else -> {
                val json = useHttpClient { client ->
                    client.get(CONTENT_API) {
                        parameter("id", blog.id)
                    }.bodyAsText().also {
                        if ("请求超时</p>" in it) throw IllegalStateException("请求超时")
                        if ("登录注册更精彩</p>" in it) throw IllegalStateException("登陆锁定")
                        if ("打开微博客户端，查看全文</p>" in it) throw IllegalStateException("微博客户端锁定")
                    }
                }
                file.writeText(json)

                CustomJson.decodeFromString<Temp<LongTextContent>>(json).data().content
            }
        }
        return content.replace("<br />", "\n").remove(SIGN)
    }
}