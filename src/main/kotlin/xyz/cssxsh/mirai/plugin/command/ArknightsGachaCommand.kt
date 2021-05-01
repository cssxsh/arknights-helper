package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.plugin.*

object ArknightsGachaCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "gacha", "抽卡",
    description = "明日方舟助手抽卡指令"
) {

    private var pool = normal

    @SubCommand
    @Description("单抽")
    suspend fun CommandSenderOnMessage<*>.one(times: Int = 1) = sendMessage {
        check(coin >= times * POOL_USE_COIN) { "合成玉不足" }
        coin -= times * POOL_USE_COIN
        val result: RecruitResult = (1..times).map { gacha(pool = pool) }.groupBy { it.rarity }
        result.getContent()
    }

    @SubCommand
    @Description("十连")
    suspend fun CommandSenderOnMessage<*>.ten(times: Int = 1) = one(times * 10)

    @SubCommand
    @Description("设置卡池")
    suspend fun CommandSenderOnMessage<*>.set(name: String) = sendMessage {
        pool = when(name) {
            "bug" -> bug
            else -> normal
        }
        "${name}设置完毕".toPlainText()
    }
}