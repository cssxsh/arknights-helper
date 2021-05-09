package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEventOrNull
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.mine.QuestionType
import xyz.cssxsh.mirai.plugin.*
import kotlin.time.measureTimedValue

object ArknightsMineCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "mine", "挖矿", "答题",
    description = "明日方舟助手挖矿指令",
    overrideContext = ArknightsCommandArgumentContext
) {

    private suspend inline fun <reified P : MessageEvent> P.nextAnswerOrNull(
        timeoutMillis: Long,
        priority: EventPriority = EventPriority.MONITOR,
        noinline filter: suspend P.(P) -> Boolean = { true }
    ): P? {
        require(timeoutMillis > 0) { "timeoutMillis must be > 0" }
        return syncFromEventOrNull<P, P>(timeoutMillis, priority) {
            takeIf { this.subject == this@nextAnswerOrNull.subject }?.takeIf { filter(it, it) }
        }
    }

    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(vararg list: String) {
        // XXX
        val types = list.map { enumValueOf<QuestionType>(it.toUpperCase()) }.toTypedArray().ifEmpty { QuestionType.values() }
        val question = types.random().build()
        sendMessage(question.getContent())

        val (reply, time) = mutex.withLock {
            measureTimedValue {
                fromEvent.nextAnswerOrNull(question.timeout) { next ->
                    next.message.content.toUpperCase().any { it in question.options.keys }
                }
            }
        }
        if (reply == null) {
            sendMessage("回答超时")
        } else {
            val answer = reply.message.content.toUpperCase().filter { it in question.options.keys }.toSet()
            var multiple = 1
            var deduct = false
            val origin = fromEvent.sender
            reply.toCommandSender().sendMessage {
                buildMessageChain {
                    if (reply.sender != origin) {
                        multiple *= 10
                        deduct = true
                        appendLine("抢答（合成玉翻10倍）")
                    }
                    if (time.toLongMilliseconds() * 3 < question.timeout) {
                        multiple *= 5
                        appendLine("快速回答（合成玉翻5倍）")
                    }
                    if (answer == question.answer) {
                        coin += (question.coin * multiple)
                        appendLine("回答正确，合成玉${"%+d".format(question.coin)}*${multiple}")
                    } else {
                        appendLine("回答错误${answer}, ${question.tips ?: "参考答案${question.answer}"}")
                        if (deduct) {
                            coin -= (question.coin * multiple)
                            appendLine("抢答，合成玉${"%+d".format(question.coin * -1)}*${multiple}")
                        }
                    }
                }
            }
        }
    }
}