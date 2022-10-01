package xyz.cssxsh.arknights.penguin

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
public fun <T, V : Type<T>> Iterable<V>.types(vararg types: T): Map<T, List<V>> =
    filter { it.type in types }.groupBy { it.type }

/**
 * 根据名称过滤
 */
public fun <V : NameI18n> Iterable<V>.name(name: String): V = first { name in it.i18n.values }

/**
 * 根据名字查找掉落
 * @see Item.i18n
 * @see Item.alias
 */
public fun Iterable<Item>.name(name: String): Item =
    first { name in it.i18n.values || it.alias.any { (_, names) -> name in names } }

/**
 * 根据稀有度分类掉落
 * @see Item.rarity
 */
public fun Iterable<Item>.rarities(): Map<Int, List<Item>> = groupBy { it.rarity }

/**
 * 根据稀有度过滤掉落 rarity in 0..4
 * @see Item.rarity
 */
public fun Iterable<Item>.rarities(vararg rarities: Int): Map<Int, List<Item>> =
    filter { it.rarity in rarities }.groupBy { it.rarity }

/**
 * 根据COST消耗分类关卡
 * @see Stage.cost
 */
public fun Iterable<Stage>.cost(): Map<Int, List<Stage>> = groupBy { it.cost }

/**
 * 根据COST消耗过滤关卡
 * @see Stage.cost
 */
public fun Iterable<Stage>.cost(costs: IntRange): Map<Int, List<Stage>> =
    filter { it.cost in costs }.groupBy { it.cost }

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
public fun <V : ZoneId> Iterable<V>.with(zones: Iterable<Zone>): List<Pair<V, Zone>> = map { it to zones.id(it.zoneId) }

/**
 * 根据物品ID过滤掉落记录
 * @see Matrix.itemId
 */
public fun <V : ItemId> Iterable<V>.item(item: Item): List<V> = filter { it.itemId == item.id }

/**
 * XXX
 */
@JvmName("withItem")
public infix fun <V : ItemId> Iterable<V>.with(items: Iterable<Item>): List<Pair<V, Item>> =
    map { it to items.id(it.itemId) }

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
 * XXX
 */
@JvmName("withStage")
public infix fun <V : StageId> Iterable<V>.with(stages: Iterable<Stage>): List<Pair<V, Stage>> =
    map { it to stages.id(it.stageId) }

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


public interface Existences {
    public val existence: Server<Existence>
}

public interface NameI18n {
    public val i18n: I18n<String>
}

public fun <T> I18n<T>.locale(): T? = get(SERVER.locale.language)

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
