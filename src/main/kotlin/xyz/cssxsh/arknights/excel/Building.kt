package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

@Serializable
data class Building(
    @SerialName("buffs")
    val buffs: Map<String, BuildingBuff>,// XXX
    @SerialName("chars")
    val characters: Map<String, CharacterBuildingInfo>,// XXX
)

@Serializable
data class CharacterBuildingInfo(
    @SerialName("buffChar")
    val buffs: List<BuffChar>,
    @SerialName("charId")
    override val character: String,
    @SerialName("maxManpower")
    val maxManpower: Int
) : CharacterId

@Serializable
data class BuffChar(
    @SerialName("buffData")
    val data: List<BuffData>
)

@Serializable
data class BuffData(
    @SerialName("buffId")
    override val buff: String,
    @SerialName("cond")
    val cond: UnlockCondition
) : BuffId

@Serializable
data class BuildingBuff(
    @SerialName("buffCategory")
    val category: String,
    @SerialName("buffColor")
    val color: String,
    @SerialName("buffIcon")
    val icon: String,
    @SerialName("buffId")
    override val id: String,
    @SerialName("buffName")
    override val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("roomType")
    val roomType: RoomType,
    @SerialName("skillIcon")
    val skillIcon: String,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("textColor")
    val textColor: String
) : Id, Name

enum class RoomType {
    CONTROL,
    POWER,
    MANUFACTURE,
    TRADING,
    WORKSHOP,
    TRAINING,
    DORMITORY,
    HIRE,
    MEETING
}