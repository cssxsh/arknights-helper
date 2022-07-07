package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsRecruitCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "recruit", "公招",
    description = "明日方舟助手公招指令"
) {

    @Handler
    public suspend fun CommandSenderOnMessage<*>.handler(vararg words: String): Unit = reply {
        recruit(words = words.asList()).toMessage()
    }
}