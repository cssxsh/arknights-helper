package xyz.cssxsh.mirai.arknights

import com.cronutils.model.*
import io.ktor.client.network.sockets.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.mirai.arknights.data.*
import java.time.LocalDate
import kotlin.coroutines.*
import kotlin.io.path.Path

public object ArknightsSubscriber : SimpleListenerHost() {
    private val files: PluginFileExtensions by lazy {
        try {
            ArknightsHelperPlugin.dataFolder
            ArknightsHelperPlugin
        } catch (_: Throwable) {
            object : PluginFileExtensions {
                override val configFolder by lazy { configFolderPath.toFile() }
                override val configFolderPath by lazy { Path(System.setProperty("arknights.config", "./config")) }
                override val dataFolder by lazy { dataFolderPath.toFile() }
                override val dataFolderPath by lazy { Path(System.setProperty("arknights.data", "./data")) }
            }
        }
    }
    private val ignore: suspend (Throwable) -> Boolean = { cause ->
        when (cause) {
            // 路由问题，重连可以解决
            is java.net.UnknownHostException, is javax.net.ssl.SSLException -> {
                false
            }
            // 缩短信息文本
            is SocketTimeoutException, is ConnectTimeoutException -> {
                logger.warning { cause.message ?: "Timeout" }
                true
            }
            // IOException 直接忽略
            is java.io.IOException -> {
                logger.warning({ "Downloader IOException" }, cause)
                true
            }
            // 其他问题甩出
            else -> {
                false
            }
        }
    }
    private val flow: MutableSharedFlow<CacheInfo> = MutableSharedFlow()
    internal val videos: VideoDataHolder by lazy {
        VideoDataHolder(files.resolveDataFile("BilibiliData").apply { mkdirs() }, ignore)
    }
    internal val blogs: MicroBlogDataHolder by lazy {
        MicroBlogDataHolder(files.resolveDataFile("WeiboData").apply { mkdirs() }, ignore)
    }
    internal val announcements: AnnouncementDataHolder by lazy {
        AnnouncementDataHolder(files.resolveDataFile("AnnouncementData").apply { mkdirs() }, ignore)
    }
    public val shared: SharedFlow<CacheInfo> = flow.asSharedFlow()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        when (exception) {
            is CancellationException -> {
                // ignore
            }
            is ExceptionInEventHandlerException -> {
                logger.warning({ "exception in ${exception.event::class.simpleName}" }, exception.cause)
            }
            else -> {
                logger.warning({ "exception in arknights-subscriber" }, exception)
            }
        }
    }

    private fun CacheInfo.isToday(): Boolean = (created.toLocalDate() == LocalDate.now())

