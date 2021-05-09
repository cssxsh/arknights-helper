package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.*
import java.io.File

fun File.readTeamTable(): TeamTable = read(type = ExcelDataType.TEAM)

typealias TeamTable = Map<String, Team>

typealias PowerMap = Map<PowerLevel, Map<Team, List<String>>>

const val DefaultTeam = "none"

fun PowerMap(table: TeamTable, characters: CharacterTable): PowerMap {
    val default = table.getValue(DefaultTeam)
    return table.values.groupBy { team -> PowerLevel.values()[team.level] }.mapValues { (level, teams) ->
        (teams + default).toSet().associateWith { team ->
            characters.values.filter { level.get(it) == team.id }.map { it.name }
        }
    }
}

enum class PowerLevel(val text: String, val get: (Character) -> String) {
    NATION("国家/地区", { it.nation ?: DefaultTeam }),
    GROUP("势力", { it.group ?: DefaultTeam }),
    TEAM("队伍", { it.team ?: DefaultTeam });
}

@Serializable
data class Team(
    @SerialName("color")
    val color: String,
    @SerialName("isLimited")
    val isLimited: Boolean,
    @SerialName("isRaw")
    val isRaw: Boolean,
    @SerialName("orderNum")
    val order: Int,
    @SerialName("powerCode")
    val code: String,
    @SerialName("powerId")
    override val id: String,
    @SerialName("powerLevel")
    val level: Int,
    @SerialName("powerName")
    override val name: String
) : Id, Name