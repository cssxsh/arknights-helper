package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.arknights.*

object ArknightsRecruitCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "recruit", "公招",
    description = "明日方舟助手公招指令"
), ArknightsHelperCommand {

    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(vararg words: String) = sendMessage {
        recruit(words = words.asList()).toMessage()
    }
}