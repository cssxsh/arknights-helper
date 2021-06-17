package xyz.cssxsh.arknights.excel

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
) : Id

private fun File.readBuilding(): Building = read(type = ExcelDataType.BUILDING)

private fun BuffMap(building: Building, characters: CharacterTable): BuffMap {
    return building.characters.map { (id, info) ->
        val list = info.buffs.flatMap { (data) -> data.map { it.buff } }.map { buff -> building.buffs.getValue(buff) }
        characters.getValue(id).name to list
    }.toMap()
}

typealias BuffMap = Map<String, List<BuildingBuff>>

private fun File.readCharacterTable(): CharacterTable = read(type = ExcelDataType.CHARACTER)

typealias CharacterMap = Map<String, Character>

private fun CharacterMap(table: CharacterTable): CharacterMap = table.values.associateBy { it.name }

private fun File.readConstInfo(): ConstInfo = read(type = ExcelDataType.CONST)

private fun File.readEnemyTable(): EnemyTable = read(type = ExcelDataType.ENEMY)

typealias EnemyMap = Map<EnemyLevel, List<Enemy>>

private fun EnemyMap(table: EnemyTable): EnemyMap = table.values.groupBy { it.level }

private fun File.readGachaTable(): GachaTable = read(type = ExcelDataType.GACHA)

private fun File.readHandbookTable(): HandbookTable = read(type = ExcelDataType.HANDBOOK)

typealias HandbookMap = Map<String, Handbook>

private fun HandbookMap(book: HandbookTable, characters: CharacterTable): HandbookMap {
    return book.handbooks.mapNotNull { (id, info) ->
        characters[id]?.let { it.name to info }
    }.toMap()
}

private fun File.readSkillTable(): SkillTable = read(type = ExcelDataType.SKILL)

typealias SkillMap = Map<String, List<Skill>>

private fun SkillMap(table: SkillTable, characters: CharacterTable): SkillMap {
    return characters.values.associate { character ->
        character.name to character.skills.mapNotNull { info -> info.skill?.let { table.getValue(it) } }
    }
}

private fun File.readStoryTable(): StoryTable = read(type = ExcelDataType.STORY)

typealias StoryMap = Map<ActionType, List<Story>>

private fun StoryMap(table: StoryTable): StoryMap = table.values.groupBy { it.action }

private fun File.readTeamTable(): TeamTable = read(type = ExcelDataType.TEAM)

typealias PowerMap = Map<PowerLevel, Map<Team, List<String>>>

private fun PowerMap(table: TeamTable, characters: CharacterTable): PowerMap {
    val default = table.getValue(DefaultTeam)
    return table.values.groupBy { team -> PowerLevel.values()[team.level] }.mapValues { (level, teams) ->
        (teams + default).toSet().associateWith { team ->
            characters.values.filter { level.get(it) == team.id }.map { it.name }
        }
    }
}

private fun File.readZoneTable(): ZoneTable = read(type = ExcelDataType.ZONE)

typealias ZoneMap = Map<ZoneType, List<Zone>>

internal fun ZoneMap(table: ZoneTable): ZoneMap = table.zones.values.groupBy { it.type }

typealias WeeklyMap = Map<WeeklyType, List<Pair<Zone, Weekly>>>

internal fun WeeklyMap(table: ZoneTable): WeeklyMap {
    return table.weekly.entries.groupBy { it.value.type }.mapValues { (_, list) ->
        list.map { (id, weekly) -> table.zones.getValue(id) to weekly }
    }
}

class ExcelData(override val dir: File): GameDataDownloader {
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
    val version by lazy { dir.readExcelDataVersion() }

    override val types get() = ExcelDataType.values().asIterable()
}

enum class ExcelDataType(file: String) : GameDataType {
    BUILDING("building_data.json"),
    CHARACTER("character_table.json"),
    CONST("gamedata_const.json"),
    ENEMY("enemy_handbook_table.json"),
    GACHA("gacha_table.json"),
    HANDBOOK("handbook_info_table.json"),
    SKILL("skill_table.json"),
    STORY("story_review_table.json"),
    TEAM("handbook_team_table.json"),
    ZONE("zone_table.json"),
    VERSION("data_version.txt");

    override val path = "excel/${file}"

    override val url: Url = jsdelivr(this)
}

private fun path(type: GameDataType): String = "${SERVER.locale}/gamedata/${type.path}"

data class ExcelDataVersion(
    val stream: String,
    val change: String,
    val versionControl: String
)

internal fun File.readExcelDataVersion(): ExcelDataVersion {
    return resolve(ExcelDataType.VERSION.path).readText().readExcelDataVersion()
}

internal suspend fun loadExcelDataVersion(): ExcelDataVersion {
    return useHttpClient<String> { it.get(jsdelivr(ExcelDataType.VERSION)) }.readExcelDataVersion()
}

internal fun String.readExcelDataVersion(): ExcelDataVersion {
    lateinit var stream: String
    lateinit var change: String
    lateinit var versionControl: String
    lines().filter(String::isNotBlank).forEach {
        val (name, value) = it.split(":")
        when (name) {
            "Stream" -> {
                stream = value
            }
            "Change" -> {
                change = value
            }
            "VersionControl" -> {
                versionControl = value
            }
            else -> {}
        }
    }
    return ExcelDataVersion(
        stream = stream,
        change = change,
        versionControl = versionControl
    )
}

private val github = { type: ExcelDataType -> Url("https://raw.githubusercontent.com/$GITHUB_REPO/master/${path(type)}") }

private val jsdelivr = { type: ExcelDataType -> Url("https://cdn.jsdelivr.net/gh/$GITHUB_REPO@master/${path(type)}") }