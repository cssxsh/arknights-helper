package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.plugin.*
import java.time.*

object ArknightsQuestionCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "question", "问题",
    description = "明日方舟助手自定义问题指令"
), ArknightsHelperCommand {

    @SubCommand("detail", "详情")
    @Description("查看问题详情")
    suspend fun CommandSenderOnMessage<*>.detail(name: String) = sendMessage {
        requireNotNull(CustomQuestions[name]) { "没有找到题目${name}" }.let {
            buildMessageChain {
                appendLine("问题：${it.problem}")
                appendLine("选项：${it.options}")
                appendLine("合成玉：${it.coin}")
                appendLine("限时：${it.timeout / 1000}s")
            }
        }
    }

    @SubCommand("list", "列表")
    @Description("列出已经设置的自定义问题")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage {
        CustomQuestions.entries.joinToString("\n") { (name, question) ->
            "$name => ${question.problem}"
        }.toPlainText()
    }

    @SubCommand("delete", "删除")
    @Description("删除指定问题")
    suspend fun CommandSenderOnMessage<*>.delete(name: String) = sendMessage {
        (CustomQuestions.remove(name)?.let { "问题：${it.problem} 已删除" } ?: "删除失败").toPlainText()
    }

    @SubCommand("add", "添加")
    @Description("设置问题")
    suspend fun CommandSenderOnMessage<*>.add() = sendMessage {
        sendMessage("问题:")
        val problem = nextContent()
        sendMessage("正确选项(按行分割):")
        val right = nextContent().lines()
        sendMessage("错误选项(按行分割):")
        val error = nextContent().lines()
        sendMessage("合成玉:")
        val coin = nextContent().toInt()
        sendMessage("提示:")
        val tips = nextContent()
        sendMessage("时间(单位秒):")
        val duration = nextContent().toLong() * 1000
        val question = CustomQuestion(problem, right, error, coin, tips, duration)
        CustomQuestions += ("${fromEvent.sender.nick} ${OffsetDateTime.now().withNano(0)}" to question)
        "问题${question} 已添加".toPlainText()
    }

    @SubCommand("count", "统计")
    @Description("问题统计")
    suspend fun CommandSenderOnMessage<*>.count() = sendMessage { tableMineCount().toPlainText() }
}