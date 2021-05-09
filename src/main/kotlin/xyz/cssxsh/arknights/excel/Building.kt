package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import java.io.File

fun File.readBuilding(): Building = read(type = ExcelDataType.BUILDING)

fun BuffMap(building: Building, characters: CharacterTable): BuffMap {
    return building.characters.map { (id, info) ->
        val list = info.buffs.flatMap { (data) -> data.map { it.buff } }.map { buff -> building.buffs.getValue(buff) }
        characters.getValue(id).name to list
    }.toMap()
}

typealias BuffMap = Map<String, List<BuildingBuff>>

@Serializable
data class Building(
    @SerialName("apToLaborRatio")
    private val apToLaborRatio: Int,
    @SerialName("apToLaborUnlockLevel")
    private val apToLaborUnlockLevel: Int,
    @SerialName("assistantUnlock")
    private val assistantUnlock: List<Int>,
    @SerialName("basicFavorPerDay")
    private val basicFavorPerDay: Int,
    @SerialName("buffs")
    val buffs: Map<String, BuildingBuff>,// XXX
    @SerialName("chars")
    val characters: Map<String, CharacterBuildingInfo>,// XXX
    @SerialName("comfortLimit")
    private val comfortLimit: Int,
    @SerialName("comfortManpowerRecoverFactor")
    private val comfortManpowerRecoverFactor: Int,
    @SerialName("controlData")
    private val controlData: JsonObject,// XXX
    @SerialName("controlSlotId")
    private val controlSlotId: String,
    @SerialName("creditCeiling")
    private val creditCeiling: Int,
    @SerialName("creditComfortFactor")
    private val creditComfortFactor: Int,
    @SerialName("creditFormula")
    private val creditFormula: JsonObject,// XXX
    @SerialName("creditGuaranteed")
    private val creditGuaranteed: Int,
    @SerialName("creditInitiativeLimit")
    private val creditInitiativeLimit: Int,
    @SerialName("creditPassiveLimit")
    private val creditPassiveLimit: Int,
    @SerialName("customData")
    private val customData: JsonObject,// XXX
    @SerialName("dormData")
    private val dormData: JsonObject,// XXX
    @SerialName("furniDuplicationLimit")
    private val furniDuplicationLimit: Int,
    @SerialName("goldItems")
    private val goldItems: Map<Int, Int>,
    @SerialName("hireData")
    private val hireData: JsonObject,// XXX
    @SerialName("humanResourceLimit")
    private val humanResourceLimit: Int,
    @SerialName("initMaxLabor")
    private val initMaxLabor: Int,
    @SerialName("laborAssistUnlockLevel")
    private val laborAssistUnlockLevel: Int,
    @SerialName("laborRecoverTime")
    private val laborRecoverTime: Int,
    @SerialName("layouts")
    private val layouts: JsonObject,// XXX
    @SerialName("manpowerDisplayFactor")
    private val manpowerDisplayFactor: Int,
    @SerialName("manufactData")
    private val manufactData: JsonObject,// XXX
    @SerialName("manufactFormulas")
    private val manufactFormulas: Map<Int, JsonObject>,
    @SerialName("manufactInputCapacity")
    private val manufactInputCapacity: Int,
    @SerialName("manufactLaborCostUnit")
    private val manufactLaborCostUnit: Int,
    @SerialName("manufactManpowerCostByNum")
    private val manufactManpowerCostByNum: List<Int>,
    @SerialName("manufactReduceTimeUnit")
    private val manufactReduceTimeUnit: Int,
    @SerialName("manufactStationBuff")
    private val manufactStationBuff: Double,
    @SerialName("manufactUnlockTips")
    private val manufactUnlockTips: String,
    @SerialName("meetingData")
    private val meetingData: JsonObject,
    @SerialName("meetingSlotId")
    private val meetingSlotId: String,
    @SerialName("powerData")
    private val powerData: JsonObject,
    @SerialName("prefabs")
    private val prefabs: JsonObject,
    @SerialName("processedCountRatio")
    private val processedCountRatio: Int,
    @SerialName("roomUnlockConds")
    private val roomUnlockConds: JsonObject,
    @SerialName("rooms")
    private val rooms: JsonObject,
    @SerialName("shopCounterCapacity")
    private val shopCounterCapacity: Int,
    @SerialName("shopData")
    private val shopData: JsonObject,
    @SerialName("shopFormulas")
    private val shopFormulas: JsonObject,
    @SerialName("shopOutputRatio")
    private val shopOutputRatio: JsonElement?,
    @SerialName("shopStackRatio")
    private val shopStackRatio: JsonElement?,
    @SerialName("shopUnlockTips")
    private val shopUnlockTips: String,
    @SerialName("socialResourceLimit")
    private val socialResourceLimit: Int,
    @SerialName("socialSlotNum")
    private val socialSlotNum: Int,
    @SerialName("tiredApThreshold")
    private val tiredApThreshold: Int,
    @SerialName("tradingData")
    private val tradingData: JsonObject,
    @SerialName("tradingLaborCostUnit")
    private val tradingLaborCostUnit: Int,
    @SerialName("tradingManpowerCostByNum")
    private val tradingManpowerCostByNum: List<Int>,
    @SerialName("tradingReduceTimeUnit")
    private val tradingReduceTimeUnit: Int,
    @SerialName("tradingStrategyUnlockLevel")
    private val tradingStrategyUnlockLevel: Int,
    @SerialName("trainingData")
    private val trainingData: JsonObject,
    @SerialName("workshopData")
    private val workshopData: JsonObject,
    @SerialName("workshopFormulas")
    private val workshopFormulas: Map<Int, JsonObject>
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