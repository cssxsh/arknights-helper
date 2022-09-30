package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsRecruitCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-recruit", "方舟公招",
    description = "明日方舟助手公招指令"
) {

    private var last = 0

    private val pool = HashSet<Character>()

    private val special = HashSet<String>()

    @Handler
    public suspend fun CommandSender.handler(vararg words: String) {
        val table = ArknightsSubscriber.excel.character()
        val gacha = ArknightsSubscriber.excel.gacha()
        // 检查公招池更新
        if (gacha.recruitDetail.hashCode() != last || pool.isEmpty()) {
            special.clear()
            pool.clear()
            val regex = """<@rc\.eml>([^<]+)</>""".toRegex()
            for (line in gacha.recruitDetail.lineSequence()) {
                if (!line.startsWith("★")) continue
                for (item in line.substringAfterLast("""\n""").splitToSequence(" / ")) {
                    val match = regex.matchEntire(item)
                    val name = if (match != null) {
                        special.add(match.groupValues[1])
                        match.groupValues[1]
                    } else {
                        item
                    }
                    for ((_, character) in table) {
                        if (character.name != name) continue
                        pool.add(character)
                        break
                    }
                }
            }
            last = gacha.recruitDetail.hashCode()
        }


        // 处理词条
        val target = HashSet<String>()
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
                else -> throw CommandArgumentParserException("未知TAG: $word")
            }
            target.add(transform)
        }
        if (target.size !in 1..5) throw CommandArgumentParserException("词条数量不对")

        val obtain = target.flatMapTo(HashSet()) { word -> pool.filter(word) }
        val site = minOf(3, target.size)
        val targets = (0..site).fold(setOf<Set<String>>(emptySet())) { used, _ ->
            target.flatMapTo(HashSet()) { a -> used.map { b -> b + a } }
        }
        val map = targets.sortedBy { it.size }.associateWith {
            it.fold(obtain as Set<Character>) { s, word -> s.filter(word) }
        }.mapValues { (_, characters) ->
            characters.groupBy { it.rarity }
        }.filter { (_, characters) ->
            characters.isNotEmpty()
        }

        val message = buildMessageChain {
            for ((tags, result) in map) {
                val info = when {
                    result.keys.all { it >= 3 } -> "${result.keys.minOrNull()!! + 1}星保底"
                    result.keys.any { it == 2 } -> continue
                    result.keys.all { it == 0 } -> "小车保底"
                    else -> ""
                }
                appendLine("====> $tags $info")
                for ((rarity, characters) in result) {
                    appendLine("${rarity + 1}星干员: ${characters.map { it.name }}")
                }
            }
        }

        sendMessage(message = message)
    }
}