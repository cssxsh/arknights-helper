package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.nodes.*
import org.jsoup.parser.*
import org.jsoup.select.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.mirai.arknights.data.*
import java.util.WeakHashMap

public class ArknightsCollector(private val contact: Contact) : FlowCollector<CacheInfo> {
    internal companion object {
        val cache = HashMap<Long, MutableMap<String, Long>>().withDefault { WeakHashMap() }
        val mutex: Mutex = Mutex()
    }

    /**
     * 推送 [value] 到 [contact]
     */
    override suspend fun emit(value: CacheInfo): Unit = mutex.withLock {
        if (cache.getValue(contact.id).contains(value.url)) return
        val message = when (value) {
            // 微博
            is MicroBlog -> {
                val accept = ArknightsTaskConfig.blog[contact.id] ?: return
                if (accept.none { it.id == value.id }) return
                buildMessageChain {
                    appendLine("鹰角有新微博！@${value.user.name}")

                    append(blog = value)

                    value.retweeted?.let { retweeted ->
                        appendLine("----------------")
                        appendLine("@${retweeted.user.name}")

                        append(blog = value)
                    }
                }
            }
            // 公告
            is Announcement -> {
                val accept = ArknightsTaskConfig.announce[contact.id] ?: return
                if (accept.none { it == value.type }) return
                buildMessageChain {
                    appendLine("鹰角有新公告！${value.title}")
                    append(announcement = value)
                }
            }
            // 视频
            is Video -> {
                val accept = ArknightsTaskConfig.video[contact.id] ?: return
                if (accept.none { it.tid == value.tid }) return
                buildMessageChain {
                    appendLine("鹰角有新视频！${value.title}")
                    append(video = value)
                }
            }
            // 未实现的类型
            else -> {
                logger.error { "未实现的推送 ${value::class.qualifiedName}" }
                return
            }
        }
        contact.sendMessage(message)
        cache.getValue(contact.id)[value.url] = System.currentTimeMillis()
    }

    private fun parseNodes(html: String, baseUri: String): List<Node> {
        val body = Parser.htmlParser().parseInput(html, baseUri).body()
        val visitor = object : NodeVisitor, MutableList<Node> by ArrayList() {
            override fun head(node: Node, depth: Int) {
                if (node is TextNode) add(node)
            }

            override fun tail(node: Node, depth: Int) {
                if (node is Element) add(node)
            }
        }
        NodeTraversor.traverse(visitor, body)

        return visitor
    }

    private suspend fun MessageChainBuilder.append(blog: MicroBlog): MessageChainBuilder = apply {
        appendLine("时间: ${blog.created}")
        appendLine("链接: ${blog.url}")
        val content = try {
            ArknightsSubscriber.blogs.content(blog = blog)
        } catch (cause: Exception) {
            logger.warning({ "加载[${blog.url}]长微博失败" }, cause)
            blog.raw ?: blog.text
        }
        for (node in parseNodes(content, blog.url)) {
            when (node) {
                is TextNode -> append(node.wholeText)
                is Element -> when (node.nodeName()) {
                    // 图片
                    "img" -> {
                        append("[图片]")
                    }
                    // 链接
                    "a" -> {
                        when {
                            node.text() == node.attr("href").trim() -> Unit
                            node.childrenSize() > 0 -> Unit
                            else -> append("<${node.attr("href")}>")
                        }
                    }
                    // 换行
                    "br", "p" -> {
                        append("\n")
                    }
                }
                // 忽略未知
                else -> continue
            }
        }

        try {
            val images = ArknightsSubscriber.blogs.images(blog = blog)
            for (file in images) {
                append(file.uploadAsImage(contact))
            }
        } catch (cause: Exception) {
            logger.warning({ "下载微博图片失败" }, cause)
        }
    }

    private suspend fun MessageChainBuilder.append(announcement: Announcement): MessageChainBuilder = apply {
        appendLine("日期: ${announcement.created.toLocalDate()}")
        appendLine("分类: ${announcement.group}")
        appendLine("链接: ${announcement.url}")
        val html = ArknightsSubscriber.announcements.download(announcement.url)
        for (node in parseNodes(html.readText(), announcement.url)) {
            when (node) {
                is TextNode -> append(node.wholeText.trim())
                is Element -> when (node.nodeName()) {
                    // 图片
                    "img" -> {
                        val image = try {
                            ArknightsSubscriber.announcements.download(url = node.attr("src"))
                                .uploadAsImage(contact)
                        } catch (cause: Exception) {
                            logger.warning({ "下载微博图片失败" }, cause)
                            continue
                        }
                        append(image)
                    }
                    // 链接
                    "a" -> {
                        when {
                            node.text() == node.attr("href").trim() -> Unit
                            node.childrenSize() > 0 -> Unit
                            else -> append("<${node.attr("href")}>")
                        }
                    }
                    // 换行
                    "br", "p" -> {
                        append("\n")
                    }
                }
                // 忽略未知
                else -> continue
            }
        }
    }

    private suspend fun MessageChainBuilder.append(video: Video): MessageChainBuilder = apply {
        appendLine("链接: ${video.url}")
        appendLine("时间: ${video.created}")
        if (video.description.isNotBlank()) {
            appendLine("简介：")
            appendLine(video.description)
        }

        try {
            val image = ArknightsSubscriber.videos.cover(video = video)
                .uploadAsImage(contact)
            append(image)
        } catch (cause: Exception) {
            appendLine("添加图片[${video.pic}]失败, ${cause.message}")
        }
    }
}