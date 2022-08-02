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
        val cache: MutableMap<Long, MicroBlog> = HashMap()

        try {
            val text = http.get(BLOG_API) { parameter("containerid", "107803${key.id}") }
                .bodyAsText()
            val temp = CustomJson.decodeFromString<Temp<PictureData>>(text)

            for (blog in temp.data().blogs()) {
                cache.compute(blog.id) { _, old ->
                    old?.copy(pictures = old.pictures + blog.pictures) ?: blog
                }
            }

            cache.replaceAll { _, blog ->
                blog.copy(
                    created = TimestampSerializer.timestamp(second = timestamp(id = blog.id)),
                    user = blog.user.copy(id = key.id, name = "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看")
                )
            }
        } catch (_: Throwable) {
            //
        }

        try {
            val text = http.get(BLOG_API) { parameter("containerid", "107603${key.id}") }
                .bodyAsText()
            val temp = CustomJson.decodeFromString<Temp<WeiboData>>(text)

            for (blog in temp.data().blogs()) {
                cache[blog.id] = blog
            }
        } catch (_: Throwable) {
            //
        }

        folder.resolve(key.filename).writeText(CustomJson.encodeToString(cache.values.toList()))

        loaded.add(key)
    }

    override suspend fun raw(): List<MicroBlog> = mutex.withLock {
        val cache: MutableList<MicroBlog> = ArrayList()
        for (user in loaded) {
            try {
                val blogs = folder.resolve(user.filename)
                    .readText()
                    .let { CustomJson.decodeFromString<List<MicroBlog>>(it) }

                cache.addAll(blogs)
            } catch (_: Throwable) {
                //
            }
        }

        cache
    }

    override suspend fun clear(): Unit = mutex.withLock {
        runInterruptible(context = Dispatchers.IO) {
            for (item in folder.listFiles() ?: return@runInterruptible) {
                if (!item.isDirectory) continue
                item.deleteRecursively()
            }
        }
    }

    private fun MicroBlog.folder(): File = folder.resolve(created.toLocalDate().toString()).apply { mkdirs() }

    public suspend fun images(blog: MicroBlog): List<File> = mutex.withLock {
        val cache = ArrayList<File>(blog.pictures.size)
        val folder = blog.folder()
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
        val file = blog.folder().resolve("${blog.id}.content.json")
        val json = when {
            !blog.isLongText -> return blog.raw ?: blog.text
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
                json
            }
        }

        CustomJson.decodeFromString<Temp<LongTextContent>>(json).data().content
    }
}