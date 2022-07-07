package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsStageCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "stage", "关卡",
    description = "明日方舟助手关卡指令"
) {

    @Handler
    public suspend fun CommandSenderOnMessage<*>.handler(code: String, limit: Int = 3, now: Boolean = true): Unit = reply {
        stage(code, limit, now)
    }
}