package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.useHttpClient
import xyz.cssxsh.arknights.weibo.*
import java.time.LocalTime
import java.time.OffsetDateTime
import kotlin.math.abs
import kotlin.time.*

internal val logger get() = ArknightsHelperPlugin.logger

internal suspend fun <T : CommandSenderOnMessage<*>> T.sendMessage(block: suspend T.(Contact) -> Message): Boolean {
    return runCatching {
        block(fromEvent.subject)
    }.onSuccess {
        sendMessage(fromEvent.message.quote() + it)
    }.onFailure {
        logger.warning({ "对${fromEvent.subject}构建消息失败" }, it)
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}

private fun Array<out String>.check(tags: Set<String> = ExcelData.gacha.tags()): Set<String> {
    return map { word ->
        val temp = tags.map { it.substring(0..1) }
        when (word) {
            in tags -> word
            "高资" -> "高级资深干员"
            "支援机械", "机械" -> "支援机械"
            "控制" -> "控场"
            "回费" -> "费用回复"
            in temp -> tags.elementAt(temp.indexOf(word))
            else -> throw IllegalArgumentException("未知TAG: $word")
        }
    }.toSet()
}

internal fun recruit(vararg words: String) = ExcelData.characters.recruit(words.check(), ExcelData.gacha.recruit())

@JvmName("buildRecruitMessage")
internal fun RecruitResult.getContent() = buildMessageChain {
    this@getContent.forEach { (rarity, character) ->
        val sum = character.groupBy { it.name }.mapValues { it.value.size }.entries.sortedBy { it.value }.map {
            "${it.key}${if (it.value == 1) "" else "*${it.value}"}"
        }
        appendLine("${rarity + 1}星干员(${character.size}): $sum")
    }
}

@JvmName("buildRecruitMapMessage")
internal fun RecruitMap.getContent() = buildMessageChain {
    this@getContent.forEach { (tags, result) ->
        append("====> $tags ")
        if ((result.keys - 0).all { it >= 3 }) {
            appendLine("${(result.keys - 0).minOrNull()!! + 1}星保底")
        } else {
            appendLine("")
        }
        append(result.getContent())
    }
}

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

private fun Double.intercept(decimal: Int = 2) = "%.${decimal}f".format(this)

private fun Duration.text() = toComponents { m, s, _ -> (if (m > 0) "${m}m" else "") + (if (s > 0) "${s}s" else "") }

internal operator fun LocalTime.minus(other: LocalTime): Int = toSecondOfDay() - other.toSecondOfDay()

private fun Pair<Matrix, Stage>.getContent() = buildMessageChain {
    appendLine("概率: ${first.quantity}/${first.times}=${first.probability.intercept()}")
    appendLine("单件期望理智: ${single.intercept()}")
    appendLine("最短通关用时: ${stage.clear.text()}")
    appendLine("单件期望用时: ${short.text()}")
}

internal fun item(name: String, limit: Int) = buildMessageChain {
    val (item, list) = (PenguinData.items to PenguinData.matrices.now()).item(name)
    appendLine("${item.alias.get()} 统计结果 By 企鹅物流数据统计")
    if (list.isEmpty()) {
        appendLine("列表为空，请尝试更新数据")
        return@buildMessageChain
    }
    var count = 0
    (list with PenguinData.stages).sortedBy { it.single }.forEach { pair ->
        if (pair.stage.isGacha || count >= limit) return@forEach
        appendLine("=======> 作战: ${pair.stage.code} (cost=${pair.stage.cost}) ")
        append(pair.getContent())
        count++
    }
}

internal fun alias() = buildMessageChain {
    PenguinData.items.forEach { item ->
        appendLine("名称: ${item.i18n.get()}，别名: ${item.alias.get()}")
    }
}

internal fun stage(code: String, limit: Int) = buildMessageChain {
    val (stage, list) = (PenguinData.stages to PenguinData.matrices.now()).stage(code)
    appendLine("[${stage.code}] (cost=${stage.cost}) 统计结果 By 企鹅物流数据统计")
    if (list.isEmpty()) {
        appendLine("列表为空，请尝试更新数据")
        return@buildMessageChain
    }
    var count = 0
    (list with PenguinData.items).sortedByDescending { it.rarity }.forEach { (matrix, item) ->
        if (item.type != ItemType.MATERIAL || count >= limit) return@forEach
        appendLine("=======> 掉落: ${item.name} (rarity=${item.rarity}) ")
        append((matrix to stage).getContent())
        count++
    }
}

internal fun zone(name: String, limit: Int) = buildMessageChain {
    val (_, list) = (PenguinData.zones to PenguinData.stages).name(name)
    list.forEach { stage ->
        append(stage(stage.code, limit))
    }
}

internal fun QuestionType.build() = build(QuestionDataLoader)

internal fun Question.getContent() = buildMessageChain {
    appendLine("[${type}]<${coin}>：${problem} (${timeout.milliseconds}内作答)")
    options.forEach { (index, text) ->
        appendLine("${index}.${text}")
    }
}

private suspend fun sendToTaskContacts(block: suspend MessageChainBuilder.(Contact) -> Unit) {
    Bot.instances.flatMap { bot ->
        bot.friends.filter { it.id in tasks } + bot.groups.filter { it.id in tasks }
    }.forEach { contact ->
        runCatching {
            contact.sendMessage(buildMessageChain { block(contact) })
        }.onFailure {
            logger.warning({ "向${contact}发送消息失败" }, it)
        }
    }
}

private val Url.filename get() = encodedPath.substringAfterLast('/')

private suspend fun sendVideo(video: Video) = sendToTaskContacts { contact ->
    appendLine("鹰角有新视频了！")
    appendLine("链接: ${video.url}")
    appendLine("标题: ${video.title}")
    appendLine("简介：${video.description}")

    runCatching {
        val image = BilibiliData.resolve(video.created.toLocalDate().toString()).resolve(video.url.filename).apply {
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
            val file = WeiboData.resolve(blog.createdAt.toLocalDate().toString()).resolve(url.filename).apply {
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

internal fun download(): Unit = runBlocking {
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

internal fun CoroutineScope.subscribe(fast: Duration = (1).minutes, slow: Duration = (10).minutes) = launch {
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
                GuardInterval = (5).minutes
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