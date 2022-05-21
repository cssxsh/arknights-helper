package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.arknights.*

object ArknightsZoneCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "zone", "章节", "活动", "地图",
    description = "明日方舟助手地图指令"
), ArknightsHelperCommand {

    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(name: String, limit: Int = 1, now: Boolean = true) = sendMessage {
        zone(name, limit, now)
    }
}