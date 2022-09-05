package xyz.cssxsh.arknights.weibo

import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File

public class MicroBlogDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<BlogUser, MicroBlog>() {

    public override val loaded: MutableSet<BlogUser> = HashSet()

    private fun timestamp(id: Long): Long = (id shr 22) + 515483463L

    override suspend fun load(key: BlogUser) {
        val cache: MutableMap<Long, MicroBlog> = HashMap()

        try {
            val text = http.prepareGet(BLOG_API) { parameter("containerid", "107803${key.id}") }
                .body<String>()
            val temp = CustomJson.decodeFromString<Temp<PictureData>>(text)

            for (blog in temp.data().blogs()) {
                cache.compute(blog.id) { _, old ->
                    old?.copy(pictures = old.pictures + blog.pictures) ?: blog
                }
            }

            cache.replaceAll { _, blog ->
                blog.copy(
                    created = TimestampSerializer.timestamp(second = timestamp(id = blog.id)),
                    user = blog.user.copy(
                        id = key.id,
                        name = "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看"
                    )
                )
            }
        } catch (_: Exception) {
            //
        }

        try {
            val text = http.prepareGet(BLOG_API) { parameter("containerid", "107603${key.id}") }
                .body<String>()
            val temp = CustomJson.decodeFromString<Temp<WeiboData>>(text)

            for (blog in temp.data().blogs()) {
                cache[blog.id] = blog
            }
        } catch (_: Exception) {
            //
        }

        key.write(cache.values.toList())

        loaded.add(key)
    }

    override suspend fun raw(): List<MicroBlog> {
        val cache: MutableList<MicroBlog> = ArrayList()
        for (user in loaded) {
            try {
                val blogs = user.read<List<MicroBlog>>()

                cache.addAll(blogs)
            } catch (_: Exception) {
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

    private fun MicroBlog.folder(): File = folder.resolve(created.toLocalDate().toString()).apply { mkdirs() }

    public suspend fun images(blog: MicroBlog): List<File> = mutex.withLock {
        val cache = ArrayList<File>(blog.pictures.size)
        val folder = blog.folder()
        for (pid in blog.pictures) {
            val url = image(pid = pid)
            val file = folder.resolve(url.substringAfterLast('/'))
            if (file.exists().not()) {
                http.prepareGet(url).copyTo(file)
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
                val statement = http.prepareGet(CONTENT_API) {
                    parameter("id", blog.id)
                }
                statement.copyTo(file)
                val json = file.readText()
                if ("请求超时</p>" in json) {
                    file.delete()
                    throw IllegalStateException("请求超时")
                }
                if ("登录注册更精彩</p>" in json) {
                    file.delete()
                    throw IllegalStateException("登陆锁定")
                }
                if ("打开微博客户端，查看全文</p>" in json) {
                    file.delete()
                    throw IllegalStateException("微博客户端锁定")
                }
                json
            }
        }

        CustomJson.decodeFromString<Temp<LongTextContent>>(json).data().content
    }
}