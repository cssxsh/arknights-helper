package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

/**
 * 过滤当前卡池
 */
public fun Collection<GachaPool>.open(): List<GachaPool> = filter { it.end > OffsetDateTime.now() }

public typealias PoolData = List<Pair<Set<Character>, Double>>

private const val CAPACITY = 1000

/**
 * 检查概率和
 */
private fun one(prob: Collection<Double>) {
    check(prob.sumOf { (it * CAPACITY).toInt() } in (CAPACITY - 3..CAPACITY + 3)) { "${prob}概率和不满足100%" }
}

/**
 * 抽卡
 */
public fun gacha(pool: PoolData): Character {
    one(pool.map { it.second })
    val temp = (1..CAPACITY).toMutableList()
    val balls = pool.map { (set, prob) ->
        set to (1..(prob * CAPACITY).toInt()).map { temp.random().also { temp.remove(it) } }
    }
    val random = ((1..CAPACITY) - temp).random()
    return balls.first { (_, ball) -> random in ball }.first.random()
}

public val BUILD_POOL_LINE: Regex = """\s*([^|]+(\s*[|]\s*[^|]+)*)\s*:\s*(0\.\d+)\s*""".toRegex()

/**
 *  使用 几个 * 值表示几星干员，
 *  多干员使用 | 隔开，
 *  干员和概率用: 隔开，
 *  规则用 换行符 或 ; 隔开
 *  注释#开头
 *  @see BUILD_POOL_LINE
 */
public fun Collection<Character>.pool(rule: String): PoolData = pool(rule.split("\r\n", "\n", "\r", ";"))

/**
 *  使用 几个 * 值表示几星干员，
 *  多干员使用 | 隔开，
 *  干员和概率用: 隔开
 *  注释#开头
 *  @see BUILD_POOL_LINE
 */
public fun Collection<Character>.pool(rule: Collection<String>): PoolData {
    check(rule.all { it.matches(BUILD_POOL_LINE) || it.startsWith("#") }) { "rule: $rule" }
    val map = rule.filter { it.matches(BUILD_POOL_LINE) }.associate { line ->
        line.split(':').let { (a, b) ->
            a.trim() to b.trim().toDouble()
        }
    }
    one(map.values)
    val other = map.entries.find { it.key == "other" }?.value
    val set = mutableSetOf<Character>()
    return (map - "other").entries.sortedByDescending { it.key }.map { (key, prob) ->
        if (key.contains('*')) {
            rarities(key.count { it == '*' } - 1) - set
        } else {
            names(key.split('|').map { it.trim() }).also {
                set.addAll(it)
            }
        } to prob
    }.let { list ->
        if (other == null) {
            list
        } else {
            list + ((this - list.flatMap { it.first }).toSet() to other)
        }
    }
}

@Serializable
public data class GachaTable(
    /**
     * 公招TAG
     */
    @SerialName("gachaTags")
    val tags: List<GachaTagInfo>,
    @SerialName("gachaTagMaxValid")
    val tagMaxValid: Int,
    @SerialName("gachaPoolClient")
    val pools: List<GachaPool>,
    @SerialName("newbeeGachaPoolClient")
    internal val newbeeGachaPoolClient: List<JsonObject>,
    @SerialName("recruitPool")
    internal val recruitPool: JsonObject,
    @SerialName("specialRecruitPool")
    internal val specialRecruitPool: List<JsonObject>,
    @SerialName("potentialMaterialConverter")
    internal val potentialMaterialConverter: JsonObject,
    @SerialName("potentialMats")
    internal val potentialMats: JsonObject,
    @SerialName("classicPotentialMaterialConverter")
    internal val classicPotentialMaterialConverter: JsonObject,
    @SerialName("classicPotentialMats")
    internal val classicPotentialMats: JsonObject,
    @SerialName("recruitRarityTable")
    internal val recruitRarityTable: JsonObject,
    @SerialName("specialTagRarityTable")
    internal val specialTagRarityTable: Map<Int, List<Int>>,
    @SerialName("recruitDetail")
    val recruitDetail: String,
    @SerialName("carousel")
    internal val carousel: List<JsonObject>,
    @SerialName("freeGacha")
    internal val freeGacha: List<JsonObject>,
    @SerialName("limitTenGachaItem")
    internal val limitTenGachaItem: List<JsonObject>,
    @SerialName("linkageTenGachaItem")
    internal val linkageTenGachaItem: List<JsonObject>,
    @SerialName("fesGachaPoolRelateItem")
    internal val fesGachaPoolRelateItem: JsonObject
)

@Serializable
public data class GachaTagInfo(
    @SerialName("tagId")
    val id: Int,
    @SerialName("tagName")
    val name: String,
    @SerialName("tagGroup")
    val group: Int
)

@Serializable
public data class GachaPool(
    @SerialName("gachaPoolDetail")
    val detail: String?,
    @SerialName("endTime")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime,
    @SerialName("gachaPoolId")
    override val id: String,
    @SerialName("gachaIndex")
    val index: Int,
    @SerialName("gachaPoolName")
    override val name: String,
    @SerialName("openTime")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("gachaRuleType")
    val rule: GachaPoolRule = GachaPoolRule.NORMAL,
    @SerialName("gachaPoolSummary")
    val summary: String,
    @SerialName("linkageRuleId")
    internal val linkageRuleId: String? = null,
    @SerialName("linkageParam")
    internal val linkageParam: JsonObject? = null,
    @SerialName("guarantee5Avail")
    internal val guarantee5Avail: Int,
    @SerialName("guarantee5Count")
    internal val guarantee5Count: Int,
    @SerialName("CDPrimColor")
    internal val cdPrimColor: String?,
    @SerialName("CDSecColor")
    internal val cdSecColor: String?,
    @SerialName("storeTextColor")
    internal val storeTextColor: String? = null,
    @SerialName("LMTGSID")
    internal val lmtgsId: String?,
    @SerialName("dynMeta")
    internal val meta: JsonElement = JsonNull
) : Id, Name, Period

public enum class GachaPoolRule(vararg lines: String) {
    /**
     * 正常
     */
    NORMAL("******:0.02", "*****:0.08", "****:0.48", "***:0.42"),

    /**
     * 限定
     */
    LIMITED,

    /**
     * 联动
     */
    LINKAGE,

    /**
     *
     */
    ATTAIN;

    public val rule: String = "#${name};" + lines.joinToString(";")
}
