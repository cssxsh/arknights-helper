package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.user.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.seconds

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
 * 玩家公招结果
 */
var CommandSenderOnMessage<*>.result: List<UserRecruit> by ArknightsUserData.sender()

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

object ArknightsUserData : AutoSavePluginData("user") {
    @Suppress("unused")
    @ValueDescription("Key 是QQ号，Value是合成玉数值")
    val coin by value<MutableMap<Long, Int>>().withDefault { 3_000 }

    @Suppress("unused")
    @ValueDescription("Key 是QQ号，Value是玩家等级")
    var level by value<MutableMap<Long, Int>>().withDefault { ExcelData.const.maxPlayerLevel }

    @Suppress("unused")
    @ValueDescription("Key 是QQ号，Value是理智预警时间戳")
    val reason by value<MutableMap<Long, Long>>().withDefault { 0 }

    @Suppress("unused")
    @ValueDescription("Key 是QQ号，Value是公招预警预警时间戳")
    val recruit by value<MutableMap<Long, Map<Int, Long>>>().withDefault { emptyMap() }

    @Suppress("unused")
    @ValueDescription("Key 是QQ号，Value是公招结果")
    val result by value<MutableMap<Long, List<UserRecruit>>>().withDefault { emptyList() }
}

object ArknightsPoolData : AutoSavePluginConfig("pool") {
    @Suppress("unused")
    @ValueDescription("Key 是QQ号/QQ群号，Value是规则名")
    val pool by value<MutableMap<Long, String>>().withDefault { GachaPoolRule.NORMAL.name }

    private val default get() = GachaPoolRule.values().associate { it.name to it.rule }

    @ValueDescription("Key 规则名，Value是卡池规则")
    val rules by value<MutableMap<String, String>>().withDefault { default.getValue(it) }
}

object ArknightsMineData : AutoSavePluginData("mine") {
    private val default = CustomQuestion(
        problem = "以下那个干员被称为老女人",
        options = mapOf(
            "凯尔希" to true,
            "华法琳" to false,
            "黑" to false,
            "斯卡蒂" to false
        ),
        tips = "还行，合成玉没有被扣",
        coin = -1000,
        timeout = (30).seconds.toLongMilliseconds()
    )

    @ValueDescription("Key 是问题ID，Value是问题")
    val question by value(mutableMapOf("default" to default))
}

object ArknightsTaskData : AutoSavePluginConfig("task") {
    @ValueDescription("Key 是QQ号/QQ群号，Value是是否开启了提醒")
    val contacts by value<MutableSet<Long>>()

    @ValueDescription("蹲饼轮询间隔，单位分钟，默认5分钟")
    var interval by value<Int>(5)
}

object ArknightsConfig : ReadOnlyPluginConfig("config") {
    @ValueDescription("Key 是表情ID, Value 是表情Hash")
    val faces by value(DefaultItems)

    private val DefaultRoles = mutableMapOf(
        "羊" to "艾雅法拉",
        "鳄鱼" to "艾丝黛尔"
    )

    @ValueDescription("Key 是别名 Value 是干员名")
    val roles by value(DefaultRoles)

    @ValueDescription("Key 是别名 Value 是材料名")
    val items by value(mutableMapOf<String, String>())
}

/**
 * 通过正负号区分群和用户
 */
internal val Contact.delegate get() = if (this is Group) id * -1 else id

fun <T, K, V> AbstractPluginData.delegate(key: T.() -> K) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value.getValue(thisRef.key())
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value[thisRef.key()] = value
    }
}

fun <V> AbstractPluginData.sender() = delegate<CommandSenderOnMessage<*>, Long, V> { fromEvent.sender.id }

fun <V> AbstractPluginData.subject() = delegate<CommandSenderOnMessage<*>, Long, V> { fromEvent.subject.delegate }

class SubjectDelegate<T>(private val default: (Contact) -> T) : ReadWriteProperty<CommandSenderOnMessage<*>, T> {
    private val map: MutableMap<Contact, T> = mutableMapOf()

    override fun setValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>, value: T) {
        map[thisRef.fromEvent.subject] = value
    }

    override fun getValue(thisRef: CommandSenderOnMessage<*>, property: KProperty<*>): T {
        return map.getOrPut(thisRef.fromEvent.subject) { default(thisRef.fromEvent.subject) }
    }
}