package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime

fun File.readStoryTable(): StoryTable = read(type = ExcelDataType.STORY)

typealias StoryTable = Map<String, Story>

typealias StoryMap = Map<ActionType, List<Story>>

fun StoryMap(table: StoryTable): StoryMap = table.values.groupBy { it.action }

val Story.start get() = startTime.takeIf { it.toEpochSecond() > 1 } ?: startShowTime

@Serializable
data class Story(
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

enum class ActionType(val text: String) {
    ACTIVITY_STORY("活动剧情"),
    MINI_STORY("微型故事集"),
    MAIN_STORY("主线剧情"),
    NONE("其他故事");
}

enum class EntryType(val text: String) {
    ACTIVITY("活动"),
    MINI_ACTIVITY("微型活动"),
    MAINLINE("主线"),
    NONE("其他");
}

@Serializable
data class StoryInfo(
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
): StoryId

enum class CostItemType {
    NONE,
    MATERIAL;
}

enum class UnLockType {
    STAGE_CLEAR,
    USE_ITEM;
}

@Serializable
data class StoryStage(
    @SerialName("stageId")
    val stage: String,
    @SerialName("minState")
    val min: Int,
    @SerialName("maxState")
    val max: Int,
)