package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime

const val GACHA = "gacha_table.json"

fun File.gacha(): GachaTable = read(name = GACHA, type = GameDataType.EXCEL)

/**
 * 获取公招干员
 */
fun GachaTable.recruit(): Set<String> {
    return recruitDetail.removeSign().lines().flatMap { line ->
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

fun Collection<RecruitResult>.sum(): RecruitResult = buildMap {
    this@sum.forEach {
        it.forEach { (t, u) ->
            this[t] = this[t].orEmpty() + u
        }
    }
}

/**
 * XXX
 */
fun CharacterTable.recruit(words: Set<String>, recruit: Set<String> = name()): RecruitMap {
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
 * 抽卡
 */
fun gacha(pool: List<Pair<Set<Character>, Double>>): Character {
    val temp = (1..CAPACITY).toMutableList()
    val balls = pool.map { (set, prob) ->
        set to (1..(prob * CAPACITY).toInt()).map { temp.random().also { temp.remove(it) } }
    }
    val random = (1..CAPACITY).random()
    return balls.first {  (_, ball) -> random in ball }.first.random()
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
    @SerialName("newbeeGachaPoolClient")
    val newbeeGachaPoolClient: List<JsonObject>,
    @SerialName("recruitPool")
    private val recruitPool: JsonObject,
    @SerialName("specialRecruitPool")
    private val specialRecruitPool: List<JsonObject>,
    @SerialName("potentialMaterialConverter")
    private val potentialMaterialConverter: JsonObject,
    @SerialName("potentialMats")
    private val potentialMats: JsonObject,
    @SerialName("recruitRarityTable")
    private val recruitRarityTable: JsonObject,
    @SerialName("specialTagRarityTable")
    private val specialTagRarityTable: Map<Int, List<Int>>,
    @SerialName("recruitDetail")
    val recruitDetail: String,
    @SerialName("carousel")
    private val carousel: List<JsonObject>,
    @SerialName("freeGacha")
    private val freeGacha: List<JsonObject>,
    @SerialName("limitTenGachaItem")
    private val limitTenGachaItem: List<JsonObject>,
    @SerialName("linkageTenGachaItem")
    private val linkageTenGachaItem: List<JsonObject>,
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
    val end: OffsetDateTime,
    @SerialName("gachaPoolId")
    val id: String,
    @SerialName("gachaIndex")
    val index: Int,
    @SerialName("gachaPoolName")
    val name: String,
    @SerialName("openTime")
    @Serializable(TimestampSerializer::class)
    val open: OffsetDateTime,
    @SerialName("gachaRuleType")
    val rule: GachaPoolRule,
    @SerialName("gachaPoolSummary")
    val summary: String,
    @SerialName("linkageRuleId")
    private val linkageRule: String? = null,
    @SerialName("linkageParam")
    private val linkageParam: JsonObject? = null,
    @SerialName("guarantee5Avail")
    private val guarantee5Avail: Int,
    @SerialName("guarantee5Count")
    private val guarantee5Count: Int,
    @SerialName("CDPrimColor")
    private val CDPrimColor: String?,
    @SerialName("CDSecColor")
    private val CDSecColor: String?,
    @SerialName("LMTGSID")
    private val LMTGSID: String?
)

enum class GachaPoolRule {
    /**
     * 正常
     */
    NORMAL,

    /**
     * 限定
     */
    LIMITED,

    /**
     * 联动
     */
    LINKAGE
}
