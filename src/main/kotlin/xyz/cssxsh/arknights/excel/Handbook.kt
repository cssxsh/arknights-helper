package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import java.io.File

const val HANDBOOK = "handbook_info_table.json"

fun File.readHandbookTable(): HandbookTable = read(name = HANDBOOK, type = GameDataType.EXCEL)

fun HandbookTable.sex() = character.values.groupBy { "【性别】男" in it.storyTextAudio.toString() }

@Serializable
data class HandbookTable(
    @SerialName("handbookDict")
    val character: Map<String, CharacterInfo>,
    @SerialName("npcDict")
    val npc: Map<String, NpcInfo>,
    @SerialName("teamMissionList")
    val team: Map<String, TeamInfo>,
    @SerialName("handbookDisplayConditionList")
    private val handbookDisplayConditionList: JsonObject,
    @SerialName("handbookStageData")
    private val handbookStageData: JsonObject
)

@Serializable
data class CharacterInfo(
    @SerialName("charID")
    val character: String,
    @SerialName("drawName")
    val illust: String,
    @SerialName("handbookAvgList")
    private val handbookAvgList: List<JsonObject>,
    @SerialName("infoName")
    val cv: String,
    @SerialName("storyTextAudio")
    val storyTextAudio: List<StoryTextAudio>
) {
    @Serializable
    data class StoryTextAudio(
        @SerialName("stories")
        val stories: List<Story>,
        @SerialName("storyTitle")
        val storyTitle: String,
        @SerialName("unLockorNot")
        val unLockOrNot: Boolean
    ) {
        @Serializable
        data class Story(
            @SerialName("storyText")
            val storyText: String,
            @SerialName("unLockParam")
            val unLockParam: String,
            @SerialName("unLockString")
            val unLockString: String,
            @SerialName("unLockType")
            val unLockType: Int
        )
    }
}

@Serializable
data class NpcInfo(
    @SerialName("appellation")
    val appellation: String,
    @SerialName("cv")
    val cv: String,
    @SerialName("displayNumber")
    val displayNumber: String,
    @SerialName("groupId")
    val group: String?,
    @SerialName("illust")
    val illust: String,
    @SerialName("name")
    val name: String,
    @SerialName("nationId")
    val nation: String?,
    @SerialName("npcId")
    val npc: String,
    @SerialName("profession")
    val profession: String,
    @SerialName("teamId")
    val team: String?,
    @SerialName("unlockDict")
    val unlockDict: Map<String, JsonObject>
)

@Serializable
data class TeamInfo(
    @SerialName("favorPoint")
    val favorPoint: Int,
    @SerialName("id")
    val id: String,
    @SerialName("item")
    val item: Item,
    @SerialName("powerId")
    val powerId: String,
    @SerialName("powerName")
    val powerName: String,
    @SerialName("sort")
    val sort: Int
) {
    @Serializable
    data class Item(
        @SerialName("count")
        val count: Int,
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: String
    )
}