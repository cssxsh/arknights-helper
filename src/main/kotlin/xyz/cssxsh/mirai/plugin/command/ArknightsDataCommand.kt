package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*

object ArknightsDataCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "data", "数据",
    description = "明日方舟助手数据指令"
) {

    @SubCommand("arknights", "方舟")
    @Description("方舟数据下载")
    suspend fun CommandSenderOnMessage<*>.arknights() = sendMessage {
        runCatching {
            ExcelData.download(flush = true)
            "ExcelData 数据加载完毕"
        }.getOrElse {
            "ExcelData 数据加载失败, ${it.message}"
        }.toPlainText()
    }

    @SubCommand("penguin", "企鹅", "企鹅物流")
    @Description("企鹅物流数据下载")
    suspend fun CommandSenderOnMessage<*>.penguin() = sendMessage {
        runCatching {
            PenguinData.download(flush = true)
            "PenguinData 数据加载完毕"
        }.getOrElse {
            "PenguinData 数据加载失败, ${it.message}"
        }.toPlainText()
    }

    @SubCommand("name", "alias", "别称", "别名")
    @Description("企鹅物流材料别称")
    suspend fun CommandSenderOnMessage<*>.name() = sendMessage { alias() }
}