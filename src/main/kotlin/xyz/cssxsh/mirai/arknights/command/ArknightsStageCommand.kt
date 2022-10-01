package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsStageCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-stage", "方舟关卡",
    description = "明日方舟助手关卡指令"
) {

    @Handler
    public suspend fun CommandSender.handler(code: String, limit: Int = 3, now: Boolean = true) {
        val stages = ArknightsSubscriber.penguin.stages()
        val stage = stages.name(code)
        val matrices = ArknightsSubscriber.penguin.matrices().let { if (now) it.now() else it }.stage(stage)
        val items = ArknightsSubscriber.penguin.items()
        val zones = ArknightsSubscriber.penguin.zones()
        val message = buildMessageChain {
            val zone = zones.id(stage.zoneId)
            appendLine("[${stage.code}] <${zone.name}> (cost=${stage.cost}) 统计结果 By 企鹅物流数据统计")
            if (matrices.isEmpty()) {
                appendLine("列表为空，请尝试更新数据")
                return@buildMessageChain
            }
            var count = 0
            for ((matrix, item) in (matrices with items).sortedByDescending { it.rarity }) {
                if (count >= limit) break
                if (item.type != ItemType.MATERIAL) continue
                appendLine("=======> 掉落: ${item.name} (rarity=${item.rarity}) ")
                append(matrix, stage)
                count++
            }
        }

        sendMessage(message = message)
    }
}