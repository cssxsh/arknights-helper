package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.sync.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.*
import xyz.cssxsh.arknights.user.*
import xyz.cssxsh.mirai.arknights.data.*
import kotlin.properties.*
import kotlin.reflect.*

/**
 * 合成玉数量
 */
internal var CommandSenderOnMessage<*>.coin: Int by ArknightsUserData.sender()

/**
 * 玩家等级
 */
internal var CommandSenderOnMessage<*>.level: Int by ArknightsUserData.sender()

/**
 * 玩家理智恢复时间
 */
internal var CommandSenderOnMessage<*>.reason: Long by ArknightsUserData.sender()

/**
 * 玩家公招到达时间
 */
internal var CommandSenderOnMessage<*>.recruit: Map<Int, Long> by ArknightsUserData.sender()

/**
 * 玩家公招结果
 */
internal var CommandSenderOnMessage<*>.result: List<UserRecruit> by ArknightsUserData.sender()

/**
 * 玩家理智最大值
 */
internal val CommandSenderOnMessage<*>.max: Int by ReadOnlyProperty { that, _ -> ExcelData.const.playerApMap[that.level - 1] }

/**
 * 当前卡池
 */
internal var CommandSenderOnMessage<*>.pool: String by ArknightsPoolConfig.subject()

/**
 * 当前卡池规则
 */
internal val CommandSenderOnMessage<*>.rule: String by ReadOnlyProperty { that, _ -> ArknightsPoolConfig.rules[that.pool] }

/**
 * 答题互斥锁
 */
internal val CommandSenderOnMessage<*>.mutex: Mutex by SubjectDelegate { Mutex() }

@OptIn(ConsoleExperimentalApi::class)
internal fun <T, K, V> AbstractPluginData.delegate(key: T.() -> K) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value.getValue(thisRef.key())
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value[thisRef.key()] = value
    }
}

internal fun <V> AbstractPluginData.sender(): ReadWriteProperty<CommandSenderOnMessage<*>, V> = delegate {
    fromEvent.sender.id
}

internal fun <V> AbstractPluginData.subject(): ReadWriteProperty<CommandSenderOnMessage<*>, V> = delegate {
    fromEvent.subject.id
}