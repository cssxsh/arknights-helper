package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class EquipTable(
    @SerialName("equipDict")
    public val equips: Map<String, Equip>,
    @SerialName("missionList")
    public val missions: Map<String, EquipMission>,
    @SerialName("subProfDict")
    public val professions: Map<String, EquipProfession>,
    @SerialName("charEquip")
    public val characterEquip: Map<String, List<String>>,
    @SerialName("equipTrackDict")
    public val equipTracks: List<EquipTrack>
)

@Serializable
public data class Equip(
    @SerialName("charEquipOrder")
    val characterEquipOrder: Int,
    @SerialName("charId")
    override val character: String,
    @SerialName("equipShiningColor")
    val shiningColor: String,
    @SerialName("itemCost")
    val itemCost: Map<Int, List<EquipItemCost>>?,
    @SerialName("missionList")
    val missions: List<String>,
    @SerialName("showEvolvePhase")
    val showEvolvePhase: String,
    @SerialName("showLevel")
    val showLevel: Int,
    @SerialName("tmplId")
    val tmplId: String?,
    @SerialName("type")
    val type: String,
    @SerialName("typeIcon")
    val typeIcon: String,
    @SerialName("typeName1")
    val typeName1: String,
    @SerialName("typeName2")
    val typeName2: String?,
    @SerialName("uniEquipDesc")
    val description: String,
    @SerialName("uniEquipGetTime")
    @Serializable(TimestampSerializer::class)
    val getTime: OffsetDateTime,
    @SerialName("uniEquipIcon")
    val icon: String,
    @SerialName("uniEquipId")
    val equipId: String,
    @SerialName("uniEquipName")
    val name: String,
    @SerialName("unlockEvolvePhase")
    val unlockEvolvePhase: String,
    @SerialName("unlockFavorPoint")
    val unlockFavorPoint: Int,
    @SerialName("unlockLevel")
    val unlockLevel: Int
) : CharacterId

@Serializable
public data class EquipItemCost(
    @SerialName("id")
    val id: String,
    @SerialName("count")
    val count: Int,
    @SerialName("type")
    val type: String
)

@Serializable
public data class EquipTrack(
    @SerialName("timeStamp")
    @Serializable(TimestampSerializer::class)
    val timestamp: OffsetDateTime,
    @SerialName("trackList")
    val tracks: List<EquipTrackInfo>
)

@Serializable
public data class EquipTrackInfo(
    @SerialName("charId")
    override val character: String,
    @SerialName("equipId")
    val equipId: String
) : CharacterId

@Serializable
public data class EquipProfession(
    @SerialName("subProfessionId")
    val id: String,
    @SerialName("subProfessionName")
    val name: String,
    @SerialName("subProfessionCatagory")
    val category: String
)

@Serializable
public data class EquipMission(
    @SerialName("template")
    val template: String,
    @SerialName("desc")
    val description: String,
    @SerialName("paramList")
    val params: List<String>,
    @SerialName("uniEquipMissionId")
    val equipMissionId: String,
    @SerialName("uniEquipMissionSort")
    val equipMissionSort: Int,
    @SerialName("uniEquipId")
    val equipId: String,
    @SerialName("jumpStageId")
    val jumpStageId: String?
)