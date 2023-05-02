package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsGachaCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-gacha", "方舟抽卡", "方舟招募",
    description = "明日方舟助手抽卡指令"
) {

    private val rules by ArknightsPoolConfig::rules

    private const val GACHA_USE_COIN = 600

    @SubCommand("one", "单抽")
    @Description("单抽times次")
    public suspend fun CommandSenderOnMessage<*>.one(times: Int = 1): Unit = reply {
        if (user.coin >= times * GACHA_USE_COIN) {
            user.coin -= times * GACHA_USE_COIN
            val pool = ArknightsSubscriber.excel.character().values
                .pool(rules.getValue(subject.pool))
            val result = List(times) { gacha(pool = pool) }.groupByTo(java.util.TreeMap()) { it.rarity }
            buildMessageChain {
                appendLine("当前卡池[${subject.pool}] 合成玉剩余${user.coin}")
                for ((rarity, character) in result) {
                    val sum = character.groupByTo(java.util.TreeMap()) { it.name }.map { (name, list) ->
                        "${name}${if (list.size == 1) "" else "*${list.size}"}"
                    }
                    appendLine("${rarity.ordinal + 1}星干员(${character.size}): $sum")
                }
            }
        } else {
            "合成玉不足,当前拥有${user.coin},请尝试答题获得".toPlainText()
        }
    }

    @SubCommand("ten", "十连")
    @Description("十连times次")
    public suspend fun CommandSenderOnMessage<*>.ten(times: Int = 1): Unit = one(times * 10)

    @SubCommand("pool", "卡池")
    @Description("设置新卡池")
    public suspend fun CommandSenderOnMessage<*>.pool(name: String, set: Boolean = false): Unit = reply {
        val name0 = name.lines().first()
        val lines = fromEvent.message.content.lineSequence()
            .filter { it.matches(BUILD_POOL_LINE) || it.startsWith('#') }
            .toList()
        // Test
        val pool = ArknightsSubscriber.excel.character().values
            .pool(lines)
        gacha(pool = pool)
        rules[name0] = lines.joinToString(";").trim()
        if (set) subject.pool = name0
        "卡池[${name0}] -> $lines 已写入".toPlainText()
    }

    @SubCommand("detail", "详情")
    @Description("查看卡池规则")
    public suspend fun CommandSender.detail() {
        val message = buildMessageChain {
            for ((name, rule) in rules) {
                appendLine("===> [${name}]")
                appendLine(rule.replace(';', '\n'))
            }
        }

        sendMessage(message = message)
    }

    @SubCommand("set", "设置")
    @Description("设置卡池")
    public suspend fun UserCommandSender.set(name: String) {
        val message = if (name in rules) {
            subject.pool = name
            "卡池[${subject.pool}]设置完毕".toPlainText()
        } else {
            "卡池不存在, ${rules.keys}".toPlainText()
        }

        sendMessage(message = message)
    }
}