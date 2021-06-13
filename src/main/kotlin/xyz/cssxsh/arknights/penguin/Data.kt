package xyz.cssxsh.arknights.penguin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.cssxsh.arknights.*
import java.time.Instant
import java.time.OffsetDateTime

/**
 * 根据名字查找
 * @see Id.id
 */
fun <V : Id> Iterable<V>.id(id: String) = first { it.id in id }

/**
 * 根据类型分类
 * @see Item.type
 */
fun <T, V : Type<T>> Iterable<V>.types() = groupBy { it.type }

/**
 * 根据类型过滤
 * @see Item.type
 */
fun <T, V : Type<T>> Iterable<V>.types(vararg types: T) = filter { it.type in types }.groupBy { it.type }

/**
 * 根据名称过滤
 */
fun <V : NameI18n> Iterable<V>.name(name: String) = first { name in it.i18n.values }

/**
 * 根据名字查找掉落
 * @see Item.i18n
 * @see Item.alias
 */
fun Iterable<Item>.name(name: String) = first { name in it.i18n.values || it.alias.any { (_, names) -> name in names } }

/**
 * 根据稀有度分类掉落 rarity in 0..4
 * @see Item.rarity
 */
fun Iterable<Item>.rarities() = groupBy { it.rarity }

/**
 * 根据稀有度过滤掉落 rarity in 0..4
 * @see Item.rarity
 */
fun Iterable<Item>.rarities(vararg rarities: Int) = filter { it.rarity in rarities }.groupBy { it.rarity }

/**
 * 根据COST消耗分类关卡
 * @see Stage.cost
 */
fun Iterable<Stage>.cost() = groupBy { it.cost }

/**
 * 根据COST消耗过滤关卡
 * @see Stage.cost
 */
fun Iterable<Stage>.cost(costs: IntRange) = filter { it.cost in costs }.groupBy { it.cost }

/**
 * 根据掉落物过滤
 * @see Drop.drops
 */
fun <V : Drop> Iterable<V>.drop(item: Item) = filter { it.drops.any { info -> info.itemId == item.id } }

/**
 * 根据掉落物过滤
 * @see drop
 * @see name
 */
fun <V : Drop> Pair<Iterable<V>, Iterable<Item>>.drop(name: String) = first.drop(second.name(name))

/**
 * 根据区域过滤
 * @see ZoneId.zoneId
 */
fun <V : ZoneId> Iterable<V>.zone(zone: Zone) = filter { it.zoneId == zone.id }

/**
 * 根据 isGacha 过滤关卡
 * @see Stage.isGacha
 */
fun Iterable<Stage>.gacha(value: Boolean) = filter { it.isGacha == value }

/**
 * 根据名字过滤区域
 * @see Zone.i18n
 */
fun <V : ZoneId> Pair<Iterable<Zone>, Iterable<V>>.name(name: String) = first.name(name).let { it to second.zone(it) }

/**
 * 根据名字过滤区域
 * @see Zone.i18n
 */
fun <V : ZoneId> Iterable<V>.with(zones: Iterable<Zone>) = map { it to zones.id(it.zoneId) }

/**
 * 根据物品ID过滤掉落记录
 * @see Matrix.itemId
 */
fun <V : ItemId> Iterable<V>.item(item: Item) = filter { it.itemId == item.id }

/**
 * 根据物品名过滤掉落记录
 * @see item
 * @see name
 */
fun <V : ItemId> Pair<Iterable<Item>, Iterable<V>>.item(name: String) = first.name(name).let { it to second.item(it) }

/**
 * XXX
 */
@JvmName("withItem")
infix fun <V : ItemId> Iterable<V>.with(items: Iterable<Item>) = map { it to items.id(it.itemId) }

/**
 * XXX
 */
val Pair<Frequency, Item>.rarity get() = first.probability * second.rarity

/**
 * 根据关卡过滤掉落记录
 * @see Matrix.stageId
 */
fun <V : StageId> Iterable<V>.stage(stage: Stage) = filter { it.stageId == stage.id }

/**
 * 根据物品名过滤掉落记录
 * @see stage
 * @see code
 */
