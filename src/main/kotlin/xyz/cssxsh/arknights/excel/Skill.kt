package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

public typealias SkillTable = Map<String, Skill>

@Serializable
public data class Skill(
    @SerialName("hidden")
    val hidden: Boolean,
    @SerialName("iconId")
    val iconId: String? = null,
    @SerialName("levels")
    val levels: List<SkillLevel>,
    @SerialName("skillId")
    override val id: String
) : Id

@Serializable
public data class SkillLevel(
    @SerialName("blackboard")
    val blackboard: List<Blackboard>,
    @SerialName("description")
    val description: String? = null,
    @SerialName("duration")
    val duration: Double,
    @SerialName("name")
    override val name: String,
    @SerialName("prefabId")
    val prefabId: String? = null,
    @SerialName("rangeId")
    val rangeId: String? = null,
    @SerialName("skillType")
    val type: SkillType,
    @SerialName("durationType")
    val durationType: DurationType = DurationType.INSTANT,
    @SerialName("spData")
    val data: SkillSpData
) : Name

@Serializable
public enum class SkillType {
    AUTO, MANUAL, PASSIVE
}

@Serializable
public enum class DurationType {
    PASSIVE, INSTANT, LIMITED
}

@Serializable
public data class SkillSpData(
    @SerialName("increment")
    val increment: Double,
    @SerialName("initSp")
    val initSp: Int,
    @SerialName("levelUpCost")
    val levelUpCost: Int? = null,
    @SerialName("maxChargeTime")
    val maxChargeTime: Int,
    @SerialName("spCost")
    val spCost: Int,
    @SerialName("spType")
    val spType: SpType
)

@Serializable
public enum class SpType {
    INCREASE_WITH_TIME,
    INCREASE_WHEN_ATTACK,
    INCREASE_WHEN_TAKEN_DAMAGE,

    @SerialName("8")
    EIGHT
}