package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class HandbookTable(
    @SerialName("handbookDict")
    val handbooks: Map<String, Handbook>,
    @SerialName("npcDict")
    val npc: Map<String, NpcInfo>,
    @SerialName("teamMissionList")
    val teams: Map<String, TeamInfo>,
    @SerialName("handbookDisplayConditionList")
    val displayConditions: Map<String, DisplayCondition>,
    @SerialName("handbookStageData")
    val stageData: Map<String, HandbookStage>,
    @SerialName("handbookStageTime")
    val stageTime: List<HandbookStageTime>
)

@Serializable
public data class Handbook(
    @SerialName("charID")
    override val character: String,
    @SerialName("drawName")
    public val illust: String = "Unknown",
    @SerialName("handbookAvgList")
    val avg: List<HandbookAvgData>,
    @SerialName("infoName")
    public val voice: String = "Unknown",
    @SerialName("storyTextAudio")
    val storyTextAudio: List<StoryTextAudio>,
    @SerialName("isLimited")
    internal val isLimited: Boolean = false,
) : CharacterId

@Serializable
public data class HandbookAvgData(
    @SerialName("avgList")
    val detail: List<HandbookAvg>,
    @SerialName("charId")
    val character: String,
    @SerialName("rewardItem")
    val rewards: List<RecordRewardInfo>,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("storyGetTime")
    @Serializable(TimestampSerializer::class)
    val storyGetTime: OffsetDateTime,
    @SerialName("storySetId")
    val storySetId: String,
    @SerialName("storySetName")
    val storySetName: String,
    @SerialName("unlockParam")
    val unlockParam: List<UnlockParam>
)

@Serializable
public data class HandbookAvg(
    @SerialName("storyCanShow")
    val storyCanShow: Boolean = false,
    @SerialName("storyId")
    val storyId: String = "",
    @SerialName("storyInfo")
    val storyInfo: String = "",
    @SerialName("storyIntro")
    val storyIntro: String = "",
    @SerialName("storySetId")
    val storySetId: String = "",
    @SerialName("storySort")
    val storySort: Int = 0,
    @SerialName("storyTxt")
    val storyTxt: String = ""
)

@Serializable
public data class StoryTextAudio(
    @SerialName("stories")
    val stories: List<HandbookStory>,
    @SerialName("storyTitle")
    val title: String,
    @SerialName("unLockorNot")
    val unLockOrNot: Boolean
)

@Serializable
public data class HandbookStory(
    @SerialName("storyText")
    val text: String,
    @SerialName("unLockParam")
    override val param: String,
    @SerialName("unLockString")
    override val string: String,
    @SerialName("unLockType")
    override val type: Int
) : UnlockInfo

@Serializable
public data class NpcInfo(
    @SerialName("appellation")
    override val appellation: String,
    @SerialName("cv")
    override val voice: String,
    @SerialName("displayNumber")
    override val displayNumber: String,
    @SerialName("groupId")
    override val group: String?,
    @Deprecated("Please use illusts", ReplaceWith("illusts[0]"))
    @SerialName("illust")
    public val illust: String = "Unknown",
    @SerialName("illustList")
    public override val illusts: List<String> = emptyList(),
    @SerialName("designerList")
    public override val designers: List<String>? = null,
    @SerialName("name")
    override val name: String,
    @SerialName("nationId")
    override val nation: String?,
    @SerialName("npcId")
    override val id: String,
    @SerialName("profession")
    val profession: String,
    @SerialName("teamId")
    override val team: String?,
    @SerialName("resType")
    val resType: String,
    @SerialName("npcShowAudioInfoFlag")
    val showAudioInfoFlag: Boolean,
    @SerialName("unlockDict")
    internal val unlockDict: Map<String, UnlockInfoImpl>
) : Illust, Voice, Role, Id

@Serializable
public data class UnlockInfoImpl(
    @SerialName("unLockParam")
    override val param: String,
    @SerialName("unLockString")
    override val string: String?,
    @SerialName("unLockType")
    override val type: Int
) : UnlockInfo

@Serializable
public data class UnlockParam(
    @SerialName("unlockParam1")
    val unlockParam1: String = "",
    @SerialName("unlockParam2")
    val unlockParam2: String? = null,
    @SerialName("unlockParam3")
    val unlockParam3: String? = null,
    @SerialName("unlockType")
    val unlockType: Int = 0
)

@Serializable
public data class TeamInfo(
    @SerialName("favorPoint")
    val favorPoint: Int,
    @SerialName("id")
    override val id: String,
    @SerialName("item")
    val item: LegacyItem,
    @SerialName("powerId")
    val powerId: String,
    @SerialName("powerName")
    val powerName: String,
    @SerialName("sort")
    val sort: Int
) : Id

@Serializable
public data class DisplayCondition(
    @SerialName("charId")
    val character: String,
    @SerialName("conditionCharId")
    val conditionCharId: String,
    @SerialName("type")
    val type: String
)

@Serializable
public data class HandbookStageTime(
    @SerialName("timestamp")
    @Serializable(TimestampSerializer::class)
    val timestamp: OffsetDateTime,
    @SerialName("charSet")
    val characters: Set<String>
)

@Serializable
public data class HandbookStage(
    @SerialName("charId")
    val character: String,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("levelId")
    val levelId: String,
    @SerialName("loadingPicId")
    val loadingPictureId: String,
    @SerialName("name")
    val name: String,
    @SerialName("picId")
    val pictureId: String,
    @SerialName("rewardItem")
    val rewards: List<RecordRewardInfo>,
    @SerialName("stageGetTime")
    @Serializable(TimestampSerializer::class)
    val stageGetTime: OffsetDateTime,
    @SerialName("stageId")
    val stageId: String,
    @SerialName("stageNameForShow")
    val stageNameForShow: String,
    @SerialName("unlockParam")
    val unlockParam: List<UnlockParam>,
    @SerialName("zoneId")
    val zoneId: String,
    @SerialName("zoneNameForShow")
    val zoneNameForShow: String
)