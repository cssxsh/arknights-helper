package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.BotJoinGroupEvent
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.useHttpClient
import xyz.cssxsh.arknights.weibo.*
import java.time.LocalTime
import java.time.OffsetDateTime
import kotlin.math.abs
import kotlin.time.*

@Suppress("FunctionName")
private fun UserOrNull(id: Long): User? {
    Bot.instances.forEach { bot ->
        bot.getFriend(id)?.let { return@UserOrNull it }
        bot.getStranger(id)?.let { return@UserOrNull it }
        bot.groups.forEach { group ->
            group.getMember(id)?.let { return@UserOrNull it }
        }
    }
    return null
}

private val Url.filename get() = encodedPath.substringAfterLast('/')

private fun OffsetDateTime.date() = toLocalDate().toString()

private suspend fun sendToTaskContacts(block: suspend MessageChainBuilder.(Contact) -> Unit) {
    Bot.instances.flatMap { bot ->
        (bot.friends + bot.groups).filter { it.delegate in GuardContacts }
    }.forEach { contact ->
        runCatching {
            contact.sendMessage(buildMessageChain { block(contact) })
        }.onFailure {
            logger.warning({ "向${contact}发送消息失败" }, it)
        }
    }
}

private suspend fun sendVideo(video: Video) = sendToTaskContacts { contact ->
    appendLine("鹰角有新视频了！")
    appendLine("链接: ${video.url}")
    appendLine("标题: ${video.title}")
    appendLine("简介：${video.description}")

    runCatching {
        val image = BilibiliData.resolve(video.created.date()).resolve(video.url.filename).apply {
            if (exists().not()) {
                mkdirs()
                writeBytes(useHttpClient { it.get(video.pic) })
            }
        }
        append(image.uploadAsImage(contact))
    }.onFailure {
        appendLine("添加图片[${video.url}]失败")
    }
}

private suspend fun sendMicroBlog(blog: MicroBlog) = sendToTaskContacts { contact ->
    appendLine("鹰角有新微博！")
    appendLine("链接: ${blog.url}")

    runCatching {
        appendLine(blog.content())
    }.onFailure {
        logger.warning({ "加载[${blog.url}]长微博失败" }, it)
        appendLine(blog.content)
    }

    blog.images.forEach { url ->
        runCatching {
            val file = WeiboData.resolve(blog.createdAt.date()).resolve(url.filename).apply {
                if (exists().not()) {
                    mkdirs()
                    writeBytes(useHttpClient { it.get(url) })
                }
            }
            append(file.uploadAsImage(contact))
        }.onFailure {
            appendLine("添加图片[${url}]失败")
        }
    }
}

private suspend fun sendReasonClock(id: Long) {
    runCatching {
        val user = requireNotNull(UserOrNull(id)) { "未找到用户" }
        val massage = " 理智警告 ".toPlainText()
        if (user is Member) {
            user.group.sendMessage(massage + At(user))
        } else {
            user.sendMessage(massage)
        }
    }.onFailure {
        logger.warning({ "定时器播报失败" }, it)
    }
}

private suspend fun sendRecruitClock(id: Long, site: Int) {
    runCatching {
        val user = requireNotNull(UserOrNull(id)) { "未找到用户" }
        val massage = " 公招位置${site}警告 ".toPlainText()
        if (user is Member) {
            user.group.sendMessage(massage + At(user))
        } else {
            user.sendMessage(massage)
        }
    }.onSuccess {
        ArknightsUserData.recruit[id] = ArknightsUserData.recruit[id].toMutableMap().apply {
            put(site, 0)
        }
    }.onFailure {
        logger.warning({ "定时器播报失败" }, it)
    }
}

internal fun CoroutineScope.clock(interval: Duration = (1).minutes) = launch {
    while (isActive) {
        ArknightsUserData.reason.forEach { (id, timestamp) ->
            if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed.toLongMilliseconds()) {
                launch {
                    sendReasonClock(id)
                }
            }
        }
        ArknightsUserData.recruit.forEach { (id, sites) ->
            sites.forEach { (site, timestamp) ->
                if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed.toLongMilliseconds()) {
                    launch {
                        sendRecruitClock(id, site)
                    }
                }
            }
        }
        delay(interval)
    }
}

