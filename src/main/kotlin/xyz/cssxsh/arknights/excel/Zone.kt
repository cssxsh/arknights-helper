package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime

fun File.readZoneTable(): ZoneTable = read(type = ExcelDataType.ZONE)

typealias ZoneMap = Map<ZoneType, List<Zone>>

fun ZoneMap(table: ZoneTable): ZoneMap = table.zones.values.groupBy { it.type }

typealias WeeklyMap = Map<WeeklyType, List<Pair<Zone, Weekly>>>

fun WeeklyMap(table: ZoneTable): WeeklyMap {
    return table.weekly.entries.groupBy { it.value.type }.mapValues { (_, list) ->
        list.map { (id, weekly) -> table.zones.getValue(id) to weekly }
    }
}

val Zone.title get() = nameFirst?.let { "$it " }.orEmpty() + nameSecond.orEmpty() + nameThird?.let { " $it" }.orEmpty()

@Serializable
data class ZoneTable(
    @SerialName("mainlineAdditionInfo")
    val mainline: Map<String, Mainline>,
    @SerialName("weeklyAdditionInfo")
    val weekly: Map<String, Weekly>,
    @SerialName("zoneValidInfo")
    val valid: Map<String, ValidInfo>,
    @SerialName("zones")
    val zones: Map<String, Zone>
)

@Serializable
data class Mainline(
    @SerialName("chapterId")
    val chapterId: String,
    @SerialName("endStageId")
    val end: String,
    @SerialName("mainlneBgName")
    val mainlineBgName: String,
    @SerialName("preposedZoneId")
    val posed: String?,
    @SerialName("startStageId")
    val start: String,
    @SerialName("zoneId")
    override val zoneId: String,
    @SerialName("zoneIndex")
    val index: Int
): ZoneId

@Serializable
data class Weekly(
    @SerialName("daysOfWeek")
    val daysOfWeek: List<Int>,
    @SerialName("type")
    val type: WeeklyType
)

enum class WeeklyType(val text: String) {
    EVOLVE("进化"),
    MATERIAL("材料"),
    SPECIAL("特殊")
}

@Serializable
data class ValidInfo(
    @SerialName("endTs")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime,
    @SerialName("startTs")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime
): Period

@Serializable
data class Zone(
    @SerialName("canPreview")
    val preview: Boolean,
    @SerialName("lockedText")
    val locked: String?,
    @SerialName("type")
    val type: ZoneType,
    @SerialName("zoneID")
    override val id: String,
    @SerialName("zoneIndex")
    val index: Int,
    @SerialName("zoneNameFirst")
    val nameFirst: String?,
    @SerialName("zoneNameSecond")
    val nameSecond: String?,
    @SerialName("zoneNameThird")
    val nameThird: String?,
    @SerialName("zoneNameTitleCurrent")
    val nameTitleCurrent: String?,
    @SerialName("zoneNameTitleEx")
    val nameTitleEx: String?,
    @SerialName("zoneNameTitleUnCurrent")
    val nameTitleUnCurrent: String?
): Id

enum class ZoneType(val text: String) {
    MAINLINE("主线"),
    GUIDE("指导"),
    WEEKLY("周常"),
    CAMPAIGN("战役"),
    ACTIVITY("活动"),
    BRANCHLINE("支线"),
    SIDESTORY("趣闻")
}