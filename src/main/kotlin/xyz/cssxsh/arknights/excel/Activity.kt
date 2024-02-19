package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class ActivityTable(
    @SerialName("basicInfo")
    val basic: Map<String, ActivityBasicInfo>,
    @SerialName("homeActConfig")
    internal val homeActConfig: Map<String, JsonObject>,
    @SerialName("zoneToActivity")
    val zoneToActivity: Map<String, String>,
    @SerialName("missionData")
    internal val missionData: List<JsonObject>,
    @SerialName("missionGroup")
    internal val missionGroup: List<JsonObject>,
    @SerialName("replicateMissions")
    internal val replicateMissions: JsonElement = JsonNull,
    @SerialName("activity")
    internal val activity: Map<String, JsonObject>,
    @SerialName("activityItems")
    internal val activityItems: Map<String, List<String>>,
    @SerialName("syncPoints")
    val syncPoints: Map<String, List<Long>>,
    @SerialName("dynActs")
    internal val dynActs: Map<String, JsonObject>,
    @SerialName("stageRewardsData")
    internal val stageRewardsData: Map<String, JsonObject>,
    @SerialName("actThemes")
    val themes: List<ActivityTheme>,
    @SerialName("actFunData")
    internal val funData: JsonObject,
    @SerialName("carData")
    internal val carData: JsonObject,
    @SerialName("siracusaData")
    internal val siracusaData: JsonObject,
    @SerialName("kvSwitchData")
    internal val kvSwitchData: Map<String, JsonObject>,
    @SerialName("hiddenStageData")
    internal val hiddenStageData: List<JsonObject>,
    @SerialName("stringRes")
    internal val stringRes: Map<String, JsonObject>,
    @SerialName("dynEntrySwitchData")
    internal val dynEntrySwitchData: Map<String, JsonObject>,
    @SerialName("extraData")
    internal val extraData: Map<String, JsonObject>
)

@Serializable
public data class ActivityBasicInfo(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val internalType: String,
    @SerialName("displayType")
    val displayType: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("startTime")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("endTime")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime,
    @SerialName("rewardEndTime")
    @Serializable(TimestampSerializer::class)
    val reward: OffsetDateTime,
    @SerialName("displayOnHome")
    val displayOnHome: Boolean,
    @SerialName("hasStage")
    val hasStage: Boolean,
    @SerialName("templateShopId")
    val templateShopId: String? = null,
    @SerialName("medalGroupId")
    val medalGroupId: String? = null,
    @SerialName("ungroupedMedalIds")
    val ungroupedMedalIds: List<String>? = null,
    @SerialName("isReplicate")
    val isReplicate: Boolean,
    @SerialName("needFixedSync")
    val needFixedSync: Boolean
) : Period {
    public val type: String get() = displayType ?: internalType
}


@Serializable
public data class ActivityTheme(
    @SerialName("funcId")
    val funcId: String,
    @SerialName("id")
    val id: String,
    @SerialName("itemId")
    val itemId: String? = null,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("timeNodes")
    val nodes: List<ActivityNode>,
    @SerialName("type")
    val type: ActivityThemeType,
    @SerialName("startTs")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("endTs")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime
) : Period

@Serializable
public data class ActivityNode(
    @SerialName("title")
    val title: String,
    @SerialName("ts")
    @Serializable(TimestampSerializer::class)
    val timestamp: OffsetDateTime
)

@Serializable
public enum class ActivityThemeType {
    ROGUELIKE, CRISIS, CRISISV2, MAINLINE, ACTIVITY, SANDBOX_PERM
}

@Serializable
public data class ActivityClock(
    public val basic: ActivityBasicInfo,
    public val theme: ActivityTheme,
    public val node: ActivityNode?
) : CacheInfo {
    override val created: OffsetDateTime get() = node?.timestamp ?: theme.end
    override val url: String = "${basic.name} - ${node?.title}"
}