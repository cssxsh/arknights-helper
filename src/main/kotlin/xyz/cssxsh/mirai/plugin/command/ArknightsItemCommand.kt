package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import xyz.cssxsh.mirai.plugin.*

object ArknightsItemCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "item", "材料",
    description = "明日方舟助手材料指令"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(name: String, limit: Int = 5) = sendMessage {
        runCatching {
            item(name = name, limit = limit)
        }.recoverCatching {
            sendMessage("查找失败, 尝试自定义别名, ${it.message}")
            item(name = ItemAlias.getValue(name), limit = limit)
        }.getOrThrow()
    }
}