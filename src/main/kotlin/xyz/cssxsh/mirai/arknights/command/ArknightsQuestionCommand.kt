package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.arknights.*
import java.time.*

public object ArknightsQuestionCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "question", "问题",
    description = "明日方舟助手自定义问题指令"
) {

    @SubCommand("detail", "详情")
    @Description("查看问题详情")
    public suspend fun CommandSenderOnMessage<*>.detail(name: String): Unit = reply {
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
    public suspend fun CommandSenderOnMessage<*>.list(): Unit = reply {
        CustomQuestions.entries.joinToString("\n") { (name, question) ->
            "$name => ${question.problem}"
        }.toPlainText()
    }

    @SubCommand("delete", "删除")
    @Description("删除指定问题")
    public suspend fun CommandSenderOnMessage<*>.delete(name: String): Unit = reply {
        (CustomQuestions.remove(name)?.let { "问题：${it.problem} 已删除" } ?: "删除失败").toPlainText()
    }

    @SubCommand("add", "添加")
    @Description("设置问题")
    public suspend fun CommandSenderOnMessage<*>.add(): Unit = reply {
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
    public suspend fun CommandSenderOnMessage<*>.count(): Unit = reply {
        tableMineCount().toPlainText()
    }
}