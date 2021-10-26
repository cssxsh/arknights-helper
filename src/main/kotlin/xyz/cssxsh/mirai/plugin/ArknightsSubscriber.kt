package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.weibo.*
import java.time.*
import kotlin.math.*

private val Url.filename get() = encodedPath.substringAfterLast('/')

private fun OffsetDateTime.date() = toLocalDate().toString()

private suspend fun delay(duration: Duration) = delay(duration.toMillis())

private fun contacts(): List<Contact> {
    return try {
        Bot.instances.flatMap { bot -> (bot.friends + bot.groups).filter { it.delegate in GuardContacts } }
    } catch (e: Throwable) {
        emptyList()
    }
}

private suspend fun sendToTaskContacts(block: suspend MessageChainBuilder.(Contact) -> Unit) {
    for (contact in contacts()) {
        try {
            contact.sendMessage(buildMessageChain { block(contact) })
        } catch (e: Throwable) {
            logger.warning({ "向${contact}发送消息失败" }, e)
        }
    }
}

private suspend fun sendVideo(video: Video) = sendToTaskContacts { contact ->
    appendLine("鹰角有新视频了！")
    appendLine("链接: ${video.url}")
    appendLine("标题: ${video.title}")
    appendLine("时间: ${video.created}")
    if (video.description.isNotBlank()) {
        appendLine("简介：")
        appendLine(video.description)
    }

    try {
        val image = VideoData.dir.resolve(video.created.date()).resolve(video.cover.filename).apply {
            if (exists().not()) {
                parentFile.mkdirs()
                writeBytes(Downloader.useHttpClient { it.get(video.cover) })
            }
        }
        append(image.uploadAsImage(contact))
    } catch (e: Throwable) {
        appendLine("添加图片[${video.url}]失败, ${e.message}")
    }
}

private suspend fun MicroBlog.toMessage(contact: Contact): Message = buildMessageChain {
    try {
        appendLine(content())
    } catch (e: Throwable) {
        logger.warning({ "加载[${url}]长微博失败" }, e)
        appendLine(content)
    }

    for (url in images) {
        try {
            val file = MicroBlogData.dir.resolve(created.date()).resolve(url.filename).apply {
                if (exists().not()) {
                    parentFile.mkdirs()
                    writeBytes(Downloader.useHttpClient { it.get(url) })
                }
            }
            append(file.uploadAsImage(contact))
        } catch (e: Throwable) {
            appendLine("添加图片[${url}]失败, ${e.message}")
        }
    }
}

