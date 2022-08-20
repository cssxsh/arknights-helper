package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

public typealias TeamTable = Map<String, Team>

public const val DefaultTeam: String = "none"

public enum class PowerLevel(public val text: String, public val get: (Character) -> String) {
    NATION("国家/地区", { it.nation ?: DefaultTeam }),
    GROUP("势力", { it.group ?: DefaultTeam }),
    TEAM("队伍", { it.team ?: DefaultTeam });
}

@Serializable
public data class Team(
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