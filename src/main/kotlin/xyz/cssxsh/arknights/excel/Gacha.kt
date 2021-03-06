package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

/**
 * 获取公招干员
 */
fun GachaTable.recruit(): Set<String> {
    return recruitDetail.remove(SIGN).lines().flatMap { line ->
        if (line.startsWith("★")) {
            line.substringAfterLast("""\n""").split("/").map { it.trim() }
        } else {
            emptyList()
        }
    }.toSet()
}

/**
 * 获取公招TAG
 */
fun GachaTable.tags() = tags.map { it.name }.toSet()

typealias RecruitResult = Map<Int, List<Character>>

typealias RecruitMap = Map<Set<String>, RecruitResult>

fun Collection<Character>.toRecruitResult() = groupBy { it.rarity }.toSortedMap()

/**
 * 列出公招结果
 */
fun CharacterMap.recruit(words: Set<String>, recruit: Set<String> = name()): RecruitMap {
    check(words.size in 1..5) { "词条数量不对" }
    val site = minOf(3, words.size)
    val obtain = values.names(recruit)
    return (0..site).fold(setOf<Set<String>>(emptySet())) { set, _ ->
        words.flatMap { a -> set.map { b -> b + a } }.toSet()
    }.sortedBy { it.size }.associateWith {
        it.fold(obtain) { s, word -> s.filter(word) }
    }.mapValues { (_, characters) ->
        characters.groupBy { it.rarity }
    }.filter { (_, characters) ->
        characters.isNotEmpty()
    }
}

/**
 * 过滤当前卡池
 */
fun Collection<GachaPool>.open() = filter { it.end > OffsetDateTime.now() }

typealias PoolData = List<Pair<Set<Character>, Double>>

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
fun gacha(pool: PoolData): Character {
    one(pool.map { it.second })
    val temp = (1..CAPACITY).toMutableList()
    val balls = pool.map { (set, prob) ->
        set to (1..(prob * CAPACITY).toInt()).map { temp.random().also { temp.remove(it) } }
    }
    val random = ((1..CAPACITY) - temp).random()
    return balls.first { (_, ball) -> random in ball }.first.random()
}

val BUILD_POOL_LINE = """\s*([^|]+(\s*[|]\s*[^|]+)*)\s*:\s*(0\.\d+)\s*""".toRegex()

/**
 *  使用 几个 * 值表示几星干员，
 *  多干员使用 | 隔开，
 *  干员和概率用: 隔开，
 *  规则用 换行符 或 ; 隔开
 *  注释#开头
 *  @see BUILD_POOL_LINE
 */
fun Collection<Character>.pool(rule: String): PoolData = pool(rule.split("\r\n", "\n", "\r", ";"))

/**
 *  使用 几个 * 值表示几星干员，
 *  多干员使用 | 隔开，
 *  干员和概率用: 隔开
 *  注释#开头
 *  @see BUILD_POOL_LINE
 */
fun Collection<Character>.pool(rule: Collection<String>): PoolData {
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
data class GachaTable(
    /**
     * 公招TAG
     */
    @SerialName("gachaTags")
    val tags: List<GachaTagInfo>,
    @SerialName("gachaTagMaxValid")
    val tagMaxValid: Int,
    @SerialName("gachaPoolClient")
    val pools: List<GachaPool>,
//    @SerialName("newbeeGachaPoolClient")
//    val newbeeGachaPoolClient: List<JsonObject>,
//    @SerialName("recruitPool")
//    private val recruitPool: JsonObject,
//    @SerialName("specialRecruitPool")
//    private val specialRecruitPool: List<JsonObject>,
//    @SerialName("potentialMaterialConverter")
//    private val potentialMaterialConverter: JsonObject,
//    @SerialName("potentialMats")
//    private val potentialMats: JsonObject,
//    @SerialName("recruitRarityTable")
//    private val recruitRarityTable: JsonObject,
//    @SerialName("specialTagRarityTable")
//    private val specialTagRarityTable: Map<Int, List<Int>>,
    @SerialName("recruitDetail")
    val recruitDetail: String,
//    @SerialName("carousel")
//    private val carousel: List<JsonObject>,
//    @SerialName("freeGacha")
//    private val freeGacha: List<JsonObject>,
//    @SerialName("limitTenGachaItem")
//    private val limitTenGachaItem: List<JsonObject>,
//    @SerialName("linkageTenGachaItem")
//    private val linkageTenGachaItem: List<JsonObject>,
)

@Serializable
data class GachaTagInfo(
    @SerialName("tagId")
    val id: Int,
    @SerialName("tagName")
    val name: String,
    @SerialName("tagGroup")
    val group: Int,
)

@Serializable
data class GachaPool(
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
    val rule: GachaPoolRule,
    @SerialName("gachaPoolSummary")
    val summary: String,
//    @SerialName("linkageRuleId")
//    private val linkageRule: String? = null,
//    @SerialName("linkageParam")
//    private val linkageParam: JsonObject? = null,
//    @SerialName("guarantee5Avail")
//    private val guarantee5Avail: Int,
//    @SerialName("guarantee5Count")
//    private val guarantee5Count: Int,
//    @SerialName("CDPrimColor")
//    private val CDPrimColor: String?,
//    @SerialName("CDSecColor")
//    private val CDSecColor: String?,
//    @SerialName("LMTGSID")
//    private val lmtgsid: String?
) : Id, Name, Period

enum class GachaPoolRule(vararg lines: String) {
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

    val rule = "#${name};" + lines.joinToString(";")
}
