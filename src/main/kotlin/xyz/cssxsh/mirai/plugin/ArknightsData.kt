package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.contact.Contact
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.mine.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 合成玉数量
 */
var CommandSenderOnMessage<*>.coin: Int by ArknightsUserData.sender()

/**
 * 玩家等级
 */
var CommandSenderOnMessage<*>.level: Int by ArknightsUserData.sender()

/**
 * 玩家理智恢复时间
 */
var CommandSenderOnMessage<*>.reason: Long by ArknightsUserData.sender()

/**
 * 玩家公招到达时间
 */
var CommandSenderOnMessage<*>.recruit: Map<Int, Long> by ArknightsUserData.sender()

/**
 * 玩家理智最大值
 */
val CommandSenderOnMessage<*>.max: Int by ReadOnlyProperty { that, _ -> ExcelData.const.playerApMap[that.level - 1] }

/**
 * 当前卡池
 */
var CommandSenderOnMessage<*>.pool: String by ArknightsPoolData.subject()

/**
 * 当前卡池规则
 */
val CommandSenderOnMessage<*>.rule: String by ReadOnlyProperty { that, _ -> ArknightsPoolData.rules[that.pool] }

/**
 * 抽卡互斥锁
 */
val CommandSenderOnMessage<*>.mutex: Mutex by SubjectDelegate { Mutex() }

/**
 * XXX
 */
var CommandSenderOnMessage<*>.task: Boolean by ArknightsTaskData.subject()

/**
 * 卡池规则MAP
 */
internal val rules get() = ArknightsPoolData.rules

/**
 * XXX
 */
internal val tasks get() = ArknightsTaskData.task

/**
 * 自定义问题MAP
 */
internal val questions get() = ArknightsMineData.questions

object ArknightsUserData : AutoSavePluginData("user") {
    @Suppress("unused")
    @ValueName("coin")
    val coin by value<MutableMap<Long, Int>>().withDefault { 3_000 }

    @Suppress("unused")
    @ValueName("level")
    var level by value<MutableMap<Long, Int>>().withDefault { ExcelData.const.maxPlayerLevel }

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

object ArknightsMineData : AutoSavePluginData("mine") {

    private val default: MutableMap<String, CustomQuestion>.() -> Unit = {
        put(
            "default", CustomQuestion(
                problem = "以下那个干员被称为老女人",
                options = mapOf(
                    "凯尔希" to true,
                    "华法琳" to false,
                    "黑" to false,
                    "斯卡蒂" to false
                ),
                tips = "还行，合成玉没有被扣",
                coin = -1000,
                timeout = 30000
            )
        )
    }

    @ValueName("question")
    val questions by value(default)
}

object ArknightsTaskData : AutoSavePluginData("task") {
    @ValueName("task")
    val task by value<MutableMap<Long, Boolean>>().withDefault { false }
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

class SubjectDelegate<T>(private val default: (Contact) -> T) : ReadWriteProperty<CommandSenderOnMessage<*>, T> {

    private val map: MutableMap<Contact, T> = mutableMapOf()

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        map[thisRef.fromEvent.subject] = value
    }

    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return map.getOrPut(thisRef.fromEvent.subject) { default(thisRef.fromEvent.subject) }
    }
}