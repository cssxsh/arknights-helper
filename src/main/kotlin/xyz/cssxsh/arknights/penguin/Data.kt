package xyz.cssxsh.arknights.penguin

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import xyz.cssxsh.arknights.*
import java.time.*

/**
 * 根据名字查找
 * @see Id.id
 */
public fun <V : Id> Iterable<V>.id(id: String): V = first { it.id in id }

/**
 * 根据类型分类
 * @see Item.type
 */
public fun <T, V : Type<T>> Iterable<V>.types(): Map<T, List<V>> = groupBy { it.type }

/**
 * 根据类型过滤
 * @see Item.type
 */
public fun <T, V : Type<T>> Iterable<V>.types(vararg types: T): Map<T, List<V>> = filter { it.type in types }.groupBy { it.type }

/**
 * 根据名称过滤
 */
public fun <V : NameI18n> Iterable<V>.name(name: String): V = first { name in it.i18n.values }

/**
 * 根据名字查找掉落
 * @see Item.i18n
 * @see Item.alias
 */
public fun Iterable<Item>.name(name: String): Item = first { name in it.i18n.values || it.alias.any { (_, names) -> name in names } }

/**
 * 根据稀有度分类掉落
 * @see Item.rarity
 */
public fun Iterable<Item>.rarities(): Map<Int, List<Item>> = groupBy { it.rarity }

/**
 * 根据稀有度过滤掉落 rarity in 0..4
 * @see Item.rarity
 */
public fun Iterable<Item>.rarities(vararg rarities: Int): Map<Int, List<Item>> = filter { it.rarity in rarities }.groupBy { it.rarity }

/**
 * 根据COST消耗分类关卡
 * @see Stage.cost
 */
public fun Iterable<Stage>.cost(): Map<Int, List<Stage>> = groupBy { it.cost }

/**
 * 根据COST消耗过滤关卡
 * @see Stage.cost
 */
public fun Iterable<Stage>.cost(costs: IntRange): Map<Int, List<Stage>> = filter { it.cost in costs }.groupBy { it.cost }

/**
 * 根据掉落物过滤
 * @see Drop.drops
 */
public fun <V : Drop> Iterable<V>.drop(item: Item): List<V> = filter { it.drops.any { info -> info.itemId == item.id } }

/**
 * 根据掉落物过滤
 * @see drop
 * @see name
 */
public fun <V : Drop> Pair<Iterable<V>, Iterable<Item>>.drop(name: String): List<V> = first.drop(second.name(name))

/**
 * 根据区域过滤
 * @see ZoneId.zoneId
 */
public fun <V : ZoneId> Iterable<V>.zone(zone: Zone): List<V> = filter { it.zoneId == zone.id }

/**
 * 根据 isGacha 过滤关卡
 * @see Stage.isGacha
 */
public fun Iterable<Stage>.gacha(value: Boolean): List<Stage> = filter { it.isGacha == value }

/**
 * 根据名字过滤区域
 * @see Zone.i18n
 */
public fun <V : ZoneId> Pair<Iterable<Zone>, Iterable<V>>.name(name: String): Pair<Zone, List<V>> = first.name(name).let { it to second.zone(it) }

/**
 * 根据名字过滤区域
 * @see Zone.i18n
 */
public fun <V : ZoneId> Iterable<V>.with(zones: Iterable<Zone>): List<Pair<V, Zone>> = map { it to zones.id(it.zoneId) }

/**
 * 根据物品ID过滤掉落记录
 * @see Matrix.itemId
 */
public fun <V : ItemId> Iterable<V>.item(item: Item): List<V> = filter { it.itemId == item.id }

/**
 * 根据物品名过滤掉落记录
 * @see item
 * @see name
 */
public fun <V : ItemId> Pair<Iterable<Item>, Iterable<V>>.item(name: String): Pair<Item, List<V>> = first.name(name).let { it to second.item(it) }

/**
 * XXX
 */
@JvmName("withItem")
public infix fun <V : ItemId> Iterable<V>.with(items: Iterable<Item>): List<Pair<V, Item>> = map { it to items.id(it.itemId) }

/**
 * XXX
 */
public val Pair<Frequency, Item>.rarity: Double get() = first.probability * second.rarity

/**
 * 根据关卡过滤掉落记录
 * @see Matrix.stageId
 */
public fun <V : StageId> Iterable<V>.stage(stage: Stage): List<V> = filter { it.stageId == stage.id }

/**
 * 根据物品名过滤掉落记录
 * @see stage
 * @see code
 */
