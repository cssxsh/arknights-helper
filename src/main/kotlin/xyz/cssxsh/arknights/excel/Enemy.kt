package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

@Serializable
public data class EnemyTable(
    @SerialName("levelInfoList")
    public val levels: List<LevelInfo>,
    @SerialName("enemyData")
    public val enemies: Map<String, Enemy>,
    @SerialName("raceData")
    public val races: Map<String, Race>
)

@Serializable
public data class LevelInfo(
    @SerialName("classLevel")
    public val level: String,
    @SerialName("attack")
    public val attack: LevelInfoRange,
    @SerialName("def")
    public val def: LevelInfoRange,
    @SerialName("magicRes")
    public val magic: LevelInfoRange,
    @SerialName("maxHP")
    public val maxHP: LevelInfoRange,
    @SerialName("moveSpeed")
    public val moveSpeed: LevelInfoRange,
    @SerialName("attackSpeed")
    public val attackSpeed: LevelInfoRange,
    @SerialName("enemyDamageRes")
    public val enemyDamage: LevelInfoRange,
    @SerialName("enemyRes")
    public val enemy: LevelInfoRange
)

@Serializable
public data class LevelInfoRange(
    @SerialName("min")
    public val min: Double,
    @SerialName("max")
    public val max: Double,
) : ClosedFloatingPointRange<Double> by (min .. max)

@Serializable
public data class Enemy(
    @SerialName("ability")
    val ability: String?,
    @SerialName("attackType")
    val type: String?,
    @SerialName("description")
    val description: String,
    @SerialName("enemyId")
    override val id: String,
    @SerialName("enemyIndex")
    val index: String,
    @SerialName("enemyLevel")
    val level: EnemyLevel,
    @SerialName("enemyTags")
    override val tags: List<String>?,
    @SerialName("isInvalidKilled")
    val isInvalidKilled: Boolean,
    @SerialName("name")
    override val name: String,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("overrideKillCntInfos")
    internal val overrideKillCntInfos: Map<String, Int>,
    @SerialName("hideInHandbook")
    internal val hideInHandbook: Boolean,
    @SerialName("abilityList")
    internal val abilities: List<EnemyAbility>,
    @SerialName("linkEnemies")
    internal val linkEnemies: List<String>,
    @SerialName("damageType")
    val damageType: Set<DamageType>,
    @SerialName("invisibleDetail")
    internal val invisibleDetail: Boolean,
) : Id, Name, TagInfo {
    val designation: String get() = "${name}(${level.text})"
}

@Serializable
public enum class EnemyLevel(public val text: String) {
    NORMAL("普通"),
    ELITE("精英"),
    BOSS("领袖");

    override fun toString(): String = text
}

@Serializable
public data class EnemyAbility(
    @SerialName("text")
    public val text: String,
    @SerialName("textFormat")
    public val format: String
)

@Serializable
public data class Race(
    @SerialName("id")
    val id: String,
    @SerialName("raceName")
    val name: String,
    @SerialName("sortId")
    val sortId: Int
)

@Serializable
public enum class DamageType(public val text: String) {
    NO_DAMAGE("其他"),
    PHYSIC("物理"),
    MAGIC("法术"),
    HEAL("治疗");

    override fun toString(): String = text
}