package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.user.*
import xyz.cssxsh.mirai.arknights.*
import java.time.*

public object ArknightsPlayerCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "player", "玩家",
    description = "明日方舟助手玩家指令"
) {

    @SubCommand("detail", "详情")
    @Description("查看玩家详情")
    public suspend fun CommandSenderOnMessage<*>.detail(): Unit = reply {
        buildMessageChain {
            appendLine("合成玉：${coin}")
            appendLine("等级：${level}")
            if (reason > System.currentTimeMillis()) {
                val time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(reason), ZoneId.systemDefault())
                appendLine("预计理智回复时间：${time}")
            } else {
                appendLine("当前未设置理智提醒")
            }
            for ((site, timestamp) in recruit) {
                if (timestamp > System.currentTimeMillis()) {
                    val time = OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                    appendLine("公招位置[${site}]设置, 预计提醒时间：${time}")
                }
            }
            if (recruit.isEmpty()) {
                appendLine("当前未设置公招提醒")
            }
        }
    }

    @SubCommand("level", "等级")
    @Description("设置玩家等级")
    public suspend fun CommandSenderOnMessage<*>.level(index: Int = 0): Unit = reply {
        check(index in PlayerLevelRange) { "等级不合法，${PlayerLevelRange}" }
        level = index
        "等级${index}已设置".toPlainText()
    }

    @SubCommand("reason", "理智")
    @Description("设置理智值并定时提醒")
    public suspend fun CommandSenderOnMessage<*>.reason(init: Int): Unit = reply {
        check(init in 0 until max) { "理智不合法，level.${level}[${(0 until max)}]" }
        val duration = RegenSpeed * (max - init)
        reason = System.currentTimeMillis() + duration
        "${init}->${max}理智提醒(level.${level})设置完毕, 预计倒计时${duration}".toPlainText()
    }

    @SubCommand("recruit", "公招")
    @Description("设置公招位时间并定时提醒")
    public suspend fun CommandSenderOnMessage<*>.recruit(site: Int, hours: Int = 9, minutes: Int = 0): Unit = reply {
        val duration = (hours * 60 + minutes) * 60 * 1000
        check(duration in RecruitTime) { "公招时间${duration}不合法" }
        val time = System.currentTimeMillis() + duration
        recruit = recruit + (site to time)
        "公招位置[${site}]设置 ${hours}小时${minutes}分 完成".toPlainText()
    }

    @SubCommand("record", "记录")
    @Description("公招结果记录")
    public suspend fun CommandSenderOnMessage<*>.record(page: Int = 1): Unit = reply {
        result.table(page).toPlainText()
    }
}