fun <V : StageId> Pair<Iterable<Stage>, Iterable<V>>.stage(code: String) = first.name(code).let { it to second.stage(it) }

/**
 * XXX
 */
@JvmName("withStage")
infix fun <V : StageId> Iterable<V>.with(stages: Iterable<Stage>) = map { it to stages.id(it.stageId) }

/**
 * XXX
 */
val Pair<Frequency, Stage>.stage get() = second

/**
 * XXX
 */
val Pair<Frequency, Stage>.single get() = stage.cost / first.probability


/**
 * XXX
 */
val Pair<Frequency, Stage>.short get() = (stage.minClearTime / first.probability).toLong()

/**
 * 根据时间戳过滤
 * @see TimePeriod
 */
fun <V : TimePeriod> Iterable<V>.time(time: OffsetDateTime) = filter { time in it.start..it.end }

/**
 * 根据当前时间戳过滤
 * @see TimePeriod
 */
fun <V : TimePeriod> Iterable<V>.now() = time(OffsetDateTime.now())

typealias I18n<T> = Map<String, T>

object OffsetDataTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(decoder.decodeLong()), SERVER_ZONE)
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toEpochSecond() * 1_000)
    }

}

interface Existences {
    val existence: Server<Existence>
}

interface NameI18n {
    val i18n: I18n<String>
}

infix fun <T> I18n<T>.get(server: ServerType) = get(server.locale.language)

fun <T> I18n<T>.get() = get(SERVER.locale.language)

interface Id {
    val id: String
}

interface Type<T : Enum<T>> {
    val type: T
}

interface Quantity {
    val quantity: Long
}

interface Times {
    val times: Long
}

interface Frequency : Quantity, Times {
    val probability get() = (quantity.toDouble() / times)
}

interface TimePeriod {
    val start: OffsetDateTime
    val end: OffsetDateTime
}

interface ItemId {
    val itemId: String
}

interface StageId {
    val stageId: String
}

interface ZoneId {
    val zoneId: String
}

interface Drop {
    val drops: List<ItemId>
}

@Serializable
data class Existence(
    @SerialName("exist")
    val exist: Boolean,
    @SerialName("openTime")
    @Serializable(OffsetDataTimeSerializer::class)
    override val start: OffsetDateTime = OffsetDateTime.MIN,
    @SerialName("closeTime")
    @Serializable(OffsetDataTimeSerializer::class)
    override val end: OffsetDateTime = OffsetDateTime.MAX
): TimePeriod

@Serializable
data class Item(
    @SerialName("addTimePoint")
    val addTimePoint: Int? = null,
    @SerialName("alias")
    val alias: I18n<List<String>>,
    @SerialName("existence")
    override val existence: Server<Existence>,
    @SerialName("groupID")
    val groupId: String? = null,
    @SerialName("itemId")
    override val id: String,
    @SerialName("itemType")
    override val type: ItemType,
    @SerialName("name")
    val name: String,
    @SerialName("name_i18n")
    override val i18n: I18n<String>,
    @SerialName("pron")
    val pron: I18n<List<String>>,
    @SerialName("rarity")
    val rarity: Int,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("spriteCoord")
    val sprites: List<Int> = emptyList()
) : Existences, NameI18n, Id, Type<ItemType>

enum class ItemType {
    CARD_EXP,
    MATERIAL,
    FURN,
    ACTIVITY_ITEM,
    TEMP,
    LGG_SHD,
    ARKPLANNER,
    CHIP
}

@Serializable
data class Stage(
    @SerialName("apCost")
    val cost: Int,
    @SerialName("code")
    val code: String,
    @SerialName("code_i18n")
    override val i18n: I18n<String>,
    @SerialName("dropInfos")
    override val drops: List<DropInfo> = emptyList(),
    @SerialName("existence")
    override val existence: Server<Existence>,
    /**
     * 单位毫秒
     */
    @SerialName("minClearTime")
    val minClearTime: Long = 0,
    @SerialName("stageId")
    override val id: String,
    @SerialName("stageType")
    override val type: StageType,
    @SerialName("zoneId")
    override val zoneId: String,
    @SerialName("isGacha")
    val isGacha: Boolean = false,
    @SerialName("recognitionOnly")
    val recognitionOnly: List<String> = emptyList()
) : Existences, NameI18n, Id, Type<StageType>, Drop, ZoneId

