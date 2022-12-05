package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class ZoneTable(
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
public data class Mainline(
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
) : ZoneId

@Serializable
public data class Weekly(
    @SerialName("daysOfWeek")
    val daysOfWeek: List<Int>,
    @SerialName("type")
    val type: WeeklyType
)

@Serializable
public enum class WeeklyType(public val text: String) {
    EVOLVE("进化"),
    MATERIAL("材料"),
    SPECIAL("特殊")
}

@Serializable
public data class ValidInfo(
    @SerialName("endTs")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime,
    @SerialName("startTs")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime
) : Period

@Serializable
public data class Zone(
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
) : Id {
    public val title: String = "${nameFirst.orEmpty()} ${nameSecond.orEmpty()} ${nameThird.orEmpty()}"
}

@Serializable
public enum class ZoneType(public val text: String) {
    MAINLINE("主线"),
    GUIDE("指导"),
    WEEKLY("周常"),
    CAMPAIGN("战役"),
    ACTIVITY("活动"),
    BRANCHLINE("支线"),
    SIDESTORY("趣闻"),
    ROGUELIKE("肉鸽"),
    CLIMB_TOWER("爬塔")
}

public class WeeklyClock(public val zone: Zone, public val weekly: Weekly) : CacheInfo {
    override val created: OffsetDateTime = OffsetDateTime.now().withHour(4)
    override val url: String = ""
}