package xyz.cssxsh.mirai.arknights.command

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.meme.face.*

@OptIn(MiraiExperimentalApi::class)
public object ArknightsFaceCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-face", "方舟表情",
    description = "明日方舟助手表情指令"
) {

    private const val AUTHOR_ID = 40424L

    private const val SUPPLIER_ID = 1112171476L

    private val http: HttpClient = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        expectSuccess = true
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            requestTimeoutMillis = null
        }
    }

    @SubCommand("random", "随机")
    @Description("表情随机")
    public suspend fun UserCommandSender.random() {
        val message = try {
            val ids = buildList {
                var offset = 0
                while (isActive) {
                    val info = MarketFaceHelper.querySupplierInfo(supplierId = SUPPLIER_ID, offset = offset)
                    for (item in info.items) {
                        if (item.appId == 1) continue
                        add(item.itemId)
                    }
                    if (info.items.isEmpty() || info.workNum == this.size) break
                    offset += 30
                }
                val author = MarketFaceHelper.queryAuthorDetail(authorId = AUTHOR_ID)
                for (item in author.items) {
                    add(item.itemId)
                }
            }

            val itemId = ids.random()
            val data = MarketFaceHelper.queryFaceAndroid(itemId = itemId)
            val faces = MarketFaceHelper.build(data = data)
            faces.random()
        } catch (_: NoClassDefFoundError) {
            "请安装 https://github.com/cssxsh/meme-helper".toPlainText()
        } catch (cause: Exception) {
            logger.warning({ "随机表情失败" }, cause)
            "随机表情失败".toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand("detail", "详情")
    @Description("表情列表")
    public suspend fun UserCommandSender.detail() {
        val message = try {
            val author = MarketFaceHelper.queryAuthorDetail(authorId = AUTHOR_ID)
            buildForwardMessage(subject) {
                bot says {
                    val head = try {
                        val bytes = http.get(author.head).body<ByteArray>()
                        bytes.toExternalResource().use { resource ->
                            subject.uploadImage(resource)
                        }
                    } catch (cause: Exception) {
                        logger.warning({ "下载作者图片失败" }, cause)
                        emptyMessageChain()
                    }
                    append(head).appendLine()
                    appendLine("作者: ${author.name}")
                    appendLine("简介: ${author.description}")
                }
                for (item in author.items) {
                    bot says {
                        val thumb = try {
                            val bytes = http.get(item.thumb).body<ByteArray>()
                            bytes.toExternalResource().use { resource ->
                                subject.uploadImage(resource)
                            }
                        } catch (cause: Exception) {
                            logger.warning({ "下载表情图片失败" }, cause)
                            emptyMessageChain()
                        }
                        append(thumb).appendLine()
                        appendLine("名称: ${item.name}")
                        appendLine("链接: https://zb.vip.qq.com/hybrid/emoticonmall/detail?id=${item.itemId}")
                    }
                }
            }
        } catch (_: NoClassDefFoundError) {
            "请安装 https://github.com/cssxsh/meme-helper".toPlainText()
        } catch (cause: Exception) {
            logger.warning({ "获取表情列表失败" }, cause)
            "获取表情列表失败".toPlainText()
        }
        sendMessage(message)
    }

    @SubCommand("info", "信息")
    @Description("表情列表")
    public suspend fun UserCommandSender.info() {
        val message = try {
            val info = MarketFaceHelper.querySupplierInfo(supplierId = SUPPLIER_ID, offset = 0)
            buildForwardMessage(subject) {
                val head = try {
                    val bytes = http.get(info.face).body<ByteArray>()
                    bytes.toExternalResource().use { resource ->
                        subject.uploadImage(resource)
                    }
                } catch (cause: Exception) {
                    logger.warning({ "下载作者图片失败" }, cause)
                    emptyMessageChain()
                }
                bot says {
                    append(head).appendLine()
                    appendLine("作者: ${info.name}")
                    appendLine("qq群: ${info.group}")
                    appendLine("简介: ${info.description}")
                }
                for (offset in 0 until info.workNum step 30) {
                    val current = MarketFaceHelper.querySupplierInfo(supplierId = SUPPLIER_ID, offset = offset)
                    bot says buildForwardMessage(subject) {
                        for (item in current.items) {
                            val thumb = try {
                                val bytes = http.get(item.url).body<ByteArray>()
                                bytes.toExternalResource().use { resource ->
                                    subject.uploadImage(resource)
                                }
                            } catch (cause: Exception) {
                                logger.warning({ "下载表情图片失败" }, cause)
                                emptyMessageChain()
                            }
                            bot says {
                                append(thumb).appendLine()
                                appendLine("名称: ${item.name}")
                                appendLine("链接: https://zb.vip.qq.com/hybrid/emoticonmall/detail?id=${item.itemId}")
                            }
                        }
                    }
                }
            }
        } catch (_: NoClassDefFoundError) {
            "请安装 https://github.com/cssxsh/meme-helper".toPlainText()
        } catch (cause: Exception) {
            logger.warning({ "获取表情列表失败" }, cause)
            "获取表情列表失败".toPlainText()
        }
        sendMessage(message)
    }
}