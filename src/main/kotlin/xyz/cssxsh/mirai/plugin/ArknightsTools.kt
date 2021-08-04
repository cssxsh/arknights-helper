package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import java.time.Duration
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal val logger by ArknightsHelperPlugin::logger

internal suspend fun <T : CommandSenderOnMessage<*>> T.nextContent(): String {
    return fromEvent.nextMessage { it.message.content.isNotBlank() }.content
}

const val SendDelay = 60 * 1000L

suspend fun <T : CommandSenderOnMessage<*>> T.sendMessage(block: suspend T.(Contact) -> Message): Boolean {
    return runCatching {
        block(fromEvent.subject)
    }.onSuccess { message ->
        quoteReply(message)
    }.onFailure { throwable ->
        logger.warning { "发送消息失败, $throwable" }
        when {
            "本群每分钟只能发" in throwable.message.orEmpty() -> {
                kotlinx.coroutines.delay(SendDelay)
                sendMessage { throwable.message.orEmpty().toPlainText() }
            }
            else -> {
                quoteReply("发送消息失败， ${throwable.message}")
            }
        }
    }.isSuccess
}

suspend fun CommandSenderOnMessage<*>.quoteReply(message: Message) = sendMessage(fromEvent.message.quote() + message)

suspend fun CommandSenderOnMessage<*>.quoteReply(message: String) = quoteReply(message.toPlainText())

class SubjectDelegate<T>(private val default: (Contact) -> T) : ReadWriteProperty<CommandSenderOnMessage<*>, T> {
    private val map: MutableMap<Contact, T> = mutableMapOf()

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        map[thisRef.fromEvent.subject] = value
    }

    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return map.getOrPut(thisRef.fromEvent.subject) { default(thisRef.fromEvent.subject) }
    }
}

/**
 * 通过正负号区分群和用户
 */
val Contact.delegate get() = if (this is Group) id * -1 else id

/**
 * 查找Contact
 */
fun findContact(delegate: Long): Contact? {
    Bot.instances.forEach { bot ->
        if (delegate < 0) {
            bot.getGroup(delegate * -1)?.let { return@findContact it }
        } else {
            bot.getFriend(delegate)?.let { return@findContact it }
            bot.getStranger(delegate)?.let { return@findContact it }
            bot.groups.forEach { group ->
                group.getMember(delegate)?.let { return@findContact it }
            }
        }
    }
    return null
}

/**
 * 检查并转换TAG
 */
