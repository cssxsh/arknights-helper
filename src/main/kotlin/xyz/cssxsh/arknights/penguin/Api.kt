package xyz.cssxsh.arknights.penguin

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public enum class PenguinDataType(override val url: String) : CacheKey {
    // BASE
    ITEMS("https://penguin-stats.cn/PenguinStats/api/v2/items"),
    STAGES("https://penguin-stats.cn/PenguinStats/api/v2/stages"),
    ZONES("https://penguin-stats.cn/PenguinStats/api/v2/zones"),
    PERIOD("https://penguin-stats.cn/PenguinStats/api/v2/period"),
    STATS("https://penguin-stats.cn/PenguinStats/api/v2/stats"),
    // STATS
    RESULT_MATRIX("https://penguin-stats.cn/PenguinStats/api/v2/_private/result/matrix/CN/global"),
    RESULT_PATTERN("https://penguin-stats.cn/PenguinStats/api/v2/_private/result/pattern/CN/global");

    override val filename: String = "${name}.json"
}

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

@Serializable
public enum class ItemType {
    CARD_EXP,
    MATERIAL,
    FURN,
    ACTIVITY_ITEM,
    TEMP,
    LGG_SHD,
    ARKPLANNER,
    CHIP,
    RECRUIT_TAG
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

@Serializable
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

@Serializable
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

@Serializable
public enum class ZoneType {
    MAINLINE,
    ACTIVITY,
    ACTIVITY_PERMANENT,
    WEEKLY,
    GACHABOX,
    RECRUIT
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