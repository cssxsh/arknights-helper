package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*

object ArknightsGuardCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "guard", "蹲饼",
    description = "明日方舟助手蹲饼指令"
) {
    @SubCommand("detail", "详情")
    @Description("查看蹲饼详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        "当前蹲饼状态${fromEvent.subject.delegate in GuardContacts}, 蹲饼间隔${GuardInterval}m".toPlainText()
    }

    @SubCommand("speed", "速度")
    @Description("设置微博蹲饼速度")
    suspend fun CommandSenderOnMessage<*>.speed(duration: Int) = sendMessage {
        check(duration in 1..10) { "速度 不合法 1~10 分钟" }
        GuardInterval = duration
        "蹲饼速度已设置 ${duration}分钟".toPlainText()
    }

    @SubCommand("open", "打开")
    @Description("开启提醒")
    suspend fun CommandSenderOnMessage<*>.open() = sendMessage {
        GuardContacts.add(fromEvent.subject.delegate)
        "蹲饼已打开".toPlainText()
    }

    @SubCommand("close", "关闭")
    @Description("关闭提醒")
    suspend fun CommandSenderOnMessage<*>.close() = sendMessage {
        GuardContacts.remove(fromEvent.subject.delegate)
        "蹲饼已关闭".toPlainText()
    }
}