private suspend fun sendMicroBlog(blog: MicroBlog) = sendToTaskContacts { contact ->
    appendLine("鹰角有新微博！@${blog.user?.name ?: "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看"}")
    appendLine("时间: ${blog.created}")
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
            writeText(Downloader.useHttpClient { it.get(web) })
        }
    }
    val body = html.readText().substringAfter("<body>").replace("<br/>", "\n")
    for (result in ContentRegex.findAll(body)) {
        if (result.value.startsWith("http")) {
            val url = Url(result.value)
            val image = AnnouncementData.dir.resolve("html/${url.filename}").apply {
                if (exists().not()) {
                    parentFile.mkdirs()
                    writeBytes(Downloader.useHttpClient { it.get(url) })
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
    try {
        val user = requireNotNull(findContact(id)) { "未找到用户" }
        val massage = " 理智警告 ".toPlainText()
        if (user is Member) {
            user.group.sendMessage(massage + At(user))
        } else {
            user.sendMessage(massage)
        }
    } catch (e: Throwable) {
        logger.warning({ "定时器播报失败" }, e)
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
        ArknightsUserData.recruit[id] = ArknightsUserData.recruit[id] + (site to 0)
    }.onFailure {
        logger.warning({ "定时器播报失败" }, it)
    }
}

internal fun downloadGameData(): Unit = runBlocking {
    awaitAll(
        async {
            ExcelData.download(flush = false)
            val old = SemVersion(ExcelData.version.versionControl)
            val now = SemVersion(ExcelDataVersion().versionControl)
            if (now > old) ExcelData.download(flush = true)
            logger.info { "ExcelData 数据加载完毕, 版本 $now" }
        },
        async {
            PenguinData.download(flush = true)
            logger.info { "PenguinData 数据加载完毕" }
        },
        async {
            VideoData.download(flush = false)
            val id = VideoData.all.maxByOrNull { it.created }?.bvid
            logger.info { "VideoData 数据加载完毕, last: $id" }
        },
        async {
            MicroBlogData.download(flush = false)
            val last = MicroBlogData.all.maxOfOrNull { it.created }
            logger.info { "MicroBlogData 数据加载完毕, last: $last" }
        },
        async {
            ArknightsFaceData.download(flush = false)
            logger.info { "ArknightsFaceData 数据加载完毕" }
        },
        async {
            AnnouncementData.download(flush = false)
            logger.info { "AnnouncementData 数据加载完毕" }
        }
    )
}

private val Fast = Duration.ofMinutes(3)

private val Slow = Duration.ofMinutes(10)

private val Start = LocalTime.of(7, 30, 0)

private val End = LocalTime.of(22, 0, 0)

private operator fun LocalTime.minus(other: LocalTime): Duration =
    Duration.ofSeconds((toSecondOfDay() - other.toSecondOfDay()).toLong())

private fun OffsetDateTime.isToday(): Boolean = (toLocalDate() == LocalDate.now())

private suspend fun waitContacts() = supervisorScope {
    while (isActive && contacts().isEmpty()) {
        logger.verbose { "蹲饼联系人没有实例，进入${Fast}等待" }
        delay(Fast)
    }
}

@OptIn(ConsoleExperimentalApi::class)
internal object ArknightsSubscriber : CoroutineScope by ArknightsHelperPlugin.childScope("ArknightsSubscriber") {

    private fun clock() = launch {
        waitContacts()
        logger.info { "明日方舟 定时器 订阅器开始运行" }
        while (isActive) {
            for ((id, timestamp) in ArknightsUserData.reason) {
                if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed) {
                    launch {
                        sendReasonClock(id)
                    }
                }
            }
            for ((id, sites) in ArknightsUserData.recruit) {
                for ((site, timestamp) in sites) {
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
        val history = VideoData.all.mapTo(mutableSetOf()) { it.bvid }
        var updated = false
        val list = VideoData.all.map { it.created.toLocalTime() }.sorted()
        val start = list.minOrNull() ?: Start
        val end = list.maxOrNull() ?: End
        if (LocalTime.now() < start) delay(start - LocalTime.now())
        waitContacts()
        logger.info { "明日方舟 哔哩哔哩 订阅器开始运行" }
        while (isActive) {

            runCatching {
                VideoData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 VideoData 数据加载完毕" }
            }.onFailure {
                if ("请求被拦截" in it.message.orEmpty()) {
                    delay(Slow.toMillis())
                }
                logger.warning({ "订阅器 VideoData 数据加载失败" }, it)
            }
            val new = VideoData.all.filterNot { it.bvid in history }.sortedBy { it.created }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 哔哩哔哩  订阅器 捕捉到结果" }
                for (video in new) {
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
        val history = MicroBlogData.all.mapTo(mutableSetOf())  { it.id }
        if (LocalTime.now() < Start) delay(Start - LocalTime.now())
        waitContacts()
        logger.info { "明日方舟 微博 订阅器开始运行" }
        while (isActive) {

            runCatching {
                MicroBlogData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 MicroBlogData 数据加载完毕" }
            }.onFailure {
                logger.warning({ "订阅器 MicroBlogData 数据加载失败" }, it)
            }

            val new = with(MicroBlogData) {
                val max = arknights.maxOfOrNull { it.id } ?: Long.MAX_VALUE
                all.filterNot { it.id in history } + picture.filterNot { it.id <= max && it.id in history }
            }.filter { blog -> blog.created.isToday() }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 微博 订阅器 捕捉到结果" }
                for (blog in new.sortedBy { it.id }) {
                    if (blog.id in history) continue
                    runCatching {
                        sendMicroBlog(blog)
                    }.onSuccess {
                        history.add(blog.id)
                    }.onFailure {
                        logger.warning({ "微博[${blog.id}]推送失败" }, it)
                    }
                }
            }

            if (LocalTime.now() > End) {
                logger.info { "明日方舟 微博 订阅器进入休眠" }
                delay(Duration.ofDays(1) - (LocalTime.now() - Start))
            }

            if (LocalTime.now().minute in (55..59) + (0..3)) {
                delay(Duration.ofSeconds(30))
                continue
            }

            delay(Duration.ofMinutes(GuardInterval.toLong()))
        }
    }

    private fun announce() = launch {
        val history = AnnouncementData.all.mapTo(mutableSetOf()) { it.id }
        if (LocalTime.now() < Start) delay(Start - LocalTime.now())
        waitContacts()
        logger.info { "明日方舟 公告 订阅器开始运行" }
        while (isActive) {

            runCatching {
                AnnouncementData.download(flush = true)
            }.onSuccess {
                logger.info { "订阅器 AnnouncementData 数据加载完毕" }
            }.onFailure {
                logger.warning({ "订阅器 AnnouncementData 数据加载失败" }, it)
            }

            val new = AnnouncementData.all.filterNot { it.id in history }.sortedBy { it.id }
            if (new.isNotEmpty()) {
                logger.info { "明日方舟 公告 订阅器 捕捉到结果" }
                for (announcement in new) {
                    if (announcement.id in history || announcement.date != LocalDate.now()) continue
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

    private fun group() {
        if (AutoAddGuard) globalEventChannel().subscribeAlways<BotJoinGroupEvent> {
            GuardContacts.add(group.delegate)
            group.sendMessage("机器人加添加群组，已自动开启蹲饼")
        }
    }

    private fun friend() {
        if (AutoAddGuard) globalEventChannel().subscribeAlways<FriendAddEvent> {
            GuardContacts.add(friend.delegate)
            friend.sendMessage("机器人加添加好友，已自动开启蹲饼")
        }
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