package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

public typealias StoryTable = Map<String, Story>

public val Story.start: OffsetDateTime get() = startTime.takeIf { it.toEpochSecond() > 1 } ?: startShowTime

@Serializable
public data class Story(
    @SerialName("actType")
    val action: ActionType,
    @SerialName("endShowTime")
    @Serializable(TimestampSerializer::class)
    val endShowTime: OffsetDateTime,
    @SerialName("endTime")
    @Serializable(TimestampSerializer::class)
    val endTime: OffsetDateTime,
    @SerialName("entryType")
    val entry: EntryType,
    @SerialName("id")
    override val id: String,
    @SerialName("infoUnlockDatas")
    val infoUnlockData: List<StoryInfo>,
    @SerialName("name")
    override val name: String,
    @SerialName("remakeEndTime")
    @Serializable(TimestampSerializer::class)
    val remakeEndTime: OffsetDateTime,
    @SerialName("remakeStartTime")
    @Serializable(TimestampSerializer::class)
    val remakeStartTime: OffsetDateTime,
    @SerialName("replicateActionId")
    val replicateActionId: String?,
    @SerialName("rewards")
    val rewards: List<LegacyItem>?,
    @SerialName("startShowTime")
    @Serializable(TimestampSerializer::class)
    val startShowTime: OffsetDateTime,
    @SerialName("startTime")
    @Serializable(TimestampSerializer::class)
    val startTime: OffsetDateTime,
    @SerialName("storyCompleteMedalId")
    val storyCompleteMedalId: String?,
    @SerialName("storyEntryPicId")
    val storyEntryPicId: String?,
    @SerialName("storyMainColor")
    val storyMainColor: String?,
    @SerialName("storyPicId")
    val storyPicId: String?
) : Id, Name

public enum class ActionType(public val text: String) {
    ACTIVITY_STORY("活动剧情"),
    MINI_STORY("微型故事集"),
    MAIN_STORY("主线剧情"),
    NONE("其他故事");
}

public enum class EntryType(public val text: String) {
    ACTIVITY("活动"),
    MINI_ACTIVITY("微型活动"),
    MAINLINE("主线"),
    NONE("其他");
}

@Serializable
public data class StoryInfo(
    @SerialName("avgTag")
    val avgTag: String,
    @SerialName("costItemCount")
    val costItemCount: Int,
    @SerialName("costItemId")
    val costItemId: String?,
    @SerialName("costItemType")
    val costItemType: CostItemType,
    @SerialName("requiredStages")
    val requiredStages: List<StoryStage>?,
    @SerialName("stageCount")
    val stageCount: Int,
    @SerialName("storyCanEnter")
    val storyCanEnter: Int,
    @SerialName("storyCanShow")
    val storyCanShow: Int,
    @SerialName("storyCode")
    val storyCode: String?,
    @SerialName("storyDependence")
    val storyDependence: String?,
    @SerialName("storyGroup")
    val storyGroup: String,
    @SerialName("storyId")
    override val story: String,
    @SerialName("storyInfo")
    val storyInfo: String,
    @SerialName("storyName")
    val storyName: String,
    @SerialName("storyPic")
    val storyPic: String?,
    @SerialName("storyReviewType")
    val storyReviewType: Int,
    @SerialName("storySort")
    val storySort: Int,
    @SerialName("storyTxt")
    val storyTxt: String,
    @SerialName("unLockType")
    val unLockType: UnLockType
) : StoryId

public enum class CostItemType {
    NONE,
    MATERIAL;
}

public enum class UnLockType {
    STAGE_CLEAR,
    USE_ITEM;
}

@Serializable
public data class StoryStage(
    @SerialName("stageId")
    val stage: String,
    @SerialName("minState")
    val min: Int,
    @SerialName("maxState")
    val max: Int,
)