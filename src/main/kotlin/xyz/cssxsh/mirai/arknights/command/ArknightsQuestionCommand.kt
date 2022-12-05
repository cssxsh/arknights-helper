package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*
import java.time.*

public object ArknightsQuestionCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-question", "方舟问题",
    description = "明日方舟助手自定义问题指令"
) {

    private val count get() = ArknightsMineData.count

    @SubCommand("detail", "详情")
    @Description("查看问题详情")
    public suspend fun CommandSenderOnMessage<*>.detail(name: String): Unit = reply {
        val question = requireNotNull(ArknightsQuestionLoader.custom.question[name]) { "没有找到题目${name}" }
        buildMessageChain {
            appendLine("问题：${question.problem}")
            appendLine("选项：${question.options}")
            appendLine("合成玉：${question.coin}")
            appendLine("限时：${question.timeout / 1000}s")
        }
    }

    @SubCommand("list", "列表")
    @Description("列出已经设置的自定义问题")
    public suspend fun CommandSenderOnMessage<*>.list(): Unit = reply {
        buildMessageChain {
            for ((name, question) in ArknightsQuestionLoader.custom.question) {
                appendLine("$name => ${question.problem}")
            }
        }
    }

    @SubCommand("delete", "删除")
    @Description("删除指定问题")
    public suspend fun CommandSenderOnMessage<*>.delete(name: String): Unit = reply {
        (ArknightsMineData.question.remove(name)?.let { "问题：${it.problem} 已删除" } ?: "删除失败")
            .toPlainText()
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
        val question = CustomQuestionInfo(problem, right, error, coin, tips, duration)
        val name = "${fromEvent.sender.nick} ${OffsetDateTime.now().withNano(0)}"
        ArknightsMineData.question[name] = question
        "问题${question} 已添加".toPlainText()
    }

    @SubCommand("count", "统计")
    @Description("问题统计")
    public suspend fun CommandSenderOnMessage<*>.count(): Unit = reply {
        buildMessageChain {
            appendLine("# 答题统计")
            appendLine("| 类型 | 正确 | 错误 | 超时 | 总计 |")
            appendLine("|:----:|:----:|:----:|:----:|:----:|")
            count.forEach { type, (f, s, t) ->
                appendLine("| $type | $f | $s | $t | ${f + s + t} |")
            }
        }
    }
}