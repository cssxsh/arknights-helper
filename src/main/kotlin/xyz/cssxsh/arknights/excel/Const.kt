package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ConstInfo(
    @SerialName("advancedGachaCrystalCost")
    private val advancedGachaCrystalCost: Int,
    @SerialName("apBuyCost")
    private val apBuyCost: Int,
    @SerialName("apBuyThreshold")
    private val apBuyThreshold: Int,
    @SerialName("assistBeUsedSocialPt")
    private val assistBeUsedSocialPt: Map<Int,Int>,
    @SerialName("attackMax")
    private val attackMax: Double,
    @SerialName("baseMaxFriendNum")
    private val baseMaxFriendNum: Int,
    @SerialName("buyApTimeNoLimitFlag")
    private val buyApTimeNoLimitFlag: Boolean,
    @SerialName("charAssistRefreshTime")
    private val charAssistRefreshTime: List<CharAssistRefreshTime>,
    @SerialName("characterExpMap")
    val characterExpMap: List<List<Int>>,
    @SerialName("characterUpgradeCostMap")
    val characterUpgradeCostMap: List<List<Int>>,
    @SerialName("commonPotentialLvlUpCount")
    private val commonPotentialLvlUpCount: Int,
    @SerialName("completeCrystalBonus")
    private val completeCrystalBonus: Int,
    @SerialName("completeGainBonus")
    private val completeGainBonus: Double,
    @SerialName("creditLimit")
    private val creditLimit: Int,
    @SerialName("crisisUnlockStage")
    private val crisisUnlockStage: String,
    @SerialName("defCDPrimColor")
    private val defCDPrimColor: String,
    @SerialName("defCDSecColor")
    private val defCDSecColor: String,
    @SerialName("defMax")
    private val defMax: Double,
    @SerialName("diamondMaterialToShardExchangeRatio")
    private val diamondMaterialToShardExchangeRatio: Int,
    @SerialName("diamondToShdRate")
    private val diamondToShdRate: Int,
    @SerialName("evolveGoldCost")
    private val evolveGoldCost: List<List<Int>>,
    @SerialName("friendAssistRarityLimit")
    private val friendAssistRarityLimit: List<Int>,
    @SerialName("hardDiamondDrop")
    private val hardDiamondDrop: Int,
    @SerialName("hpMax")
    private val hpMax: Double,
    @SerialName("initCampaignTotalFee")
    private val initCampaignTotalFee: Int,
    @SerialName("initCharIdList")
    private val initCharIdList: List<String>,
    @SerialName("initPlayerDiamondShard")
    private val initPlayerDiamondShard: Int,
    @SerialName("initPlayerGold")
    private val initPlayerGold: Int,
    @SerialName("initRecruitTagList")
    private val initRecruitTagList: List<Int>,
    @SerialName("instFinDmdShdCost")
    private val instFinDmdShdCost: Int,
    @SerialName("isDynIllustEnabled")
    private val isDynIllustEnabled: Boolean,
    @SerialName("isLMGTSEnabled")
    private val isLMGTSEnabled: Boolean,
    @SerialName("lMTGSDescConstOne")
    private val lMTGSDescConstOne: String,
    @SerialName("lMTGSDescConstTwo")
    private val lMTGSDescConstTwo: String,
    @SerialName("LMTGSToEPGSRatio")
    private val lMTGSToEPGSRatio: Int,
    @SerialName("legacyItemList")
    private val legacyItemList: List<LegacyItem>,
    @SerialName("legacyTime")
    private val legacyTime: Int,
    @SerialName("mailBannerType")
    private val mailBannerType: List<String>,
    @SerialName("maxLevel")
    val maxLevel: List<List<Int>>,
    @SerialName("maxPlayerLevel")
    val maxPlayerLevel: Int,
    @SerialName("maxPracticeTicket")
    val maxPracticeTicket: Int,
    @SerialName("monthlySubRemainTimeLimitDays")
    private val monthlySubRemainTimeLimitDays: Int,
    @SerialName("monthlySubWarningTime")
    private val monthlySubWarningTime: Int,
    @SerialName("multiInComeByRank")
    private val multiInComeByRank: List<String>,
    @SerialName("newBeeGiftEPGS")
    private val newBeeGiftEPGS: Int,
    @SerialName("normalGachaUnlockPrice")
    private val normalGachaUnlockPrice: List<Int>,
    @SerialName("normalRecruitLockedString")
    private val normalRecruitLockedString: List<String>,
    @SerialName("playerApMap")
    val playerApMap: List<Int>,
    @SerialName("playerApRegenSpeed")
    val playerApRegenSpeed: Int,
    @SerialName("playerExpMap")
    val playerExpMap: List<Int>,
    @SerialName("pullForceZeroIndex")
    private val pullForceZeroIndex: Int,
    @SerialName("pullForces")
    private val pullForces: List<Double>,
    @SerialName("pushForceZeroIndex")
    private val pushForceZeroIndex: Int,
    @SerialName("pushForces")
    private val pushForces: List<Double>,
    @SerialName("reMax")
    private val reMax: Double,
    @SerialName("recruitPoolVersion")
    private val recruitPoolVersion: Int,
    @SerialName("replicateShopStartTime")
    private val replicateShopStartTime: Int,
    @SerialName("requestSameFriendCD")
    private val requestSameFriendCD: Int,
    @SerialName("richTextStyles")
    private val richTextStyles: Map<String, String>,
    @SerialName("TSO")
    private val TSO: Int,
    @SerialName("termDescriptionDict")
    private val termDescriptionDict: Map<String, JsonObject>,
    @SerialName("UnlimitSkinOutOfTime")
    private val unlimitSkinOutOfTime: Int,
    @SerialName("useAssistSocialPt")
    private val useAssistSocialPt: Int,
    @SerialName("useAssistSocialPtMaxCount")
    private val useAssistSocialPtMaxCount: Int,
    @SerialName("v006RecruitTimeStep1Refresh")
    private val v006RecruitTimeStep1Refresh: Int,
    @SerialName("v006RecruitTimeStep2Check")
    private val v006RecruitTimeStep2Check: Int,
    @SerialName("v006RecruitTimeStep2Flush")
    private val v006RecruitTimeStep2Flush: Int,
    @SerialName("voucherDiv")
    private val voucherDiv: Int,
    @SerialName("voucherSkinDesc")
    private val voucherSkinDesc: String,
    @SerialName("voucherSkinRedeem")
    private val voucherSkinRedeem: Int,
    @SerialName("weeklyOverrideDesc")
    private val weeklyOverrideDesc: String
)

/**
 * XXX
 */
@Serializable
data class CharAssistRefreshTime(
    @SerialName("Hour")
    val hour: Int,
    @SerialName("Minute")
    val minute: Int
)