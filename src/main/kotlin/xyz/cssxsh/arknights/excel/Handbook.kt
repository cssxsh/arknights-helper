package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

private val TagRegex = """(?<=【|\[)(.+)(?:]|】)(.+)""".toRegex()

fun Handbook.infos(): Map<String, String> {
    return stories.values.flatten().flatMap { text ->
        TagRegex.findAll(text).map { result ->
            result.destructured.let { (key, value) -> key to value.trim() }
        }
    }.toMap()
}

val Handbook.stories get() = storyTextAudio.associate { info -> info.title to info.stories.map { it.text } }

fun HandbookMap.tags() = entries.fold(emptySet<String>()) { acc, (_, handbook) -> acc + handbook.infos().keys }

@Serializable
data class HandbookTable(
    @SerialName("handbookDict")
    val handbooks: HandbookMap,
    @SerialName("npcDict")
    val npc: Map<String, NpcInfo>,
    @SerialName("teamMissionList")
    val teams: Map<String, TeamInfo>,
    @SerialName("handbookDisplayConditionList")
    private val handbookDisplayConditionList: JsonObject,
    @SerialName("handbookStageData")
    private val handbookStageData: JsonObject
)

@Serializable
data class Handbook(
    @SerialName("charID")
    override val character: String,
    @SerialName("drawName")
    override val illust: String,
    @SerialName("handbookAvgList")
    private val handbookAvgList: List<JsonObject>,
    @SerialName("infoName")
    override val voice: String,
    @SerialName("storyTextAudio")
    val storyTextAudio: List<StoryTextAudio>
) : Illust, Voice, CharacterId

@Serializable
data class StoryTextAudio(
    @SerialName("stories")
    val stories: List<HandbookStory>,
    @SerialName("storyTitle")
    val title: String,
    @SerialName("unLockorNot")
    val unLockOrNot: Boolean
)

@Serializable
data class HandbookStory(
    @SerialName("storyText")
    val text: String,
    @SerialName("unLockParam")
    val unLockParam: String,
    @SerialName("unLockString")
    val unLockString: String,
    @SerialName("unLockType")
    val unLockType: Int
)

@Serializable
data class NpcInfo(
    @SerialName("appellation")
    override val appellation: String,
    @SerialName("cv")
    override val voice: String,
    @SerialName("displayNumber")
    override val displayNumber: String,
    @SerialName("groupId")
    override val group: String?,
    @SerialName("illust")
    override val illust: String,
    @SerialName("name")
    override val name: String,
    @SerialName("nationId")
    override val nation: String?,
    @SerialName("npcId")
    override val id: String,
    @SerialName("profession")
    val profession: String,// XXX
    @SerialName("teamId")
    override val team: String?,
    @SerialName("unlockDict")
    val unlockDict: Map<String, JsonObject>
) : Illust, Voice, Role, Id

@Serializable
data class TeamInfo(
    @SerialName("favorPoint")
    val favorPoint: Int,
    @SerialName("id")
    override val id: String,
    @SerialName("item")
    val item: LegacyItem,
    @SerialName("powerId")
    val powerId: String, // XXX
    @SerialName("powerName")
    val powerName: String,
    @SerialName("sort")
    val sort: Int
) : Id