package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.message.nextMessage
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.plugin.*
import java.time.OffsetDateTime
import kotlin.time.*

object ArknightsQuestionCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "question", "问题",
    description = "明日方舟助手自定义问题指令"
) {
    @SubCommand("detail", "详情")
    @Description("查看问题详情")
    suspend fun CommandSenderOnMessage<*>.detail(name: String) = sendMessage {
        requireNotNull(questions[name]) { "没有找到题目${name}" }.let {
            buildMessageChain {
                appendLine("问题：${it.problem}")
                appendLine("选项：${it.options}")
                appendLine("合成玉：${it.coin}")
                appendLine("合成玉：${it.timeout.milliseconds}")
            }
        }
    }

    @SubCommand("list", "列表")
    @Description("列出已经设置的自定义问题")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage {
        questions.entries.joinToString("\n") { (name, question) ->
            "$name => ${question.problem}"
        }.toPlainText()
    }

    @SubCommand("delete", "删除")
    @Description("删除指定问题")
    suspend fun CommandSenderOnMessage<*>.delete(name: String) = sendMessage {
        (questions.remove(name)?.let { "问题：${it.problem} 已删除" } ?: "删除失败").toPlainText()
    }

    @SubCommand("add", "添加")
    @Description("设置问题")
    suspend fun CommandSenderOnMessage<*>.add() = sendMessage {
        sendMessage("问题:")
        val problem = fromEvent.nextMessage().content
        sendMessage("正确选项(按行分割):")
        val right = fromEvent.nextMessage().content.lines()
        sendMessage("错误选项(按行分割):")
        val error = fromEvent.nextMessage().content.lines()
        sendMessage("合成玉:")
        val coin = fromEvent.nextMessage().content.toInt()
        sendMessage("提示:")
        val tips = fromEvent.nextMessage().content
        sendMessage("时间(单位秒):")
        val duration = fromEvent.nextMessage().content.toLong().seconds
        val question = CustomQuestion(problem, right, error, coin, tips, duration)
        questions += ("${fromEvent.sender.nick} ${OffsetDateTime.now().withNano(0)}" to question)
        "问题${question} 已添加".toPlainText()
    }
}