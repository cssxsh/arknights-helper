package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.mirai.arknights.*

public object ArknightsItemCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-item", "方舟材料",
    description = "明日方舟助手材料指令"
) {

    @Handler
    public suspend fun CommandSender.handler(name: String, limit: Int = 5, now: Boolean = true) {
        val items = ArknightsSubscriber.penguin.items()
        val item = items.name(name)
        val matrices = ArknightsSubscriber.penguin.matrices().let { if (now) it.now() else it }.item(item)
        val stages = ArknightsSubscriber.penguin.stages()
        val zones = ArknightsSubscriber.penguin.zones()
        val message = buildMessageChain {
            appendLine("${item.alias.locale()} 统计结果 By 企鹅物流数据统计")
            if (matrices.isEmpty()) {
                appendLine("列表为空，请尝试更新数据")
                return@buildMessageChain
            }
            var count = 0
            for ((matrix, stage) in (matrices with stages).sortedBy { it.single }) {
                if (count >= limit) break
                if (stage.isGacha) continue
                if (stage.zoneId == "gachabox") continue
                val zone = zones.id(stage.zoneId)
                appendLine("====> 作战: [${stage.code}] <${zone.name}> (cost=${stage.cost})")
                append(matrix, stage)
                count++
            }
        }

        sendMessage(message = message)
    }
}