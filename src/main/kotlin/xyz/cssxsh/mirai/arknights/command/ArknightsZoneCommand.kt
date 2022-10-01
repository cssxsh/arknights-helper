package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsZoneCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-zone", "方舟章节", "方舟活动", "方舟地图",
    description = "明日方舟助手地图指令"
) {

    @Handler
    public suspend fun CommandSender.handler(name: String, limit: Int = 1, now: Boolean = true) {
        val zones = ArknightsSubscriber.penguin.zones()
        val zone = zones.name(name)
        val stages = ArknightsSubscriber.penguin.stages().zone(zone)
        val matrices = ArknightsSubscriber.penguin.matrices().let { if (now) it.now() else it }
        val items = ArknightsSubscriber.penguin.items()
        val message = buildMessageChain {
            appendLine("<${zone.name}> 统计结果 By 企鹅物流数据统计")
            for (stage in stages) {
                val target = matrices.stage(stage)
                if (target.isEmpty()) continue
                appendLine("[${stage.code}] <${zone.name}> (cost=${stage.cost})")
                var count = 0
                for ((matrix, item) in (target with items).sortedByDescending { it.rarity }) {
                    if (count >= limit) break
                    if (item.type != ItemType.MATERIAL) continue
                    appendLine("=======> 掉落: ${item.name} (rarity=${item.rarity}) ")
                    append(matrix, stage)
                    count++
                }
            }
        }

        sendMessage(message = message)
    }
}