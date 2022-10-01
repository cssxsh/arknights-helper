package xyz.cssxsh.mirai.arknights.command

import kotlinx.coroutines.sync.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsMineCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-mine", "方舟挖矿", "方舟答题",
    description = "明日方舟助手挖矿指令"
) {

    private suspend inline fun <reified P : MessageEvent> P.nextAnswerOrNull(
        timeoutMillis: Long,
        priority: EventPriority = EventPriority.MONITOR,
        noinline filter: suspend (P) -> Boolean = { true }
    ): P? {
        return withTimeoutOrNull(timeoutMillis) {
            bot.eventChannel.nextEvent(priority) { next ->
                subject.id == next.subject.id && filter(next)
            }
        }
    }

    private fun countQuestionType(type: QuestionType, mode: Int) {
        ArknightsMineData.count.compute(type) { _, s ->
            (s ?: mutableListOf(0, 0, 0)).apply { this[mode] += 1 }
        }
    }

    @Handler
    public suspend fun CommandSenderOnMessage<*>.handler(type: QuestionType = QuestionType.values().random()) {
        val question = type.random(ArknightsQuestionLoader)

        val (reply, time) = mutex.withLock {
            sendMessage(buildMessageChain {
                append(fromEvent.message.quote())
                appendLine("[${type}]<${question.coin}>：${question.problem} (${question.timeout / 1000}s内作答)")
                for ((index, text) in question.options) {
                    appendLine("${index}.${text}")
                }
            })

            val start = System.currentTimeMillis()
            fromEvent.nextAnswerOrNull(question.timeout) { next ->
                next.message.content.uppercase().any { it in question.options.keys }
            } to System.currentTimeMillis() - start
        }
        if (reply == null) {
            sendMessage("回答超时")
            countQuestionType(type, 2)
            return
        }
        val answer = reply.message.content.uppercase().filter { it in question.options.keys }.toSet()
        var multiple = 1
        var deduct = false
        val origin = fromEvent.sender
        reply.toCommandSender().reply {
            buildMessageChain {
                appendLine(question.problem)
                if (reply.sender != origin) {
                    multiple *= 10
                    deduct = true
                    appendLine("抢答（合成玉翻10倍）")
                }
                if (time * 3 < question.timeout) {
                    multiple *= 5
                    appendLine("快速回答（合成玉翻5倍）")
                }
                if (answer == question.answer) {
                    countQuestionType(type, 0)
                    user.coin += (question.coin * multiple)
                    appendLine("回答正确，合成玉${"%+d".format(question.coin)}*${multiple}")
                } else {
                    countQuestionType(type, 1)
                    appendLine("回答错误${answer}, ${question.tips ?: "参考答案${question.answer}"}")
                    if (deduct) {
                        user.coin -= (question.coin * multiple)
                        appendLine("抢答，合成玉${"%+d".format(question.coin * -1)}*${multiple}")
                    }
                }
            }
        }
    }
}