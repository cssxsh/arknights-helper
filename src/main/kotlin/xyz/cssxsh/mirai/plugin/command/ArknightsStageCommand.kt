package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import xyz.cssxsh.mirai.plugin.*

object ArknightsStageCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "stage", "关卡",
    description = "明日方舟助手关卡指令"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(code: String, limit: Int = 3, now: Boolean = true) = sendMessage {
        stage(code, limit, now)
    }
}