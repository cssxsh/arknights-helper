package xyz.cssxsh.mirai.arknights.command

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import xyz.cssxsh.mirai.arknights.*

public object ArknightsAudioCommand : SimpleCommand(
    owner = ArknightsHelperPlugin,
    "ark-audio", "方舟听力", "方舟语音",
    description = "明日方舟助手听力指令"
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

    private val CommandSenderOnMessage<*>.mutex: Mutex by SubjectDelegate { Mutex() }

    private val options = 'A' .. 'D'

    @Handler
    public suspend fun CommandSenderOnMessage<*>.handler() {
        val table = ArknightsSubscriber.excel.word().characterWords
        val categories = table.values.groupBy { it.voiceTitle }.values.random()
        val words = (options zip categories.shuffled()).toMap()
        val characters = ArknightsSubscriber.excel.character()
        val target = words.values.random()
        val info = characters.getValue(target.character)
        val file = ArknightsSubscriber.static.voice(character = info, word = target)
        val audio = file.toExternalResource().use { (subject as AudioSupported).uploadAudio(it) }

        sendMessage(buildMessageChain {
            appendLine("[AUDIO]<1200>：请听取语音内容，然后选择对应选项 (180s内作答)")
            for ((option, word) in words) {
                val character = characters.getValue(word.character)
                append(option).append('.').append(character.name).append('\n')
            }
        })

        val time: Long
        val reply: MessageEvent?
        mutex.withLock {
            sendMessage(message = audio)

            val start = System.currentTimeMillis()
            reply = fromEvent.nextAnswerOrNull(180_000) { next ->
                next.message.content.uppercase().any { it in 'A'..'D' }
            }
            time = System.currentTimeMillis() - start
        }
        if (reply == null) {
            sendMessage("回答超时")
            return
        }
        val option = reply.message.content.uppercase().first { it in options }
        val answer = words[option]
        var multiple = 1
        var deduct = false
        val origin = fromEvent.sender
        delay(3_000L)
        reply.toCommandSender().reply {
            buildMessageChain {
                if (reply.sender != origin) {
                    multiple *= 10
                    deduct = true
                    appendLine("抢答（合成玉翻10倍）")
                }
                if (time * 3 < 60_000) {
                    multiple *= 5
                    appendLine("快速回答（合成玉翻5倍）")
                }
                if (answer == target) {
                    user.coin += (1000 * multiple)
                    appendLine("回答正确，合成玉${"%+d".format(1200)}*${multiple}")
                } else {
                    appendLine("回答错误, ${info.name} - ${target.voiceTitle}")
                    if (deduct) {
                        user.coin -= 1000 * multiple
                        appendLine("抢答，合成玉${"%+d".format(1200 * -1)}*${multiple}")
                    }
                }
            }
        }
    }
}