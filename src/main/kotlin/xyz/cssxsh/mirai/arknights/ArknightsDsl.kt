package xyz.cssxsh.mirai.arknights

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import java.time.*
import kotlin.properties.*
import kotlin.reflect.*

internal val logger by lazy {
    try {
        ArknightsHelperPlugin.logger
    } catch (_: ExceptionInInitializerError) {
        MiraiLogger.Factory.create(ArknightsSubscriber::class)
    }
}

internal suspend fun <T : CommandSenderOnMessage<*>> T.nextContent(): String {
    return fromEvent.nextMessage { it.message.content.isNotBlank() }.content
}

internal suspend fun CommandSenderOnMessage<*>.reply(block: suspend UserCommandSender.() -> Message) {
    try {
        sendMessage(message = fromEvent.message.quote() + block.invoke(this as UserCommandSender))
    } catch (cause: Exception) {
        logger.warning({ "发送消息失败" }, cause)
        sendMessage(message = "发送消息失败， ${cause.message}")
    }
}

internal class SubjectDelegate<T>(private val default: (Contact) -> T) :
    ReadWriteProperty<CommandSenderOnMessage<*>, T> {

    private val map: MutableMap<Contact, T> = HashMap()

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        map[thisRef.fromEvent.subject] = value
    }

    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return map.getOrPut(thisRef.fromEvent.subject) { default(thisRef.fromEvent.subject) }
    }
}

/**
 * 公招
 * @param words 公招词汇
 */
public suspend fun recruit(vararg words: String): RecruitMap {
    val character = ArknightsSubscriber.excel.character()
    val gacha = ArknightsSubscriber.excel.gacha()
    val target = HashSet<String>()
    val pool = HashSet<String>()

    loop@ for (word in words) {
        for (tag in gacha.tags) {
            if (tag.name.startsWith(word)) {
                target.add(tag.name)
                continue@loop
            }
        }

        val transform = when (word) {
            "高资" -> "高级资深干员"
            "支援机械", "机械" -> "支援机械"
            "控制" -> "控场"
            "回费" -> "费用回复"
            else -> throw IllegalArgumentException("未知TAG: $word")
        }
        target.add(transform)
    }

    for (line in gacha.recruitDetail.lineSequence()) {
        if (line.startsWith("★")) {
            for (item in line.substringAfterLast("""\n""").splitToSequence("/")) {
                pool.add(item.trim())
            }
        }
    }

    return character.recruit(words = target, pool = pool)
}

@JvmName("buildRecruitMessage")
internal fun RecruitResult.toMessage() = buildMessageChain {
    for ((rarity, character) in this@toMessage) {
        val sum = character.groupBy { it.name }.mapValues { it.value.size }.entries.sortedBy { it.value }.map {
            "${it.key}${if (it.value == 1) "" else "*${it.value}"}"
        }
        appendLine("${rarity + 1}星干员(${character.size}): $sum")
    }
}

@JvmName("buildRecruitMapMessage")
internal fun RecruitMap.toMessage() = buildMessageChain {
    for ((tags, result) in this@toMessage) {
        val info = when {
            result.keys.all { it >= 3 } -> "${result.keys.minOrNull()!! + 1}星保底"
            result.keys.all { it == 0 } -> "小车保底"
            else -> ""
        }
        appendLine("====> $tags $info")
        append(result.toMessage())
    }
}

private fun duration(millis: Long) = with(Duration.ofMillis(millis)) { "${toMinutesPart()}m${toSecondsPart()}s" }

internal fun MessageChainBuilder.append(matrix: Matrix, stage: Stage) {
    val single = (matrix to stage).single
    val short = (matrix to stage).short
    appendLine("概率: ${matrix.quantity}/${matrix.times}=${matrix.probability.percentage()}")
    appendLine("单件期望理智: ${single.intercept()}")
    appendLine("最短通关用时: ${duration(stage.minClearTime)}")
    appendLine("单件期望用时: ${duration(short)}")
}