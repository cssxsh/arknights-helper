package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.console.util.SemVersion
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.BotJoinGroupEvent
import net.mamoe.mirai.event.events.FriendAddEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.loadExcelDataVersion
import xyz.cssxsh.arknights.useHttpClient
import xyz.cssxsh.arknights.weibo.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import kotlin.math.abs

private val Url.filename get() = encodedPath.substringAfterLast('/')

private fun OffsetDateTime.date() = toLocalDate().toString()

private suspend fun delay(duration: Duration) = delay(duration.toMillis())

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
    appendLine("发布时间: ${video.created}")

    runCatching {
        val image = VideoData.dir.resolve(video.created.date()).resolve(video.cover.filename).apply {
            if (exists().not()) {
                parentFile.mkdirs()
                writeBytes(useHttpClient { it.get(video.cover) })
            }
        }
        append(image.uploadAsImage(contact))
    }.onFailure {
        appendLine("添加图片[${video.url}]失败, ${it.message}")
    }
}

private suspend fun MicroBlog.toMessage(contact: Contact): Message = buildMessageChain {
    runCatching {
        appendLine(content())
    }.onFailure {
        logger.warning({ "加载[${url}]长微博失败" }, it)
        appendLine(content)
    }

    images.forEach { url ->
        runCatching {
            val file = MicroBlogData.dir.resolve(createdAt.date()).resolve(url.filename).apply {
                if (exists().not()) {
                    parentFile.mkdirs()
                    writeBytes(useHttpClient { it.get(url) })
                }
            }
            append(file.uploadAsImage(contact))
        }.onFailure {
            appendLine("添加图片[${url}]失败, ${it.message}")
        }
    }
}

private suspend fun sendMicroBlog(blog: MicroBlog) = sendToTaskContacts { contact ->
    appendLine("鹰角有新微博！@${blog.user?.name}")
    appendLine("时间: ${blog.createdAt}")
    appendLine("链接: ${blog.url}")

    append(blog.toMessage(contact))

    blog.retweeted?.let { retweeted ->
        appendLine("----------------")
        appendLine("@${retweeted.user?.name}")

        append(retweeted.toMessage(contact))
    }
}

private val ContentRegex = """((?<=>)[^<]+(?=</))|(https://[^<]+\.(jpg|png|gif))""".toRegex()

private suspend fun Announcement.toMessage(contact: Contact): Message = buildMessageChain {
    val html = AnnouncementData.dir.resolve("html/${web.filename}").apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeText(useHttpClient { it.get(web) })
        }
    }
    ContentRegex.findAll(html.readText().substringAfter("main")).forEach { result ->
        if (result.value.startsWith("http")) {
            val url = Url(result.value)
            val image = AnnouncementData.dir.resolve("html/${url.filename}").apply {
                if (exists().not()) {
                    parentFile.mkdirs()
                    writeBytes(useHttpClient { it.get(url) })
                }
            }
            append(image.uploadAsImage(contact))
        } else {
            appendLine(result.value.trim())
        }
    }
}

private suspend fun sendAnnouncement(info: Announcement) = sendToTaskContacts { contact ->
    appendLine("鹰角有新公告！${info.title}")
    appendLine("日期: ${info.date}")
    appendLine("分类: ${info.group}")
    appendLine("链接: ${info.webUrl}")

    append(info.toMessage(contact))
}

