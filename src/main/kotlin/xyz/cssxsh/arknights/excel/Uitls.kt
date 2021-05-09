package xyz.cssxsh.arknights.excel

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime

interface Id {
    val id: String
}

interface Name {
    val name: String
}

interface CharacterId {
    val character: String
}

interface GroupId {
    val group: String?
}

interface NationId {
    val nation: String?
}

interface TeamId {
    val team: String?
}

interface Role : Name, GroupId, NationId, TeamId {
    val appellation: String
    val displayNumber: String?
}

interface Illust {
    val illust: String
}

interface Voice {
    val voice: String
}

interface BuffId {
    val buff: String
}

interface SkillId {
    val skill: String?
}

interface TagInfo {
    val tags: List<String>?
}

interface ZoneId {
    val zoneId: String
}

interface Period {
    val start: OffsetDateTime
    val end: OffsetDateTime
}

interface StoryId {
    val story: String
}

@Serializable
data class Blackboard(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: Double
)

@Serializable
data class UnlockCondition(
    @SerialName("level")
    val level: Int,
    @SerialName("phase")
    val phase: Int
)

@Serializable
data class LegacyItem(
    @SerialName("count")
    val count: Int,
    @SerialName("id")
    override val id: String,
    @SerialName("type")
    val type: String
): Id

class ExcelData(dir: File) {
    private val building by lazy { dir.readBuilding() }
    private val character by lazy { dir.readCharacterTable() }
    val const by lazy { dir.readConstInfo() }
    val buffs by lazy { BuffMap(building, character) }
    val characters by lazy { CharacterMap(character) }
    val gacha by lazy { dir.readGachaTable() }
    private val book by lazy { dir.readHandbookTable() }
    val handbooks by lazy { HandbookMap(book, character) }
    private val skill by lazy { dir.readSkillTable() }
    val skills by lazy { SkillMap(skill, character) }
    private val teamTable by lazy { dir.readTeamTable() }
    val powers by lazy { PowerMap(teamTable, character) }
    private val storyTable by lazy { dir.readStoryTable() }
    val stories by lazy { StoryMap(storyTable) }
    private val enemy by lazy { dir.readEnemyTable() }
    val enemies by lazy { EnemyMap(enemy) }
    private val zone by lazy { dir.readZoneTable() }
    val zones by lazy { ZoneMap(zone) }
    val weeks by lazy { WeeklyMap(zone) }
}

enum class ExcelDataType(file: String): GameDataType {
    BUILDING("building_data.json"),
    CHARACTER("character_table.json"),
    CONST("gamedata_const.json"),
    ENEMY("enemy_handbook_table.json"),
    GACHA("gacha_table.json"),
    HANDBOOK("handbook_info_table.json"),
    SKILL("skill_table.json"),
    STORY("story_review_table.json"),
    TEAM("handbook_team_table.json"),
    ZONE("zone_table.json");

    override val path = "excel/${file}"
}


private fun path(type: GameDataType): String = "${SERVER.locale}/gamedata/${type.path}"

internal val github = { type: ExcelDataType ->
    Url("https://raw.githubusercontent.com/$GITHUB_REPO/master/${path(type)}")
}

internal val jsdelivr = { type: ExcelDataType ->
    Url("https://cdn.jsdelivr.net/gh/$GITHUB_REPO@master/${path(type)}")
}

suspend fun Iterable<ExcelDataType>.download(dir: File, flush: Boolean = false): List<File> = load(dir, flush, jsdelivr)