public fun <V : StageId> Pair<Iterable<Stage>, Iterable<V>>.stage(code: String): Pair<Stage, List<V>> =
    first.name(code).let { it to second.stage(it) }

/**
 * XXX
 */
@JvmName("withStage")
public infix fun <V : StageId> Iterable<V>.with(stages: Iterable<Stage>): List<Pair<V, Stage>> = map { it to stages.id(it.stageId) }

/**
 * XXX
 */
public val Pair<*, Stage>.stage: Stage get() = second

/**
 * XXX
 */
public val Pair<Frequency, *>.frequency: Frequency get() = first

/**
 * XXX
 */
public val Pair<Frequency, Stage>.single: Double get() = stage.cost / frequency.probability


/**
 * XXX
 */
public val Pair<Frequency, Stage>.short: Long get() = (stage.minClearTime / frequency.probability).toLong()

/**
 * 根据时间戳过滤
 * @see TimePeriod
 */
public fun <V : TimePeriod> Iterable<V>.time(time: OffsetDateTime): List<V> = filter { time in it.start..it.end }

/**
 * 根据当前时间戳过滤
 * @see TimePeriod
 */
public fun <V : TimePeriod> Iterable<V>.now(): List<V> = time(OffsetDateTime.now())

public typealias I18n<T> = Map<String, T>

internal object OffsetDataTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(decoder.decodeLong()), java.time.ZoneId.systemDefault())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeLong(value.toInstant().toEpochMilli())
    }

}

public interface Existences {
    public val existence: Server<Existence>
}

public interface NameI18n {
    public val i18n: I18n<String>
}

public infix fun <T> I18n<T>.get(server: ServerType): T? = get(server.locale.language)

public fun <T> I18n<T>.get(): T? = get(SERVER.locale.language)

public interface Id {
    public val id: String
}

public interface Type<T : Enum<T>> {
    public val type: T
}

public interface Quantity {
    public val quantity: Long
}

public interface Times {
    public val times: Long
}

public interface Frequency : Quantity, Times {
    public val probability: Double get() = (quantity.toDouble() / times)
}

public interface TimePeriod {
    public val start: OffsetDateTime
    public val end: OffsetDateTime
}

public interface ItemId {
    public val itemId: String
}

public interface StageId {
    public val stageId: String
}

public interface ZoneId {
    public val zoneId: String
}

public interface Drop {
    public val drops: List<ItemId>
}

@Serializable
public data class Existence(
    @SerialName("exist")
    val exist: Boolean,
    @SerialName("openTime")
    @Serializable(OffsetDataTimeSerializer::class)
    override val start: OffsetDateTime = OffsetDateTime.MIN,
    @SerialName("closeTime")
    @Serializable(OffsetDataTimeSerializer::class)
    override val end: OffsetDateTime = OffsetDateTime.MAX
) : TimePeriod

@Serializable
public data class Item(
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

public enum class ItemType {
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
public data class Stage(
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

public enum class StageType {
    MAIN,
    SUB,
    ACTIVITY,
    DAILY
}

@Serializable
public data class DropInfo(
    @SerialName("bounds")
    val bounds: Bounds,
    @SerialName("dropType")
    val type: DropType,
    @SerialName("itemId")
    override val itemId: String = ""
) : ItemId

public enum class DropType {
    NORMAL_DROP,
    EXTRA_DROP,
    FURNITURE,
    SPECIAL_DROP
}

@Serializable
public data class Bounds(
    @SerialName("lower")
    val lower: Int,
    @SerialName("upper")
    val upper: Int,
    @SerialName("exceptions")
    val exceptions: List<Int> = emptyList()
)

@Serializable
public data class Zone(
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

public enum class ZoneType {
    MAINLINE,
    ACTIVITY,
    WEEKLY
}

@Serializable
public data class Period(
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
public data class ServerStats(
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
public data class ItemQuantity(
    @SerialName("itemId")
    override val itemId: String,
    @SerialName("quantity")
    override val quantity: Long
) : Quantity, ItemId

@Serializable
public data class StageTimes(
    @SerialName("stageId")
    override val stageId: String,
    @SerialName("times")
    override val times: Long
) : Times, StageId

@Serializable
public data class MatrixData(
    @SerialName("matrix")
    val matrices: List<Matrix>
)

@Serializable
public data class Matrix(
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
public data class PatternData(
    @SerialName("pattern_matrix")
    val patterns: List<PatternMatrix>
)

@Serializable
public data class PatternMatrix(
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
public data class Pattern(
    @SerialName("drops")
    override val drops: List<ItemQuantity>
) : Drop