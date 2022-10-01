package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsGachaCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-gacha", "方舟抽卡",
    description = "明日方舟助手抽卡指令"
) {

    private val rules by ArknightsPoolConfig::rules

    private const val GACHA_USE_COIN = 600

    @SubCommand("one", "单抽")
    @Description("单抽times次")
    public suspend fun CommandSenderOnMessage<*>.one(times: Int = 1): Unit = reply {
        if (user.coin >= times * GACHA_USE_COIN) {
            user.coin -= times * GACHA_USE_COIN
            val data = ArknightsSubscriber.excel.character().values
                .pool(rules.getValue(subject.pool))
            val result = List(times) { gacha(data) }.groupByTo(java.util.TreeMap()) { it.rarity }
            buildMessageChain {
                appendLine("当前卡池[${subject.pool}] 合成玉剩余${user.coin}")

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
    public suspend fun CommandSenderOnMessage<*>.pool(name_: String, set: Boolean = false): Unit = reply {
        val name = name_.lines().first()
//        val lines = fromEvent.message.content.lines().filter {
//            it.matches(BUILD_POOL_LINE) || it.startsWith('#')
//        }.also {
//            gacha(pool = Obtain.pool(it))
//        }
//        PoolRules[name] = lines.joinToString(";").trim()
//        if (set) pool = name
//        "卡池[${name}] -> $lines 已写入".toPlainText()
        emptyMessageChain()
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