package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.plugin.data.ArknightsUserData
import xyz.cssxsh.mirai.plugin.data.delegate

internal val logger get() = ArknightsHelperPlugin.logger

internal val data get() = ArknightsHelperPlugin.dataFolder

suspend fun CommandSenderOnMessage<*>.sendMessage(block: suspend (Contact) -> Message): Boolean {
    return runCatching {
        block(fromEvent.subject)
    }.onSuccess {
        sendMessage(fromEvent.message.quote() + it)
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}

suspend fun CommandSenderOnMessage<*>.sendString(block: suspend (Contact) -> String): Boolean {
    return runCatching {
        block(fromEvent.subject)
    }.onSuccess {
        sendMessage(fromEvent.message.quote() + it)
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}

var CommandSenderOnMessage<*>.coin: Int by ArknightsUserData.delegate()

var CommandSenderOnMessage<*>.reason: Int by ArknightsUserData.delegate()

internal fun Array<out String>.check(tags: Set<String> = gacha.tags()) = map { word ->
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
}

internal fun recruit(vararg words: String) = character.recruit(words.check().toSet(), gacha.recruit())

@JvmName("buildRecruitMessage")
fun RecruitResult.getContent() = buildMessageChain {
    toSortedMap().forEach { (rarity, character) ->
        val sum = character.groupBy { it.name }.mapValues { it.value.size }.entries.sortedBy { it.value }.map {
            "${it.key}${if (it.value == 1) "" else "*${it.value}"}"
        }
        appendLine("${rarity + 1}星干员(${character.size}): $sum")
    }
}

@JvmName("buildRecruitMapMessage")
fun RecruitMap.getContent() = buildMessageChain {
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

val obtain = character.values.rarities(2..5).obtain("招募寻访")

val normal: PoolData = listOf(
    obtain.rarities(5) to 0.02,
    obtain.rarities(4) to 0.08,
    obtain.rarities(3) to 0.48,
    obtain.rarities(2) to 0.42
)

val bug: PoolData = listOf(character.values.toSet() to 1.00)

