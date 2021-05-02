package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import xyz.cssxsh.arknights.excel.GachaPoolRule
import xyz.cssxsh.mirai.plugin.const
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var CommandSenderOnMessage<*>.coin: Int by ArknightsUserData.sender()

var CommandSenderOnMessage<*>.level: Int by ArknightsUserData.sender()

var CommandSenderOnMessage<*>.reason: Long by ArknightsUserData.sender()

var CommandSenderOnMessage<*>.recruit: Map<Int, Long> by ArknightsUserData.sender()

val CommandSenderOnMessage<*>.max: Int by ReadOnlyProperty { that, _ -> const.playerApMap[that.level - 1] }

var CommandSenderOnMessage<*>.pool: String by ArknightsPoolData.subject()

val CommandSenderOnMessage<*>.rule: String by ReadOnlyProperty { that, _ -> ArknightsPoolData.rules[that.pool] }

internal val rules get() = ArknightsPoolData.rules

object ArknightsUserData : AutoSavePluginData("user") {
    @Suppress("unused")
    @ValueName("coin")
    val coin by value<MutableMap<Long, Int>>().withDefault { 6_000_000 }

    @Suppress("unused")
    @ValueName("level")
    var level by value<MutableMap<Long, Int>>().withDefault { const.maxPlayerLevel }

    @Suppress("unused")
    @ValueName("reason")
    val reason by value<MutableMap<Long, Long>>().withDefault { 0 }

    @Suppress("unused")
    @ValueName("recruit")
    val recruit by value<MutableMap<Long, Map<Int, Long>>>().withDefault { emptyMap() }
}

object ArknightsPoolData : AutoSavePluginData("pool") {
    @Suppress("unused")
    @ValueName("pool")
    val pool by value<MutableMap<Long, String>>().withDefault { GachaPoolRule.NORMAL.name }

    private val default = GachaPoolRule.values().associate { it.name to it.rule }

    @ValueName("rules")
    val rules by value<MutableMap<String, String>>().withDefault { default.getValue(it) }
}

fun <T, K, V> AbstractPluginData.delegate(key: T.() -> K) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value.getValue(thisRef.key())
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value[thisRef.key()] = value
    }
}

fun <V> AbstractPluginData.sender() = delegate<CommandSenderOnMessage<*>, Long, V> { fromEvent.sender.id }

fun <V> AbstractPluginData.subject() = delegate<CommandSenderOnMessage<*>, Long, V> { fromEvent.subject.id }