internal fun downloadExternalData(): Unit = runBlocking {
    runCatching {
        downloadExcelData(flush = false)
    }.onSuccess {
        logger.info { "ArknightsGameData $ARKNIGHTS_EXCEL_DATA 数据加载完毕" }
    }.onFailure {
        logger.warning({ "ArknightsGameData $ARKNIGHTS_EXCEL_DATA 数据加载失败" }, it)
    }

    runCatching {
        downloadPenguinData(flush = false)
    }.onSuccess {
        logger.info { "PenguinStats $PENGUIN_DATA 数据加载完毕" }
    }.onFailure {
        logger.warning({ "PenguinStats $PENGUIN_DATA 数据加载失败" }, it)
    }

    runCatching {
        downloadVideoData(flush = false)
    }.onSuccess {
        logger.info { "BilibiliData $BILIBILI_VIDEO 数据加载完毕" }
    }.onFailure {
        logger.warning({ "BilibiliData $BILIBILI_VIDEO 数据加载失败" }, it)
    }


    runCatching {
        downloadMicroBlogData(flush = false)
    }.onSuccess {
        logger.info { "WeiboData $MICRO_BLOG_USER 数据加载完毕" }
    }.onFailure {
        logger.warning({ "WeiboData $MICRO_BLOG_USER 数据加载失败" }, it)
    }
}

internal fun CoroutineScope.subscribe(fast: Duration = (1).minutes, slow: Duration = (5).minutes) = launch {
    var last = VideoData.all.maxOfOrNull { it.created } ?: OffsetDateTime.now()
    var updated = false
    val list = VideoData.all.map { it.created.toLocalTime() }.sorted()
    val start = list.minOrNull() ?: LocalTime.of(9, 0, 0)
    val end = list.maxOrNull() ?: LocalTime.of(22, 0, 0)
    if (LocalTime.now() < start) delay((start - LocalTime.now()).seconds)
    logger.info { "明日方舟 哔哩哔哩 订阅器开始运行" }
    while (isActive) {
        if (LocalTime.now() > end) {
            logger.info { "哔哩哔哩 明日方舟 订阅器结束运行" }
            return@launch
        }

        runCatching {
            downloadVideoData(flush = true)
        }.onSuccess {
            logger.info { "订阅器 BilibiliData $BILIBILI_VIDEO 数据加载完毕" }
        }.onFailure {
            logger.warning({ "订阅器 BilibiliData $BILIBILI_VIDEO 数据加载失败" }, it)
        }
        val new = VideoData.all.filter { it.created > last }
        if (new.isNotEmpty()) {
            logger.info { "哔哩哔哩 明日方舟 订阅器 捕捉到结果" }
            launch {
                new.sortedBy { it.created }.forEach { video ->
                    runCatching {
                        sendVideo(video)
                    }
                }
            }
            updated = true
            last = new.maxOfOrNull { it.created }!!
        }

        if (updated.not() && list.any { abs(it - LocalTime.now()) < slow.inSeconds }) {
            delay(fast)
        } else {
            delay(slow)
        }
    }
}

internal fun CoroutineScope.guard() = launch {
    var last = MicroBlogData.arknights.maxOfOrNull { it.createdAt } ?: OffsetDateTime.now()
    val start = LocalTime.of(9, 0, 0)
    val end = LocalTime.of(22, 0, 0)
    if (LocalTime.now() < start) delay((start - LocalTime.now()).seconds)
    logger.info { "明日方舟 微博 订阅器开始运行" }
    while (isActive) {
        if (LocalTime.now() > end) {
            logger.info { "明日方舟 微博 订阅器结束运行" }
            return@launch
        }

        runCatching {
            downloadMicroBlogData(flush = true)
        }.onSuccess {
            logger.info { "订阅器 WeiboData $MICRO_BLOG_USER 数据加载完毕" }
        }.onFailure {
            logger.warning({ "订阅器 WeiboData $MICRO_BLOG_USER 数据加载失败" }, it)
        }

        val new = MicroBlogData.arknights.filter { it.createdAt > last }
        if (new.isNotEmpty()) {
            logger.info { "哔哩哔哩 微博 订阅器 捕捉到结果" }
            launch {
                new.sortedBy { it.createdAt }.forEach { blog ->
                    runCatching {
                        sendMicroBlog(blog)
                    }
                }
            }
            last = new.maxOfOrNull { it.createdAt }!!
        }
        delay(GuardInterval)
    }
}

internal fun CoroutineScope.group() = globalEventChannel().subscribeAlways<BotJoinGroupEvent> {
    GuardContacts.add(group.delegate)
    group.sendMessage("机器人加添加群组，已自动开启蹲饼")
}

internal fun CoroutineScope.friend() = globalEventChannel().subscribeAlways<FriendAddEvent> {
    GuardContacts.add(friend.delegate)
    friend.sendMessage("机器人加添加好友，已自动开启蹲饼")
}