package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.plugin.*

object ArknightsRecruitCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "recruit", "公招",
    description = "明日方舟助手公招指令"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(vararg words: String) = sendMessage {
        recruit(words = words.asList()).toMessage()
    }
}