package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
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

private fun Double.intercept(decimal: Int = 2) = "%.${decimal}f".format(this)

private fun Duration.text() = toComponents { m, s, _ -> (if (m > 0) "${m}m" else "") + (if (s > 0) "${s}s" else "") }

private fun Pair<Matrix, Stage>.toMessage() = buildMessageChain {
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
        append(pair.toMessage())
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
        append((matrix to stage).toMessage())
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

internal fun Question.toMessage() = buildMessageChain {
    appendLine("[${type}]<${coin}>：${problem} (${timeout.milliseconds}内作答)")
    options.forEach { (index, text) ->
        appendLine("${index}.${text}")
    }
}