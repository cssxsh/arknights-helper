package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import kotlin.time.*

object ArknightsPlayerCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "player", "玩家",
    description = "明日方舟助手玩家指令"
) {

    @SubCommand("level", "等级")
    @Description("设置玩家等级")
    suspend fun CommandSenderOnMessage<*>.level(index: Int = 0) = sendMessage {
        check(index in PlayerLevelRange) { "等级不合法，${PlayerLevelRange}" }
        level = index
        "等级${index}已设置".toPlainText()
    }

    @SubCommand("reason", "理智")
    @Description("设置理智值并定时提醒")
    suspend fun CommandSenderOnMessage<*>.reason(init: Int) = sendMessage {
        check(init in 0 until max) { "理智不合法，level.${level}[${(0 until max)}]" }
        val duration = RegenSpeed * (max - init)
        reason = System.currentTimeMillis() + duration.toLongMilliseconds()
        "${init}->${max}理智提醒(level.${level})设置完毕, 预计倒计时${duration}".toPlainText()
    }

    @SubCommand("recruit", "公招")
    suspend fun CommandSenderOnMessage<*>.recruit(site: Int, hours: Int = 9, minutes: Int = 0) = sendMessage {
        val duration = hours.hours + minutes.minutes
        check(duration in RECRUIT_TIME) { "公招时间${duration}不合法" }
        val time = System.currentTimeMillis() + duration.toLongMilliseconds()
        recruit = recruit.toMutableMap().apply {
            put(site, time)
        }
        "公招位置[${site}]设置 ${hours}小时${minutes}分 完成".toPlainText()
    }
}