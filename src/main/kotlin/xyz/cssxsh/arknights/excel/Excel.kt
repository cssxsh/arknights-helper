package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

public interface Id {
    public val id: String
}

public interface Name {
    public val name: String
}

public interface CharacterId {
    public val character: String
}

public interface GroupId {
    public val group: String?
}

public interface NationId {
    public val nation: String?
}

public interface TeamId {
    public val team: String?
}

public interface Role : Name, GroupId, NationId, TeamId {
    public val appellation: String
    public val displayNumber: String?
}

public interface Illust {
    public val illusts: List<String>?
    public val designers: List<String>?
}

public interface Voice {
    public val voice: String
}

public interface BuffId {
    public val buff: String
}

public interface SkillId {
    public val skill: String?
}

public interface TagInfo {
    public val tags: List<String>?
}

public interface ZoneId {
    public val zoneId: String
}

public interface Period : ClosedRange<OffsetDateTime> {
    public override val start: OffsetDateTime
    public val end: OffsetDateTime

    override val endInclusive: OffsetDateTime get() = end
}

public interface StoryId {
    public val story: String
}

@Serializable
public data class Blackboard(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: Double = 0.0,
    @SerialName("valueStr")
    val valueStr: String = ""
)

@Serializable
public data class UnlockCondition(
    @SerialName("level")
    val level: Int,
    @SerialName("phase")
    val phase: String
)

public interface UnlockInfo {
    public val param: String
    public val string: String?
    public val type: String
}

@Serializable
public data class LegacyItem(
    @SerialName("count")
    val count: Int,
    @SerialName("id")
    override val id: String,
    @SerialName("type")
    val type: String
) : Id

@Serializable
public enum class ExcelDataType(override val url: String) : CacheKey {
    BUILDING("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/building_data.json"),
    CHARACTER("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/character_table.json"),
    CONST("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/gamedata_const.json"),
    ENEMY("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/enemy_handbook_table.json"),
    GACHA("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/gacha_table.json"),
    HANDBOOK("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/handbook_info_table.json"),
    WORD("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/charword_table.json"),
    SKILL("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/skill_table.json"),
    STORY("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/story_review_table.json"),
    TEAM("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/handbook_team_table.json"),
    ZONE("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/zone_table.json"),
    SKIN("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/skin_table.json"),
    ACTIVITY("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/activity_table.json"),
    EQUIP("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/uniequip_table.json"),
    VERSION("https://raw.githubusercontent.com/Kengxxiao/ArknightsGameData/master/zh_CN/gamedata/excel/data_version.txt");

    override val filename: String = url.substringAfterLast('/')
}

public data class ExcelDataVersion(
    val stream: String,
    val change: String,
    val versionControl: String
)