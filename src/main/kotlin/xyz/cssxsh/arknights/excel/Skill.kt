package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.*
import java.io.File

fun File.readSkillTable(): SkillTable = read(type = ExcelDataType.SKILL)

fun SkillMap(table: SkillTable, characters: CharacterTable): SkillMap {
    return characters.values.associate { character ->
        character.name to character.skills.mapNotNull { info -> info.skill?.let { table.getValue(it) } }
    }
}

typealias SkillTable = Map<String, Skill>

typealias SkillMap = Map<String, List<Skill>>

@Serializable
data class Skill(
    @SerialName("hidden")
    val hidden: Boolean,
    @SerialName("iconId")
    val iconId: String?,
    @SerialName("levels")
    val levels: List<Level>,
    @SerialName("skillId")
    override val id: String
) : Id

@Serializable
data class Level(
    @SerialName("blackboard")
    val blackboard: List<Blackboard>,
    @SerialName("description")
    val description: String,
    @SerialName("duration")
    val duration: Double,
    @SerialName("name")
    override val name: String,
    @SerialName("prefabId")
    val prefabId: String?,
    @SerialName("rangeId")
    val rangeId: String?,
    @SerialName("skillType")
    val type: Int,
    @SerialName("spData")
    val data: SpData
) : Name

@Serializable
data class SpData(
    @SerialName("increment")
    val increment: Double,
    @SerialName("initSp")
    val initSp: Int,
    @SerialName("levelUpCost")
    val levelUpCost: Int?,
    @SerialName("maxChargeTime")
    val maxChargeTime: Int,
    @SerialName("spCost")
    val spCost: Int,
    @SerialName("spType")
    val spType: Int
)