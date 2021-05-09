package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import xyz.cssxsh.mirai.plugin.*

object ArknightsZoneCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "zone", "章节", "活动", "地图",
    description = "明日方舟助手地图指令"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(name: String, limit: Int = 1, decimal: Int = 2) = sendMessage {
        zone(name = name, limit = limit)
    }
}