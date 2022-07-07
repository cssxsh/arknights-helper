package xyz.cssxsh.mirai.arknights

import io.ktor.http.*
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
import xyz.cssxsh.arknights.weibo.*

public class ArknightsCollector(private val contact: Contact) :
    FlowCollector<CacheInfo> {

    /**
     * 推送 [value] 到 [contact]
     */
    override suspend fun emit(value: CacheInfo) {
        val message = when (value) {
            is MicroBlog -> buildMessageChain {
                appendLine("鹰角有新微博！@${value.user?.name ?: "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看"}")

                append(value.toMessage(contact))

                value.retweeted?.let { retweeted ->
                    appendLine("----------------")
                    appendLine("@${retweeted.user?.name}")

                    append(retweeted.toMessage(contact))
                }
            }
            is Announcement -> buildMessageChain {
                appendLine("鹰角有新公告！${value.title}")
                append(value.toMessage(contact))
            }
            else -> {
                logger.error { "未知推送类型 ${value::class}" }
                return
            }
        }
        contact.sendMessage(message)
    }

    private suspend fun MicroBlog.toMessage(contact: Contact): Message = buildMessageChain {
        appendLine("时间: $created")
        appendLine("链接: $url")
        try {
            appendLine(ArknightsSubscriber.blogs.content(blog = this@toMessage))
        } catch (cause: Throwable) {
            logger.warning({ "加载[${url}]长微博失败" }, cause)
            appendLine(content)
        }

        try {
            val images = ArknightsSubscriber.blogs.images(blog = this@toMessage)
            for (file in images) {
                append(file.uploadAsImage(contact))
            }
        } catch (cause: Throwable) {
            logger.warning({ "下载微博图片失败" }, cause)
        }
    }

    private suspend fun Announcement.toMessage(contact: Contact): Message = buildMessageChain {
        appendLine("日期: $date")
        appendLine("分类: $group")
        appendLine("链接: $webUrl")
        val html = ArknightsSubscriber.announcements.download(web)
        val document = Parser.htmlParser().parseInput(html.reader(), webUrl)
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
                    "img" -> {
                        val image = try {
                            ArknightsSubscriber.announcements.download(Url(node.attr("src")))
                                .uploadAsImage(contact)
                        } catch (cause: Throwable) {
                            logger.warning({ "下载微博图片失败" }, cause)
                            continue
                        }
                        append(image)
                    }
                    "a" -> {
                        when {
                            node.text() == node.attr("href") -> Unit
                            node.childrenSize() > 0 -> Unit
                            else -> append("<${node.attr("href")}>")
                        }
                    }
                    "br" -> {
                        append("\n")
                    }
                }
                else -> continue
            }
        }
    }
}