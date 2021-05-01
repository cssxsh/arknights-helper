package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


object ArknightsUserData: AutoSavePluginData("user") {
    @ValueName("coin")
    val coin by value<MutableMap<Long, Int>>().withDefault { 6_000_000 }

    @ValueName("reason")
    val reason by value<MutableMap<Long, Int>>().withDefault { 0 }
}

inline fun <reified T> ArknightsUserData.delegate() = object : ReadWriteProperty<CommandSenderOnMessage<*>, T> {
    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return findBackingFieldValue<MutableMap<Long, T>>(property.name)!!.value.getValue(thisRef.fromEvent.subject.id)
    }

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        findBackingFieldValue<MutableMap<Long, T>>(property.name)!!.value[thisRef.fromEvent.subject.id] = value
    }
}
