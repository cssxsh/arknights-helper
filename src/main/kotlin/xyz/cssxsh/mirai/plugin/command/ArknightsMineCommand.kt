package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEventOrNull
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import xyz.cssxsh.arknights.mine.QuestionType
import xyz.cssxsh.mirai.plugin.*

object ArknightsMineCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "mine", "挖矿", "答题",
    description = "明日方舟助手挖矿指令"
) {

    private suspend inline fun <reified P : MessageEvent> P.nextAnswerOrNull(
        timeoutMillis: Long,
        priority: EventPriority = EventPriority.MONITOR,
        noinline filter: suspend P.(P) -> Boolean = { true }
    ): P? {
        require(timeoutMillis > 0) { "timeoutMillis must be > 0" }
        return syncFromEventOrNull<P, P>(timeoutMillis, priority) {
            takeIf { subject == this@nextAnswerOrNull.subject && filter(it, it) }
        }
    }

    @Handler
    suspend fun CommandSenderOnMessage<*>.handler(type: QuestionType = QuestionType.values().random()) {
        val question = type.random()

        val (reply, time) = mutex.withLock {
            sendMessage(fromEvent.message.quote() + question.toMessage())

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
        reply.toCommandSender().sendMessage {
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
                    coin += (question.coin * multiple)
                    appendLine("回答正确，合成玉${"%+d".format(question.coin)}*${multiple}")
                } else {
                    countQuestionType(type, 1)
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