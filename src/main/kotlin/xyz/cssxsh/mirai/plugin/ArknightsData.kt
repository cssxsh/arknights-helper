package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.sync.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.console.util.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.user.*
import xyz.cssxsh.arknights.weibo.*
import kotlin.properties.*
import kotlin.reflect.*

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
 * 答题互斥锁
 */
val CommandSenderOnMessage<*>.mutex: Mutex by SubjectDelegate { Mutex() }

sealed interface ArknightsHelperData : PluginData {

    companion object : Collection<ArknightsHelperData> {
        private val list by lazy {
            ArknightsHelperData::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = list.size

        override fun contains(element: ArknightsHelperData): Boolean = list.contains(element)

        override fun containsAll(elements: Collection<ArknightsHelperData>): Boolean = list.containsAll(elements)

        override fun isEmpty(): Boolean = list.isEmpty()

        override fun iterator(): Iterator<ArknightsHelperData> = list.iterator()

        @OptIn(ConsoleExperimentalApi::class)
        operator fun get(name: String): ArknightsHelperData = list.first { it.saveName.equals(name, true) }
    }
}

object ArknightsUserData : AutoSavePluginData("user"), ArknightsHelperData {
    @ValueDescription("Key 是QQ号，Value是合成玉数值")
    val coin by value<MutableMap<Long, Int>>().withDefault { 3_000 }

    @ValueDescription("Key 是QQ号，Value是玩家等级")
    var level by value<MutableMap<Long, Int>>().withDefault { ExcelData.const.maxPlayerLevel }

    @ValueDescription("Key 是QQ号，Value是理智预警时间戳")
    val reason by value<MutableMap<Long, Long>>().withDefault { 0 }

    @ValueDescription("Key 是QQ号，Value是公招预警预警时间戳")
    val recruit by value<MutableMap<Long, Map<Int, Long>>>().withDefault { emptyMap() }

    @ValueDescription("Key 是QQ号，Value是公招结果")
    val result by value<MutableMap<Long, List<UserRecruit>>>().withDefault { emptyList() }
}

object ArknightsPoolData : AutoSavePluginConfig("pool"), ArknightsHelperData {
    @ValueDescription("Key 是QQ号/QQ群号，Value是规则名")
    val pool by value<MutableMap<Long, String>>().withDefault { GachaPoolRule.NORMAL.name }

    private val default get() = GachaPoolRule.values().associate { it.name to it.rule }

    @ValueDescription("Key 规则名，Value是卡池规则")
    val rules by value<MutableMap<String, String>>().withDefault { default.getValue(it) }
}

object ArknightsMineData : AutoSavePluginData("mine"), ArknightsHelperData {
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
        timeout = 30 * 1000L
    )

    @ValueDescription("Key 是问题ID，Value是问题")
    val question by value(mutableMapOf("default" to default))

    @ValueDescription("正确数 错误数 和 超时数")
    val count by value(mutableMapOf<QuestionType, MutableList<Int>>())
}

object ArknightsTaskData : AutoSavePluginConfig("task"), ArknightsHelperData {
    @ValueDescription("开启了提醒的QQ号/QQ群号(正负性区别，QQ群是负数)")
    val contacts by value<MutableSet<Long>>()

    @ValueDescription("蹲饼轮询间隔，单位分钟，默认5分钟")
    var interval by value(5)
}

object ArknightsConfig : ReadOnlyPluginConfig("config"), ArknightsHelperData {

    @ValueDescription("Key 是别名 Value 是干员名")
    val roles by value(
        mutableMapOf(
            "羊" to "艾雅法拉",
            "鳄鱼" to "艾丝黛尔"
        )
    )

    @ValueDescription("Key 是别名 Value 是材料名")
    val items by value(mutableMapOf("绿管" to "晶体元件"))

    @ValueName("auto_add_guard")
    @ValueDescription("开启新好友或新群自动蹲饼")
    val auto by value(true)

    @ValueName("video")
    @ValueDescription("开启订阅的b站视频类型 ANIME, MUSIC, GAME, ENTERTAINMENT")
    val video by value(VideoDataType.values().toSet())

    @ValueName("blog")
    @ValueDescription("开启订阅的微博号 BYPRODUCT, MOUNTEN, HISTORICUS")
    val blog by value(BlogUser.values().toSet())

    @ValueDescription("Key 是表情ID, Value 是表情Hash")
    val faces by value(DefaultFaceItems)
}

@OptIn(ConsoleExperimentalApi::class)
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