package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsGachaCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "gacha", "抽卡",
    description = "明日方舟助手抽卡指令"
) {

    @SubCommand("one", "单抽")
    @Description("单抽times次")
    public suspend fun CommandSenderOnMessage<*>.one(times: Int = 1): Unit = reply {
        if (coin >= times * PoolUseCoin) {
            coin -= times * PoolUseCoin
            val data = Obtain.pool(rule)
            val result = List(times) { gacha(data) }.toRecruitResult()
            "当前卡池[${pool}] 合成玉剩余${coin}\n".toPlainText() + result.toMessage()
        } else {
            "合成玉不足,当前拥有${coin},请尝试答题获得".toPlainText()
        }
    }

    @SubCommand("ten", "十连")
    @Description("十连times次")
    public suspend fun CommandSenderOnMessage<*>.ten(times: Int = 1): Unit = one(times * 10)

    @SubCommand("pool", "卡池")
    @Description("设置新卡池")
    public suspend fun CommandSenderOnMessage<*>.pool(name_: String, set: Boolean = false): Unit = reply {
        val name = name_.lines().first()
        val lines = fromEvent.message.content.lines().filter {
            it.matches(BUILD_POOL_LINE) || it.startsWith('#')
        }.also {
            gacha(pool = Obtain.pool(it))
        }
        PoolRules[name] = lines.joinToString(";").trim()
        if (set) pool = name
        "卡池[${name}] -> $lines 已写入".toPlainText()
    }

    @SubCommand("detail", "详情")
    @Description("查看卡池规则")
    public suspend fun CommandSenderOnMessage<*>.detail(): Unit = reply {
        PoolRules.entries.joinToString("\n") { (name, rule) ->
            "===> [${name}]\n" + rule.split(';').joinToString("\n")
        }.toPlainText()
    }

    @SubCommand("set", "设置")
    @Description("设置卡池")
    public suspend fun CommandSenderOnMessage<*>.set(name: String = ""): Unit = reply {
        check(name in PoolRules) { "卡池不存在, ${PoolRules.keys}" }
        pool = name
        "卡池[${pool}]设置完毕".toPlainText()
    }
}