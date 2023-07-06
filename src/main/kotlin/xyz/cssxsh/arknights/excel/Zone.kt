package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class ZoneTable(
    @SerialName("mainlineAdditionInfo")
    val mainline: Map<String, Mainline>,
    @SerialName("zoneValidInfo")
    val valid: Map<String, ValidInfo>,
    @SerialName("weeklyAdditionInfo")
    val weekly: Map<String, Weekly>,
    @SerialName("zones")
    val zones: Map<String, Zone>,
    @SerialName("zoneRecordGroupedData")
    internal val recordGroupedData: Map<String, RecordGroupedData>,
    @SerialName("zoneRecordRewardData")
    internal val recordRewardData: Map<String, List<String>>,
    @SerialName("zoneMetaData")
    internal val metaData: JsonObject
)

@Serializable
public data class Mainline(
    @SerialName("mainlneBgName")
    val background: String,
    @SerialName("chapterId")
    val chapterId: String,
    @SerialName("endStageId")
    val endStageId: String,
    @SerialName("preposedZoneId")
    val preposedZoneId: String?,
    @SerialName("startStageId")
    val startStageId: String,
    @SerialName("zoneId")
    override val zoneId: String,
    @SerialName("zoneIndex")
    val zoneIndex: Int,
    @SerialName("zoneOpenTime")
    @Serializable(TimestampSerializer::class)
    val zoneOpenTime: OffsetDateTime,
    @SerialName("buttonName")
    internal val buttonName: String,
    @SerialName("buttonStyle")
    internal val buttonStyle: String,
    @SerialName("diffGroup")
    internal val diffGroup: List<String>,
    @SerialName("recapId")
    internal val recapId: String,
    @SerialName("recapPreStageId")
    internal val recapPreStageId: String,
    @SerialName("spoilAlert")
    internal val spoilAlert: Boolean,
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
    public val title: String = "${nameFirst.orEmpty()} ${nameSecond.orEmpty()} ${nameThird.orEmpty()}".trim()
}

@Serializable
public data class RecordGroupedData(
    @SerialName("zoneId")
    val zoneId: String,
    @SerialName("records")
    val records: List<RecordGroupedInfo>,
    @SerialName("unlockData")
    val unlockData: UnlockData
)

@Serializable
public data class RecordGroupedInfo(
    @SerialName("recordId")
    val recordId: String,
    @SerialName("zoneId")
    val zoneId: String,
    @SerialName("recordTitleName")
    val recordTitle: String,
    @SerialName("preRecordId")
    val preRecordId: String?,
    @SerialName("nodeTitle1")
    val nodeTitle1: String?,
    @SerialName("nodeTitle2")
    val nodeTitle2: String?,
    @SerialName("rewards")
    val rewards: List<RecordRewardData>
)

@Serializable
public data class RecordRewardData(
    @SerialName("bindStageId")
    val bindStageId: String,
    @SerialName("stageDiff1")
    val stageDiff1: String,
    @SerialName("stageDiff")
    val stageDiff: String,
    @SerialName("picRes")
    val picture: String?,
    @SerialName("textPath")
    val path: String?,
    @SerialName("textDesc")
    val description: String?,
    @SerialName("recordReward")
    val detail: List<RecordRewardInfo>?
)

@Serializable
public data class RecordRewardInfo(
    @SerialName("id")
    val id: String,
    @SerialName("count")
    val count: Int,
    @SerialName("type")
    val type: String
)

@Serializable
public data class UnlockData(
    @SerialName("noteId")
    val noteId: String,
    @SerialName("zoneId")
    val zoneId: String,
    @SerialName("initialName")
    val initialName: String,
    @SerialName("finalName")
    val finalName: String?,
    @SerialName("accordingExposeId")
    val accordingExposeId: String? = null,
    @SerialName("initialDes")
    val initialDescription: String? = null,
    @SerialName("finalDes")
    val finalDescription: String? = null,
    @SerialName("remindDes")
    val remindDescription: String? = null
)

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
    override val created: OffsetDateTime = OffsetDateTime.now()
        .withHour(4).withMinute(0).withSecond(0).withNano(0)
    override val url: String = ""
}