internal fun Collection<String>.tag(tags: Set<String> = ExcelData.gacha.tags()): Set<String> {
    val temp = tags.map { it.substring(0..1) }
    return map { it.trim() }.filter { it.isNotBlank() }.map { word ->
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

internal fun recruit(words: List<String>) = ExcelData.characters.recruit(words.tag(), ExcelData.gacha.recruit())

internal fun String.role(roles: Set<String> = ExcelData.gacha.recruit()) = trim().let { name ->
    when (name) {
        in roles -> name
        in RoleAlias -> RoleAlias.getValue(name)
        else -> throw IllegalArgumentException("未知干员: $name")
    }
}

@JvmName("buildRecruitMessage")
internal fun RecruitResult.toMessage() = buildMessageChain {
    this@toMessage.forEach { (rarity, character) ->
        val sum = character.groupBy { it.name }.mapValues { it.value.size }.entries.sortedBy { it.value }.map {
            "${it.key}${if (it.value == 1) "" else "*${it.value}"}"
        }
        appendLine("${rarity + 1}星干员(${character.size}): $sum")
    }
}

@JvmName("buildRecruitMapMessage")
internal fun RecruitMap.toMessage() = buildMessageChain {
    this@toMessage.forEach { (tags, result) ->
        append("====> $tags ")
        if ((result.keys - 0).all { it >= 3 }) {
            appendLine("${(result.keys - 0).minOrNull()!! + 1}星保底")
        } else {
            appendLine("")
        }
        append(result.toMessage())
    }
}

@JvmName("buildMarketFaceMapMessage")
internal fun ArknightsFaceMap.toMessage() = buildMessageChain {
    appendLine("共${this@toMessage.size}个表情")
    this@toMessage.forEach { (name, list) ->
        appendLine("$name ${list.first().detail}")
    }
}

@JvmName("buildArknightsFaceMapMessage")
internal fun ArknightsFace.toMessage() = buildMessageChain {
    appendLine("表情名: $title $content")
    appendLine("详情: $detail")
    appendLine("原图: $image")
    appendLine("Hash: $key")
}

private fun duration(millis: Long) = Duration.ofMillis(millis).run { "${toMinutesPart()}m${toSecondsPart()}s" }

private fun Pair<Matrix, Stage>.toMessage() = buildMessageChain {
    appendLine("概率: ${first.quantity}/${first.times}=${first.probability.percentage()}")
    appendLine("单件期望理智: ${single.intercept()}")
    appendLine("最短通关用时: ${duration(stage.minClearTime)}")
    appendLine("单件期望用时: ${duration(short)}")
}

internal fun item(name: String, limit: Int, now: Boolean) = buildMessageChain {
    val (item, list) = (PenguinData.items to PenguinData.matrices.let { if (now) it.now() else it }).item(name)
    appendLine("${item.alias.get()} 统计结果 By 企鹅物流数据统计")
    if (list.isEmpty()) {
        appendLine("列表为空，请尝试更新数据")
        return@buildMessageChain
    }
    var count = 0
    (list with PenguinData.stages).sortedBy { it.single }.forEach { pair ->
        if (pair.stage.isGacha || count >= limit) return@forEach
        appendLine("=======> 作战: ${pair.stage.code} (cost=${pair.stage.cost}) ${pair.stage}")
        append(pair.toMessage())
        count++
    }
}

internal fun alias() = buildMessageChain {
    appendLine("企鹅物流材料别名")
    PenguinData.items.forEach { item ->
        appendLine("名称: ${item.i18n.get()}，别名: ${item.alias.get()}")
    }
}

internal fun stage(code: String, limit: Int, now: Boolean) = buildMessageChain {
    val (stage, list) = (PenguinData.stages to PenguinData.matrices.let { if (now) it.now() else it }).stage(code)
    appendLine("[${stage.code}] (cost=${stage.cost}) 统计结果 By 企鹅物流数据统计")
    if (list.isEmpty()) {
        appendLine("列表为空，请尝试更新数据")
        return@buildMessageChain
    }
    var count = 0
    (list with PenguinData.items).sortedByDescending { it.rarity }.forEach { (matrix, item) ->
        if (item.type != ItemType.MATERIAL || count >= limit) return@forEach
        appendLine("=======> 掉落: ${item.name} (rarity=${item.rarity}) ")
        append((matrix to stage).toMessage())
        count++
    }
}

internal fun zone(name: String, limit: Int, now: Boolean) = buildMessageChain {
    val (_, list) = (PenguinData.zones to PenguinData.stages).name(name)
    list.forEach { stage ->
        append(stage(stage.code, limit, now))
    }
}

internal fun QuestionType.random() = random(QuestionDataLoader)

internal fun countQuestionType(type: QuestionType, mode: Int) {
    MineCount.compute(type) { _, s ->
        (s ?: mutableListOf(0, 0, 0)).apply { this[mode] += 1 }
    }
}

internal fun tableMineCount() = buildString {
    appendLine("# 答题统计")
    appendLine("| 类型 | 正确 | 错误 | 超时 | 总计 |")
    appendLine("|:----:|:----:|:----:|:----:|:----:|")
    MineCount.forEach { type, (f, s, t) ->
        appendLine("| $type | $f | $s | $t | ${f + s + t} |")
    }
}

internal fun Question.toMessage() = buildMessageChain {
    appendLine("[${type}]<${coin}>：${problem} (${timeout / 1000}s内作答)")
    options.forEach { (index, text) ->
        appendLine("${index}.${text}")
    }
}