enum class StageType {
    MAIN,
    SUB,
    ACTIVITY,
    DAILY
}

@Serializable
data class DropInfo(
    @SerialName("bounds")
    val bounds: Bounds,
    @SerialName("dropType")
    val type: DropType,
    @SerialName("itemId")
    override val itemId: String = ""
) : ItemId

enum class DropType {
    NORMAL_DROP,
    EXTRA_DROP,
    FURNITURE,
    SPECIAL_DROP
}

@Serializable
data class Bounds(
    @SerialName("lower")
    val lower: Int,
    @SerialName("upper")
    val upper: Int,
    @SerialName("exceptions")
    val exceptions: List<Int> = emptyList()
)

@Serializable
data class Zone(
    @SerialName("background")
    val background: String? = null,
    @SerialName("existence")
    override val existence: Server<Existence>,
    @SerialName("stages")
    val stages: List<String>,
    @SerialName("type")
    override val type: ZoneType,
    @SerialName("zoneId")
    override val id: String,
    @SerialName("zoneIndex")
    val index: Int,
    @SerialName("zoneName")
    val name: String,
    @SerialName("zoneName_i18n")
    override val i18n: I18n<String>
) : Existences, NameI18n, Id, Type<ZoneType>

enum class ZoneType {
    MAINLINE,
    ACTIVITY,
    WEEKLY
}

@Serializable
data class Period(
    @SerialName("existence")
    override val existence: Server<Existence>,
    @SerialName("label_i18n")
    override val i18n: I18n<String>,
    @SerialName("start")
    @Serializable(OffsetDataTimeSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("end")
    @Serializable(OffsetDataTimeSerializer::class)
    override val end: OffsetDateTime = OffsetDateTime.now()
) : Existences, NameI18n, TimePeriod

@Serializable
data class ServerStats(
    @SerialName("totalApCost")
    val totalCost: Long,
    @SerialName("totalItemQuantities")
    val totalItemQuantities: List<ItemQuantity>,
    @SerialName("totalStageTimes")
    val totalStageTimes: List<StageTimes>,
    @SerialName("totalStageTimes_24h")
    val totalStageTimes24h: List<StageTimes>
)

@Serializable
data class ItemQuantity(
    @SerialName("itemId")
    override val itemId: String,
    @SerialName("quantity")
    override val quantity: Long
) : Quantity, ItemId

@Serializable
data class StageTimes(
    @SerialName("stageId")
    override val stageId: String,
    @SerialName("times")
    override val times: Long
) : Times, StageId

@Serializable
data class MatrixData(
    @SerialName("matrix")
    val matrices: List<Matrix>
)

@Serializable
data class Matrix(
    @SerialName("end")
    @Serializable(OffsetDataTimeSerializer::class)
    override val end: OffsetDateTime = OffsetDateTime.MAX,
    @SerialName("itemId")
    override val itemId: String,
    @SerialName("quantity")
    override val quantity: Long,
    @SerialName("stageId")
    override val stageId: String,
    @SerialName("start")
    @Serializable(OffsetDataTimeSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("times")
    override val times: Long
) : Frequency, TimePeriod, ItemId, StageId

@Serializable
data class PatternData(
    @SerialName("pattern_matrix")
    val patterns: List<PatternMatrix>
)

@Serializable
data class PatternMatrix(
    @SerialName("end")
    @Serializable(OffsetDataTimeSerializer::class)
    override val end: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("pattern")
    val pattern: Pattern,
    @SerialName("quantity")
    override val quantity: Long,
    @SerialName("stageId")
    override val stageId: String,
    @SerialName("start")
    @Serializable(OffsetDataTimeSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("times")
    override val times: Long
) : Frequency, TimePeriod, StageId

@Serializable
data class Pattern(
    @SerialName("drops")
    override val drops: List<ItemQuantity>
) : Drop