private suspend fun sendReasonClock(id: Long) {
    runCatching {
        val user = requireNotNull(findContact(id)) { "未找到用户" }
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
        val user = requireNotNull(findContact(id)) { "未找到用户" }
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

internal fun downloadGameData(): Unit = runBlocking {
    runCatching {
        ExcelData.download(flush = false)
        val old = SemVersion.invoke(ExcelData.version.versionControl)
        val now = SemVersion.invoke(loadExcelDataVersion().versionControl)
        if (now > old) {
            ExcelData.download(flush = true)
            now
        } else {
            old
        }
    }.onSuccess {
        logger.info { "ExcelData 数据加载完毕, 版本 $it" }
    }.onFailure {
        logger.warning({ "ExcelData 数据加载失败" }, it)
    }

    runCatching {
        PenguinData.download(flush = true)
    }.onSuccess {
        logger.info { "PenguinData 数据加载完毕" }
    }.onFailure {
        logger.warning({ "PenguinData 数据加载失败" }, it)
    }

    runCatching {
        VideoData.download(flush = false)
    }.onSuccess {
        logger.info { "VideoData 数据加载完毕" }
    }.onFailure {
        logger.warning({ "VideoData 数据加载失败" }, it)
    }

    runCatching {
        MicroBlogData.download(flush = false)
    }.onSuccess {
        logger.info { "MicroBlogData 数据加载完毕" }
    }.onFailure {
        logger.warning({ "MicroBlogData 数据加载失败" }, it)
    }

    runCatching {
        ArknightsFaceData.download(flush = false)
    }.onSuccess {
        logger.info { "ArknightsFaceData 数据加载完毕" }
    }.onFailure {
        logger.warning({ "ArknightsFaceData 数据加载失败" }, it)
    }

    runCatching {
        AnnouncementData.download(flush = false)
    }.onSuccess {
        logger.info { "AnnouncementData 数据加载完毕" }
    }.onFailure {
        logger.warning({ "AnnouncementData 数据加载失败" }, it)
    }
}

private val Fast = Duration.ofMinutes(3)

private val Slow = Duration.ofMinutes(10)

private val Start = LocalTime.of(7, 30, 0)

private val End = LocalTime.of(22, 0, 0)

private operator fun LocalTime.minus(other: LocalTime): Duration =
    Duration.ofSeconds((toSecondOfDay() - other.toSecondOfDay()).toLong())

private suspend fun CoroutineScope.waitBotImpl() {
    while (isActive && Bot.instances.isEmpty()) {
        logger.verbose { "机器人没有实例，进入${Fast}等待" }
        delay(Fast)
    }
}

internal object ArknightsSubscriber : CoroutineScope by ArknightsHelperPlugin.childScope("ArknightsSubscriber") {

    private fun clock() = launch {
        waitBotImpl()
        logger.info { "明日方舟 定时器 订阅器开始运行" }
        while (isActive) {
            ArknightsUserData.reason.forEach { (id, timestamp) ->
                if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed) {
                    launch {
                        sendReasonClock(id)
                    }
                }
            }
            ArknightsUserData.recruit.forEach { (id, sites) ->
                sites.forEach { (site, timestamp) ->
                    if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed) {
                        launch {
                            sendRecruitClock(id, site)
                        }
                    }
                }
            }
            delay(Fast)
        }
    }

    private fun bilibili() = launch {
        val history = VideoData.all.map { it.bvid }.toMutableSet()
        var updated = false
        val list = VideoData.all.map { it.created.toLocalTime() }.sorted()
        val start = list.minOrNull() ?: Start
        val end = list.maxOrNull() ?: End
        if (LocalTime.now() < start) delay(start - LocalTime.now())
        waitBotImpl()
        logger.info { "明日方舟 哔哩哔哩 订阅器开始运行" }
        while (isActive) {

            runCatching {
                VideoData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 VideoData 数据加载完毕" }
            }.onFailure {
                logger.warning({ "订阅器 VideoData 数据加载失败" }, it)
            }
            val new = VideoData.all.filterNot { it.bvid in history }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 哔哩哔哩  订阅器 捕捉到结果" }
                new.sortedBy { it.created }.forEach { video ->
                    runCatching {
                        sendVideo(video)
                    }.onSuccess {
                        history.add(video.bvid)
                    }
                }
                updated = true
            }

            if (LocalTime.now() > end) {
                logger.info { "明日方舟 哔哩哔哩 订阅器进入休眠" }
                delay(Duration.ofDays(1) - (LocalTime.now() - start))
            }

            if (updated.not() && list.any { (it - LocalTime.now()).abs() < Slow }) {
                delay(Fast)
            } else {
                delay(Slow)
            }
        }
    }

    private fun weibo() = launch {
        val history = MicroBlogData.all.map { it.id }.toMutableSet()
        if (LocalTime.now() < Start) delay((Start - LocalTime.now()))
        waitBotImpl()
        logger.info { "明日方舟 微博 订阅器开始运行" }
        while (isActive) {

            runCatching {
                MicroBlogData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 MicroBlogData 数据加载完毕" }
            }.onFailure {
                logger.warning({ "订阅器 MicroBlogData 数据加载失败" }, it)
            }

            val new = MicroBlogData.all.filterNot { it.id in history }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 微博 订阅器 捕捉到结果" }
                new.sortedBy { it.id }.forEach { blog ->
                    if (blog.id in history || blog.createdAt.toLocalDate() != LocalDate.now()) return@forEach
                    runCatching {
                        sendMicroBlog(blog)
                    }.onSuccess {
                        history.add(blog.id)
                    }
                }
            }

            if (LocalTime.now() > End) {
                logger.info { "明日方舟 微博 订阅器进入休眠" }
                delay(Duration.ofDays(1) - (LocalTime.now() - Start))
            }

            delay(Duration.ofMinutes(GuardInterval.toLong()))
        }
    }

    private fun announce() = launch {
        val history = AnnouncementData.all.map { it.id }.toMutableSet()
        if (LocalTime.now() < Start) delay((Start - LocalTime.now()))
        waitBotImpl()
        logger.info { "明日方舟 公告 订阅器开始运行" }
        while (isActive) {

            runCatching {
                AnnouncementData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 AnnouncementData 数据加载完毕" }
            }.onFailure {
                logger.warning({ "订阅器 AnnouncementData 数据加载失败" }, it)
            }

            val new = AnnouncementData.all.filterNot { it.id in history }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 公告 订阅器 捕捉到结果" }
                new.sortedBy { it.id }.forEach { announcement ->
                    if (announcement.id in history) return@forEach
                    runCatching {
                        sendAnnouncement(announcement)
                    }.onSuccess {
                        history.add(announcement.id)
                    }
                }
            }

            if (LocalTime.now() > End) {
                logger.info { "明日方舟 公告 订阅器进入休眠" }
                delay(Duration.ofDays(1) - (LocalTime.now() - Start))
            }

            delay(Fast)
        }
    }

    private fun group() = globalEventChannel().subscribeAlways<BotJoinGroupEvent> {
        GuardContacts.add(group.delegate)
        group.sendMessage("机器人加添加群组，已自动开启蹲饼")
    }

    private fun friend() = globalEventChannel().subscribeAlways<FriendAddEvent> {
        GuardContacts.add(friend.delegate)
        friend.sendMessage("机器人加添加好友，已自动开启蹲饼")
    }

    fun start() {
        clock()
        bilibili()
        weibo()
        announce()
        group()
        friend()
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}