package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.sync.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.mirai.arknights.data.*
import java.time.*
import kotlin.properties.*
import kotlin.reflect.*

internal val logger by lazy {
    try {
        ArknightsHelperPlugin.logger
    } catch (_: ExceptionInInitializerError) {
        MiraiLogger.Factory.create(ArknightsSubscriber::class)
    }
}

internal suspend fun CommandSenderOnMessage<*>.nextContent(): String {
    return fromEvent.nextMessage { message.anyIsInstance<PlainText>() }
        .firstIsInstance<PlainText>().content
}

internal suspend fun CommandSenderOnMessage<*>.reply(block: suspend UserCommandSender.() -> Message) {
    try {
        sendMessage(message = fromEvent.message.quote() + block.invoke(this as UserCommandSender))
    } catch (cause: Exception) {
        logger.warning({ "发送消息失败" }, cause)
        sendMessage(message = "发送消息失败， ${cause.message}")
    }
}

/**
 * 合成玉数量
 */
internal var User.coin: Int by ArknightsUserData.delegate()

/**
 * 当前卡池
 */
internal var Contact.pool: String by ArknightsPoolConfig.delegate()

@OptIn(ConsoleExperimentalApi::class)
internal fun <V> AbstractPluginData.delegate() = object : ReadWriteProperty<Contact, V> {
    override fun getValue(thisRef: Contact, property: KProperty<*>): V {
        return findBackingFieldValue<MutableMap<Long, V>>(property.name)!!.value.getValue(thisRef.id)
    }

    override fun setValue(thisRef: Contact, property: KProperty<*>, value: V) {
        findBackingFieldValue<MutableMap<Long, V>>(property.name)!!.value[thisRef.id] = value
    }
}

internal class SubjectDelegate<T>(private val default: (Contact) -> T) :
    ReadWriteProperty<CommandSenderOnMessage<*>, T> {

    private val map: MutableMap<Contact, T> = HashMap()

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        map[thisRef.fromEvent.subject] = value
    }

    @Synchronized
    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return map.getOrPut(thisRef.fromEvent.subject) { default(thisRef.fromEvent.subject) }
    }
}

private fun duration(millis: Long) = with(Duration.ofMillis(millis)) { "${toMinutesPart()}m${toSecondsPart()}s" }

private fun Double.intercept(decimal: Int = 2): String = "%.${decimal}f".format(this)

private fun Double.percentage(decimal: Int = 2): String = "${(this * 100).intercept(decimal)}%"

internal fun MessageChainBuilder.append(matrix: Matrix, stage: Stage) {
    val single = (matrix to stage).single
    val short = (matrix to stage).short
    appendLine("概率: ${matrix.quantity}/${matrix.times}=${matrix.probability.percentage()}")
    appendLine("单件期望理智: ${single.intercept()}")
    appendLine("最短通关用时: ${duration(stage.minClearTime)}")
    appendLine("单件期望用时: ${duration(short)}")
}