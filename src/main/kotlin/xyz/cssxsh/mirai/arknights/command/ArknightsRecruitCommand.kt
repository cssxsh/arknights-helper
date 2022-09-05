package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsRecruitCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-recruit", "方舟公招",
    description = "明日方舟助手公招指令"
) {

    @Handler
    public suspend fun CommandSender.handler(vararg words: String) {
        val message = recruit(words = words).toMessage()

        sendMessage(message = message)
    }
}