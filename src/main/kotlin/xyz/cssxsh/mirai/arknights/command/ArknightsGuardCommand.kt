package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsGuardCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "guard", "蹲饼",
    description = "明日方舟助手蹲饼指令"
) {

    @SubCommand("detail", "详情")
    @Description("查看蹲饼详情")
    public suspend fun CommandSenderOnMessage<*>.detail(): Unit = reply {
        buildMessageChain {
            val list = GuardContacts.groupBy { it > 0 }
            for (friend in list[true].orEmpty()) {
                appendLine("friend: $friend")
            }
            for (group in list[false].orEmpty()) {
                appendLine("group ${-group}")
            }
            appendLine("蹲饼间隔${GuardInterval}m")
        }
    }

    @SubCommand("speed", "速度")
    @Description("设置微博蹲饼速度")
    public suspend fun CommandSenderOnMessage<*>.speed(duration: Int): Unit = reply {
        check(duration in 1..10) { "速度 不合法 1~10 分钟" }
        GuardInterval = duration
        "蹲饼速度已设置 ${duration}分钟".toPlainText()
    }

    @SubCommand("open", "打开")
    @Description("开启提醒")
    public suspend fun CommandSenderOnMessage<*>.open(contact: Contact = fromEvent.subject): Unit = reply {
        GuardContacts.add(contact.delegate)
        "$contact 蹲饼已打开".toPlainText()
    }

    @SubCommand("close", "关闭")
    @Description("关闭提醒")
    public suspend fun CommandSenderOnMessage<*>.close(contact: Contact = fromEvent.subject): Unit = reply {
        GuardContacts.remove(contact.delegate)
        "$contact 蹲饼已关闭".toPlainText()
    }
}