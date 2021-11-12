package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.plugin.*

object ArknightsItemCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "item", "材料",
    description = "明日方舟助手材料指令"
), ArknightsHelperCommand {

    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(name: String, limit: Int = 5, now: Boolean = true) = sendMessage {
        runCatching {
            item(name, limit, now)
        }.recoverCatching {
            sendMessage("查找失败, 尝试自定义别名, ${it.message}")
            item(ItemAlias.getValue(name), limit, now)
        }.getOrThrow()
    }
}