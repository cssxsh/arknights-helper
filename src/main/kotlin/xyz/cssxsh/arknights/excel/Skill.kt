package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

public typealias SkillTable = Map<String, Skill>

@Serializable
public data class Skill(
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
public data class Level(
    @SerialName("blackboard")
    val blackboard: List<Blackboard>,
    @SerialName("description")
    val description: String?,
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
public data class SpData(
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