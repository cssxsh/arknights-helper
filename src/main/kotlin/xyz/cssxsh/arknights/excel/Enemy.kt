package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

public typealias EnemyTable = Map<String, Enemy>

public val Enemy.designation: String get() = "${name}(${race?.let { "$it#" }.orEmpty()}${level.text})"

@Serializable
public data class Enemy(
    @SerialName("ability")
    val ability: String?,
    @SerialName("attack")
    val attack: String,
    @SerialName("attackType")
    val type: String,// XXX 多属性用空格分开
    @SerialName("defence")
    val defence: String,
    @SerialName("description")
    val description: String,
    @SerialName("endure")
    val endure: String,
    @SerialName("enemyId")
    override val id: String,
    @SerialName("enemyIndex")
    val index: String,
    @SerialName("enemyLevel")
    val level: EnemyLevel,
    @SerialName("enemyRace")
    val race: String?,
    @SerialName("enemyTags")
    override val tags: List<String>?,
    @SerialName("isInvalidKilled")
    val isInvalidKilled: Boolean,
    @SerialName("name")
    override val name: String,
    @SerialName("overrideKillCntInfos")
    val overrideKillCntInfos: JsonObject,
    @SerialName("resistance")
    val resistance: String,
    @SerialName("sortId")
    val sortId: Int
) : Id, Name, TagInfo

public enum class EnemyLevel(public val text: String) {
    NORMAL("普通"),
    ELITE("精英"),
    BOSS("领袖");
}