//    private fun clock() = launch {
//        logger.info { "明日方舟 定时器 订阅器开始运行" }
//        while (isActive) {
//            for ((id, timestamp) in ArknightsUserData.reason) {
//                if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed) {
//                    launch {
//                        sendReasonClock(id)
//                    }
//                }
//            }
//            for ((id, sites) in ArknightsUserData.recruit) {
//                for ((site, timestamp) in sites) {
//                    if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed) {
//                        launch {
//                            sendRecruitClock(id, site)
//                        }
//                    }
//                }
//            }
//            delay(Fast)
//        }
//    }
//
    internal fun video() = launch {
        val history: MutableSet<String> = HashSet()
        val cron: (VideoType) -> Cron = { ArknightsCronConfig.video[it] ?: ArknightsCronConfig.default }
        runBlocking {
            videos.raw().forEach {
                history.add(it.bvid)
            }
        }
        for (type in VideoType.values()) {
            launch {
                while (isActive) {
                    delay(10_000)
                    delay(cron(type).next())
                    // 加载
                    try {
                        videos.load(key = type)
                    } catch (cause: Throwable) {
                        logger.warning({ "明日方舟 视频 $type 数据加载失败" }, cause)
                        continue
                    }
                    // 推送
                    val new = videos.raw()
                        .filter { video -> video.isToday() && video.bvid !in history }
                        .sortedBy { it.created }
                    if (new.isEmpty()) continue
                    logger.info { "明日方舟 视频 $type 捕捉到结果" }
                    for (video in new) {
                        try {
                            flow.emit(video)
                            history.add(video.bvid)
                        } catch (cause: Throwable) {
                            logger.warning({ "视频[${video.bvid}]推送失败" }, cause)
                        }
                    }
                }
            }
        }
    }

    private fun weibo() {
        val history: MutableSet<Long> = HashSet()
        val cron: (BlogUser) -> Cron = { ArknightsCronConfig.blog[it] ?: ArknightsCronConfig.default }
        runBlocking {
            blogs.raw().forEach {
                history.add(it.id)
            }
        }
        for (user in BlogUser.values()) {
            launch {
                while (isActive) {
                    delay(10_000)
                    delay(cron(user).next())
                    // 加载
                    try {
                        blogs.load(key = user)
                    } catch (cause: Throwable) {
                        logger.warning({ "明日方舟 微博 $user 数据加载失败" }, cause)
                        continue
                    }
                    // 推送
                    val raw = blogs.raw()
                        .filter { blog -> blog.isToday() && blog.id !in history }
                        .sortedBy { it.created }
                    if (raw.isEmpty()) continue
                    logger.info { "明日方舟 微博 $user 捕捉到结果" }
                    for (blog in raw) {
                        try {
                            flow.emit(blog)
                            history.add(blog.id)
                        } catch (cause: Throwable) {
                            logger.warning({ "微博[${blog.id}]推送失败" }, cause)
                        }
                    }
                }
            }
        }
    }

    private fun announce() {
        val history: MutableSet<Int> = HashSet()
        val cron: (AnnounceType) -> Cron = { ArknightsCronConfig.announce }
        runBlocking {
            announcements.raw().forEach {
                history.add(it.id)
            }
        }
        for (type in AnnounceType.values()) {
            launch {
                while (isActive) {
                    delay(10_000)
                    delay(cron(type).next())
                    // 加载
                    try {
                        announcements.load(key = type)
                    } catch (cause: Throwable) {
                        logger.warning({ "明日方舟 公告 $type 数据加载失败" }, cause)
                        continue
                    }
                    // 推送

                    val new = announcements.raw()
                        .filter { announcement -> announcement.isToday() && announcement.id !in history }
                        .sortedBy { it.id }
                    if (new.isEmpty()) continue
                    logger.info { "明日方舟 公告 $type 捕捉到结果" }
                    for (announcement in new) {
                        try {
                            flow.emit(announcement)
                            history.add(announcement.id)
                        } catch (cause: Throwable) {
                            logger.warning({ "公告[${announcement.webUrl}]推送失败" }, cause)
                        }
                    }
                }
            }
        }
    }

//    private fun group() {
//        if (AutoAddGuard) globalEventChannel().subscribeAlways<BotJoinGroupEvent> {
//            GuardContacts.add(group.delegate)
//            group.sendMessage("机器人加添加群组，已自动开启蹲饼")
//        }
//    }
//
//    private fun friend() {
//        if (AutoAddGuard) globalEventChannel().subscribeAlways<FriendAddEvent> {
//            GuardContacts.add(friend.delegate)
//            friend.sendMessage("机器人加添加好友，已自动开启蹲饼")
//        }
//    }

    public fun start() {
        video()
        weibo()
        announce()
    }

    public fun listen(contact: Contact) {
        contact.launch(coroutineContext) {
            ArknightsCollector(contact).emitAll(flow)
        }
    }

    @EventHandler
    public fun BotOnlineEvent.online() {
        for (group in bot.groups) {
            // TODO: check
            listen(contact = group)
        }
        for (friend in bot.friends) {
            if (friend.delegate !in ArknightsTaskConfig.contacts) continue
            listen(contact = friend)
        }
    }

    @EventHandler
    public fun BotJoinGroupEvent.join() {
        // TODO: check
        listen(contact = group)
        launch {
            delay(60_000)
            group.sendMessage("机器人加添加群组，已自动开启蹲饼")
        }
    }

    @EventHandler
    public fun FriendAddEvent.add() {
        // TODO: check
        listen(contact = friend)
        launch {
            delay(60_000)
            friend.sendMessage("机器人加添加好友，已自动开启蹲饼")
        }
    }
}