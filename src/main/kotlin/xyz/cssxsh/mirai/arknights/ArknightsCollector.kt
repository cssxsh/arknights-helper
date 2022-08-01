package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.flow.*
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

public class ArknightsCollector(private val contact: Contact) :
    FlowCollector<CacheInfo> {

    /**
     * 推送 [value] 到 [contact]
     */
    override suspend fun emit(value: CacheInfo) {
        val message = when (value) {
            // TODO: 过滤屏蔽的类型
            is MicroBlog -> buildMessageChain {
                appendLine("鹰角有新微博！@${value.user?.name ?: "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看"}")

                append(blog = value)

                value.retweeted?.let { retweeted ->
                    appendLine("----------------")
                    appendLine("@${retweeted.user?.name}")

                    append(blog = value)
                }
            }
            // TODO: 过滤屏蔽的类型
            is Announcement -> buildMessageChain {
                appendLine("鹰角有新公告！${value.title}")
                append(announcement = value)
            }
            // TODO: 过滤屏蔽的类型
            is Video -> buildMessageChain {
                appendLine("鹰角有新视频！${value.title}")
                append(video = value)
            }
            // 未实现的类型
            else -> {
                logger.error { "未实现的推送 ${value::class.qualifiedName}" }
                return
            }
        }
        contact.sendMessage(message)
    }

    private suspend fun MessageChainBuilder.append(blog: MicroBlog): MessageChainBuilder = apply {
        appendLine("时间: ${blog.created}")
        appendLine("链接: ${blog.url}")
        try {
            appendLine(ArknightsSubscriber.blogs.content(blog = blog))
        } catch (cause: Throwable) {
            logger.warning({ "加载[${blog.url}]长微博失败" }, cause)
            appendLine(blog.content)
        }

        try {
            val images = ArknightsSubscriber.blogs.images(blog = blog)
            for (file in images) {
                append(file.uploadAsImage(contact))
            }
        } catch (cause: Throwable) {
            logger.warning({ "下载微博图片失败" }, cause)
        }
    }

    private suspend fun MessageChainBuilder.append(announcement: Announcement): MessageChainBuilder = apply {
        appendLine("日期: ${announcement.date}")
        appendLine("分类: ${announcement.group}")
        appendLine("链接: ${announcement.webUrl}")
        val html = ArknightsSubscriber.announcements.download(announcement.webUrl)
        val document = Parser.htmlParser().parseInput(html.reader(), announcement.webUrl)
        val visitor = object : NodeVisitor, MutableList<Node> by ArrayList() {
            override fun head(node: Node, depth: Int) {
                if (node is TextNode) add(node)
            }

            override fun tail(node: Node, depth: Int) {
                if (node is Element) add(node)
            }
        }
        NodeTraversor.traverse(visitor, document)

        for (node in visitor) {
            when (node) {
                is TextNode -> append(node.wholeText)
                is Element -> when (node.nodeName()) {
                    // 图片
                    "img" -> {
                        val image = try {
                            ArknightsSubscriber.announcements.download(url = node.attr("src"))
                                .uploadAsImage(contact)
                        } catch (cause: Throwable) {
                            logger.warning({ "下载微博图片失败" }, cause)
                            continue
                        }
                        append(image)
                    }
                    // 链接
                    "a" -> {
                        when {
                            node.text() == node.attr("href") -> Unit
                            node.childrenSize() > 0 -> Unit
                            else -> append("<${node.attr("href")}>")
                        }
                    }
                    // 换行
                    "br" -> {
                        append("\n")
                    }
                }
                // 忽略未知
                else -> continue
            }
        }
    }

    private suspend fun MessageChainBuilder.append(video: Video): MessageChainBuilder = apply {
        appendLine("鹰角有新视频了！")
        appendLine("链接: ${video.url}")
        appendLine("标题: ${video.title}")
        appendLine("时间: ${video.created}")
        if (video.description.isNotBlank()) {
            appendLine("简介：")
            appendLine(video.description)
        }

        try {
            val image = ArknightsSubscriber.videos.cover(video = video)
                .uploadAsImage(contact)
            append(image)
        } catch (cause: Throwable) {
            appendLine("添加图片[${video.pic}]失败, ${cause.message}")
        }
    }
}