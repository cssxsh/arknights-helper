package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class Building(
    @SerialName("buffs")
    val buffs: Map<String, BuildingBuff>,
    @SerialName("chars")
    val characters: Map<String, CharacterBuildingInfo>,
    // ap
    @SerialName("apToLaborUnlockLevel")
    internal val apToLaborUnlockLevel: Int,
    @SerialName("apToLaborRatio")
    internal val apToLaborRatio: Int,
    //
    @SerialName("assistFavorReport")
    @Serializable(TimestampSerializer::class)
    internal val assistFavorReport: OffsetDateTime,
    @SerialName("assistantUnlock")
    internal val assistantUnlock: List<Int>,
    @SerialName("basicFavorPerDay")
    internal val basicFavorPerDay: Int,
    // comfort
    @SerialName("comfortLimit")
    internal val comfortLimit: Int,
    @SerialName("comfortManpowerRecoverFactor")
    internal val comfortManpowerRecoverFactor: Int,
    // control
    @SerialName("controlData")
    internal val controlData: JsonObject,
    @SerialName("controlSlotId")
    internal val controlSlotId: String,
    //
    @SerialName("creditCeiling")
    internal val creditCeiling: Int,
    @SerialName("creditComfortFactor")
    internal val creditComfortFactor: Int,
    @SerialName("creditFormula")
    internal val creditFormula: JsonObject,
    @SerialName("creditGuaranteed")
    internal val creditGuaranteed: Int,
    @SerialName("creditInitiativeLimit")
    internal val creditInitiativeLimit: Int,
    @SerialName("creditPassiveLimit")
    internal val creditPassiveLimit: Int,
    //
    @SerialName("customData")
    internal val customData: Map<String, JsonObject>,
    @SerialName("dormData")
    internal val dormData: JsonObject,
    @SerialName("furniDuplicationLimit")
    internal val furnitureDuplicationLimit: Int,
    @SerialName("goldItems")
    internal val goldItems: Map<Int, Int>,
    @SerialName("hireData")
    internal val hireData: JsonObject,
    @SerialName("humanResourceLimit")
    internal val humanResourceLimit: Int,
    @SerialName("initMaxLabor")
    internal val initMaxLabor: Int,
    //
    @SerialName("laborAssistUnlockLevel")
    internal val laborAssistUnlockLevel: Int,
    @SerialName("laborRecoverTime")
    internal val laborRecoverTime: Int,
    //
    @SerialName("layouts")
    internal val layouts: Map<String, JsonObject>,
    @SerialName("manpowerDisplayFactor")
    internal val manpowerDisplayFactor: Int,
    // manufacture
    @SerialName("manufactData")
    internal val manufactureData: JsonObject,
    @SerialName("manufactFormulas")
    internal val manufactureFormulas: Map<Int, JsonObject>,
    @SerialName("manufactInputCapacity")
    internal val manufactureInputCapacity: Int,
    @SerialName("manufactLaborCostUnit")
    internal val manufactureLaborCostUnit: Int,
    @SerialName("manufactManpowerCostByNum")
    internal val manufactureManpowerCostByNum: List<Int>,
    @SerialName("manufactReduceTimeUnit")
    internal val manufactureReduceTimeUnit: Int,
    @SerialName("manufactStationBuff")
    internal val manufactureStationBuff: Double,
    @SerialName("manufactUnlockTips")
    internal val manufactureUnlockTips: String,
    // meeting
    @SerialName("meetingData")
    internal val meetingData: JsonObject,
    @SerialName("meetingSlotId")
    internal val meetingSlotId: String,
    //
    @SerialName("powerData")
    internal val powerData: JsonObject,
    @SerialName("prefabs")
    internal val prefabs: Map<String, JsonObject>,
    @SerialName("processedCountRatio")
    internal val processedCountRatio: Double,
    // room
    @SerialName("rooms")
    internal val rooms: Map<RoomType, JsonObject>,
    @SerialName("roomUnlockConds")
    internal val roomUnlockConditions: Map<String, JsonObject>,
    // shop
    @SerialName("shopCounterCapacity")
    internal val shopCounterCapacity: Int,
    @SerialName("shopData")
    internal val shopData: JsonObject,
    @SerialName("shopFormulas")
    internal val shopFormulas: Map<Int, JsonObject>,
    @SerialName("shopOutputRatio")
    internal val shopOutputRatio: Double?,
    @SerialName("shopStackRatio")
    internal val shopStackRatio: Double?,
    @SerialName("shopUnlockTips")
    internal val shopUnlockTips: String,
    // social
    @SerialName("socialResourceLimit")
    internal val socialResourceLimit: Int,
    @SerialName("socialSlotNum")
    internal val socialSlotNum: Int,
    //
    @SerialName("tiredApThreshold")
    internal val tiredApThreshold: Int,
    // trading
    @SerialName("tradingData")
    internal val tradingData: JsonObject,
    @SerialName("tradingLaborCostUnit")
    internal val tradingLaborCostUnit: Int,
    @SerialName("tradingManpowerCostByNum")
    internal val tradingManpowerCostByNum: List<Int>,
    @SerialName("tradingReduceTimeUnit")
    internal val tradingReduceTimeUnit: Int,
    @SerialName("tradingStrategyUnlockLevel")
    internal val tradingStrategyUnlockLevel: Int,
    //
    @SerialName("trainingBonusMax")
    internal val trainingBonusMax: Int,
    @SerialName("trainingData")
    internal val trainingData: JsonObject,
    // workshop
    @SerialName("workshopBonus")
    internal val workshopBonus: Map<String, List<String>>,
    @SerialName("workshopData")
    internal val workshopData: JsonObject,
    @SerialName("workshopFormulas")
    internal val workshopFormulas: Map<Int, JsonObject>,
)

@Serializable
public data class CharacterBuildingInfo(
    @SerialName("buffChar")
    val buffs: List<BuffChar>,
    @SerialName("charId")
    override val character: String,
    @SerialName("maxManpower")
    val maxManpower: Int
) : CharacterId

@Serializable
public data class BuffChar(
    @SerialName("buffData")
    val data: List<BuffData>
)

@Serializable
public data class BuffData(
    @SerialName("buffId")
    override val buff: String,
    @SerialName("cond")
    val condition: UnlockCondition
) : BuffId

@Serializable
public data class BuildingBuff(
    @SerialName("buffCategory")
    val category: String,
    @SerialName("buffColor")
    val color: String,
    @SerialName("description")
    val description: String,
    @SerialName("buffIcon")
    val icon: String,
    @SerialName("buffId")
    override val id: String,
    @SerialName("buffName")
    override val name: String,
    @SerialName("roomType")
    val roomType: RoomType,
    @SerialName("skillIcon")
    val skillIcon: String,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("textColor")
    val textColor: String
) : Id, Name

@Serializable
public enum class RoomType {
    CONTROL,
    CORRIDOR,
    DORMITORY,
    ELEVATOR,
    HIRE,
    MANUFACTURE,
    MEETING,
    POWER,
    TRADING,
    TRAINING,
    WORKSHOP
}