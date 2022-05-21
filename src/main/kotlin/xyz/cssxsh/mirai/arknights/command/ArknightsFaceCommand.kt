package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.mirai.arknights.*

object ArknightsFaceCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "arkface", "方舟表情",
    description = "明日方舟助手表情指令"
), ArknightsHelperCommand {

    @SubCommand("random", "随机")
    @Description("表情随机")
    suspend fun CommandSenderOnMessage<*>.random(id: Int = 0) = sendMessage {
        (ArknightsFaceData.faces[id] ?: ArknightsFaceData.faces.values.random()).random().also {
            logger.info {
                "${it.id} ${it.title} ${it.content} ${it.key} ${it.md5}"
            }
        }.impl()
    }

    @SubCommand("list", "列表")
    @Description("表情列表")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage {
        ArknightsFaceData.faces.toMessage()
    }

    @OptIn(MiraiExperimentalApi::class)
    @SubCommand("detail", "详情")
    @Description("表情详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        val face = fromEvent.nextMessage { message.anyIsInstance<MarketFace>() }.firstIsInstance<MarketFace>()
        // logger.info { "KEY: ${face.hash}" }
        ArknightsFaceData.faces.getValue(face.id).first { it.md5 == face.md5 }.toMessage()
    }
}