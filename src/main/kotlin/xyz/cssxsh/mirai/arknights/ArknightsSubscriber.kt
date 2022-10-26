package xyz.cssxsh.mirai.arknights

import com.cronutils.model.*
import io.ktor.client.network.sockets.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.penguin.*
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
        } catch (_: ExceptionInInitializerError) {
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
            // 路由问题/协议问题，重连可以解决
            is java.net.UnknownHostException,
            is java.net.NoRouteToHostException,
            is javax.net.ssl.SSLException,
            is okhttp3.internal.http2.StreamResetException -> {
                false
            }
            // 缩短信息文本
            is SocketTimeoutException,
            is ConnectTimeoutException -> {
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

    public val videos: VideoDataHolder by lazy {
        VideoDataHolder(files.resolveDataFile("BilibiliData").apply { mkdirs() }, ignore)
    }
    public val blogs: MicroBlogDataHolder by lazy {
        MicroBlogDataHolder(files.resolveDataFile("WeiboData").apply { mkdirs() }, ignore)
    }
    public val announcements: AnnouncementDataHolder by lazy {
        AnnouncementDataHolder(files.resolveDataFile("AnnouncementData").apply { mkdirs() }, ignore)
    }
    public val penguin: PenguinDataHolder by lazy {
        PenguinDataHolder(files.resolveDataFile("PenguinStats").apply { mkdirs() }, ignore)
    }
    public val excel: ExcelDataHolder by lazy {
        ExcelDataHolder(files.resolveDataFile("ArknightsGameData").apply { mkdirs() }, ignore)
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

    private fun clock() {
        // TODO: 剿灭
        // TODO: 线索需求推送
    }

    private fun video() {
        val history: MutableSet<String> = HashSet()
        val cron: (VideoType) -> Cron = { ArknightsCronConfig.video[it] ?: ArknightsCronConfig.default }
        for (type in VideoType.values()) {
            launch {
                for (video in videos.raw(type)) {
                    history.add(video.bvid)
                }
                while (isActive) {
                    delay(10_000)
                    delay(cron(type).next())
                    // 加载
                    try {
                        videos.load(key = type)
                    } catch (cause: Exception) {
                        logger.warning({ "明日方舟 视频 $type 数据加载失败" }, cause)
                        continue
                    }
                    // 推送
                    val new = videos.raw(type)
                        .filter { video -> video.isToday() && video.bvid !in history }
                        .sortedBy { it.created }
                    if (new.isEmpty()) continue
                    logger.info { "明日方舟 视频 $type 捕捉到结果" }
                    for (video in new) {
                        try {
                            flow.emit(video)
                            history.add(video.bvid)
                        } catch (cause: Exception) {
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
        for (user in BlogUser.values()) {
            launch {
                for (blog in blogs.raw(user)) {
                    history.add(blog.id)
                }
                while (isActive) {
                    delay(10_000)
                    delay(cron(user).next())
                    // 加载
                    try {
                        blogs.load(key = user)
                    } catch (cause: Exception) {
                        logger.warning({ "明日方舟 微博 $user 数据加载失败" }, cause)
                        continue
                    }
                    // 推送
                    val raw = blogs.raw(user)
                        .filter { blog -> blog.isToday() && blog.id !in history }
                        .sortedBy { it.created }
                    if (raw.isEmpty()) continue
                    logger.info { "明日方舟 微博 $user 捕捉到结果" }
                    for (blog in raw) {
                        try {
                            flow.emit(blog)
                            history.add(blog.id)
                        } catch (cause: Exception) {
                            logger.warning({ "微博[${blog.id}]推送失败" }, cause)
                        }
                    }
                }
            }
        }
    }

    private fun announce() {
        val history: MutableSet<Int> = HashSet()
        val cron = ArknightsCronConfig.announce
        for (type in AnnounceType.values()) {
            launch {
                for (announcement in announcements.raw(type)) {
                    history.add(announcement.id)
                }
                while (isActive) {
                    delay(10_000)
                    delay(cron.next())
                    // 加载
                    try {
                        announcements.load(key = type)
                    } catch (cause: Exception) {
                        logger.warning({ "明日方舟 公告 $type 数据加载失败" }, cause)
                        continue
                    }
                    // 推送

                    val new = announcements.raw(type)
                        .filter { announcement -> announcement.isToday() && announcement.id !in history }
                        .sortedBy { it.id }
                    if (new.isEmpty()) continue
                    logger.info { "明日方舟 公告 $type 捕捉到结果" }
                    for (announcement in new) {
                        try {
                            flow.emit(announcement)
                            history.add(announcement.id)
                        } catch (cause: Exception) {
                            logger.warning({ "公告[${announcement.webUrl}]推送失败" }, cause)
                        }
                    }
                }
            }
        }
    }

    private fun penguin() {
        val cron: (PenguinDataType) -> Cron = { ArknightsCronConfig.penguin[it] ?: ArknightsCronConfig.default }
        for (type in PenguinDataType.values()) {
            launch {
                while (isActive) {
                    delay(10_000)
                    // 加载
                    try {
                        penguin.load(key = type)
                    } catch (cause: Exception) {
                        logger.warning({ "企鹅物流 数据 $type 数据加载失败" }, cause)
                        continue
                    }
                    delay(cron(type).next())
                }
            }
        }
    }

    private fun excel() {
        val cron = ArknightsCronConfig.excel
        val values = ExcelDataType.values().toList() - ExcelDataType.VERSION
        launch {
            while (isActive) {
                delay(10_000)

                val local = SemVersion(version = excel.version().versionControl)
                excel.load(ExcelDataType.VERSION)
                val current = SemVersion(version = excel.version().versionControl)
                if (local >= current) continue

                // 加载
                for (type in values) {
                    try {
                        excel.load(key = type)
                    } catch (cause: Exception) {
                        logger.warning({ "游戏数值 数据 $type 数据加载失败" }, cause)
                        continue
                    }
                }
                delay(cron.next())
            }
        }
    }

    public suspend fun clear() {
        videos.clear()
        blogs.clear()
        announcements.clear()
        penguin.clear()
        excel.clear()
    }

    private fun listen(contact: Contact) {
        contact.launch(coroutineContext) {
            ArknightsCollector(contact).emitAll(flow)
        }
    }

    @EventHandler
    public fun StartupEvent.handle() {
        System.setProperty(ExcelDataHolder.GAME_SOURCE_HOST_KEY, ArknightsConfig.host)
        video()
        weibo()
        announce()
        penguin()
        excel()
        plugin.logger.info { "蹲饼监听已开启" }
    }

    @EventHandler
    public fun BotOnlineEvent.online() {
        for (group in bot.groups) {
            listen(contact = group)
        }
        for (friend in bot.friends) {
            listen(contact = friend)
        }
    }

    @EventHandler
    public fun BotJoinGroupEvent.join() {
        launch {
            delay(60_000)
            listen(contact = group)
        }
    }

    @EventHandler
    public fun FriendAddEvent.add() {
        launch {
            delay(60_000)
            listen(contact = friend)
        }
    }
}