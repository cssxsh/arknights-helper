package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class ConstInfo(
    @SerialName("characterExpMap")
    val characterExpMap: List<List<Int>>,
    @SerialName("characterUpgradeCostMap")
    val characterUpgradeCostMap: List<List<Int>>,
    @SerialName("maxLevel")
    val maxLevel: List<List<Int>>,
    @SerialName("maxPlayerLevel")
    val maxPlayerLevel: Int,
    @SerialName("maxPracticeTicket")
    val maxPracticeTicket: Int,
    @SerialName("playerApMap")
    val playerApMap: List<Int>,
    @SerialName("playerApRegenSpeed")
    val playerApRegenSpeed: Int,
    @SerialName("playerExpMap")
    val playerExpMap: List<Int>,
    @SerialName("dataVersion")
    val dataVersion: String,
    @SerialName("resPrefVersion")
    val resPrefVersion: String,
    @SerialName("announceWebBusType")
    val announceWebBusType: String,
    @SerialName("subProfessionDamageTypePairs")
    internal val subProfessionDamageTypePairs: Map<String, ProfessionDamageType>,
    @SerialName("advancedGachaCrystalCost")
    internal val advancedGachaCrystalCost: Int,
    @SerialName("addedRewardDisplayZone")
    internal val addedRewardDisplayZone: String,
    @SerialName("apBuyCost")
    internal val apBuyCost: Int,
    @SerialName("apBuyThreshold")
    internal val apBuyThreshold: Int,
    @SerialName("assistBeUsedSocialPt")
    internal val assistBeUsedSocialPt: Map<Int, Int>,
    @SerialName("attackMax")
    internal val attackMax: Double,
    @SerialName("baseMaxFriendNum")
    internal val baseMaxFriendNum: Int,
    @SerialName("buyApTimeNoLimitFlag")
    internal val buyApTimeNoLimitFlag: Boolean,
    @SerialName("charAssistRefreshTime")
    internal val charAssistRefreshTime: List<CharAssistRefreshTime>,
    @SerialName("commonPotentialLvlUpCount")
    internal val commonPotentialLvlUpCount: Int,
    @SerialName("completeCrystalBonus")
    internal val completeCrystalBonus: Int,
    @SerialName("completeGainBonus")
    internal val completeGainBonus: Double,
    @SerialName("creditLimit")
    internal val creditLimit: Int,
    @SerialName("crisisUnlockStage")
    internal val crisisUnlockStage: String,
    @SerialName("charmEquipCount")
    internal val charmEquipCount: Int,
    // def
    @SerialName("defCDPrimColor")
    internal val defCDPrimColor: String,
    @SerialName("defCDSecColor")
    internal val defCDSecColor: String,
    @SerialName("defMax")
    internal val defMax: Double,
    // diamond
    @SerialName("diamondMaterialToShardExchangeRatio")
    internal val diamondMaterialToShardExchangeRatio: Int,
    @SerialName("diamondToShdRate")
    internal val diamondToShdRate: Int,
    //
    @SerialName("easyCrystalBonus")
    internal val easyCrystalBonus: Int,
    @SerialName("evolveGoldCost")
    internal val evolveGoldCost: List<List<Int>>,
    @SerialName("friendAssistRarityLimit")
    internal val friendAssistRarityLimit: List<Int>,
    @SerialName("hardDiamondDrop")
    internal val hardDiamondDrop: Int,
    @SerialName("hpMax")
    internal val hpMax: Double,
    // init
    @SerialName("initCampaignTotalFee")
    internal val initCampaignTotalFee: Int,
    @SerialName("initCharIdList")
    internal val initCharIdList: List<String>,
    @SerialName("initPlayerDiamondShard")
    internal val initPlayerDiamondShard: Int,
    @SerialName("initPlayerGold")
    internal val initPlayerGold: Int,
    @SerialName("initRecruitTagList")
    internal val initRecruitTagList: List<Int>,
    //
    @SerialName("instFinDmdShdCost")
    internal val instFinDmdShdCost: Int,
    @SerialName("isClassicQCShopEnabled")
    internal val isClassicQCShopEnabled: Boolean,
    @SerialName("isClassicPotentialItemFuncEnabled")
    internal val isClassicPotentialItemFuncEnabled: Boolean,
    @SerialName("isClassicGachaPoolFuncEnabled")
    internal val isClassicGachaPoolFuncEnabled: Boolean,
    @SerialName("isDynIllustStartEnabled")
    internal val isDynIllustStartEnabled: Boolean,
    @SerialName("isDynIllustEnabled")
    internal val isDynIllustEnabled: Boolean,
    @SerialName("isLMGTSEnabled")
    internal val isLMGTSEnabled: Boolean,
    @SerialName("isRoguelikeTopicFuncEnabled")
    internal val isRoguelikeTopicFuncEnabled: Boolean,
    @SerialName("isRoguelikeAvgAchieveFuncEnabled")
    internal val isRoguelikeAvgAchieveFuncEnabled: Boolean,
    @SerialName("isVoucherClassicItemDistinguishable")
    internal val isVoucherClassicItemDistinguishable: Boolean,
    // legacy
    @SerialName("legacyItemList")
    internal val legacyItemList: List<LegacyItem>,
    @SerialName("legacyTime")
    @Serializable(TimestampSerializer::class)
    internal val legacyTime: OffsetDateTime,
    // LMTGS
    @SerialName("lMTGSDescConstOne")
    internal val lmtgsDescriptionConstOne: String,
    @SerialName("lMTGSDescConstTwo")
    internal val lmtgsDescriptionConstTwo: String,
    @SerialName("LMTGSToEPGSRatio")
    internal val lmtgsToEPGSRatio: Double,
    //
    @SerialName("mailBannerType")
    internal val mailBannerType: List<String>,
    // mainline
    @SerialName("mainlineCompatibleDesc")
    internal val mainlineCompatibleDescription: String,
    @SerialName("mainlineEasyDesc")
    internal val mainlineEasyDescription: String,
    @SerialName("mainlineNormalDesc")
    internal val mainlineNormalDescription: String,
    @SerialName("mainlineToughDesc")
    internal val mainlineToughDescription: String,
    // monthly
    @SerialName("monthlySubRemainTimeLimitDays")
    internal val monthlySubRemainTimeLimitDays: Int,
    @SerialName("monthlySubWarningTime")
    internal val monthlySubWarningTime: Long,
    //
    @SerialName("multiInComeByRank")
    internal val multiInComeByRank: List<String>,
    @SerialName("newBeeGiftEPGS")
    internal val newBeeGiftEPGS: Int,
    @SerialName("normalGachaUnlockPrice")
    internal val normalGachaUnlockPrice: List<Int>,
    @SerialName("normalRecruitLockedString")
    internal val normalRecruitLockedString: List<String>,
    // force
    @SerialName("pullForces")
    internal val pullForces: List<Double>,
    @SerialName("pullForceZeroIndex")
    internal val pullForceZeroIndex: Int,
    @SerialName("pushForces")
    internal val pushForces: List<Double>,
    @SerialName("pushForceZeroIndex")
    internal val pushForceZeroIndex: Int,
    //
    @SerialName("recruitPoolVersion")
    internal val recruitPoolVersion: Int,
    @SerialName("rejectSpCharMission")
    @Serializable(TimestampSerializer::class)
    internal val rejectSpCharMission: OffsetDateTime,
    @SerialName("reMax")
    internal val reMax: Double,
    @SerialName("replicateShopStartTime")
    @Serializable(TimestampSerializer::class)
    internal val replicateShopStartTime: OffsetDateTime,
    @SerialName("requestSameFriendCD")
    internal val requestSameFriendCD: Int,
    @SerialName("richTextStyles")
    internal val richTextStyles: Map<String, String>,
    @SerialName("storyReviewUnlockItemLackTip")
    internal val storyReviewUnlockItemLackTip: String,
    @SerialName("termDescriptionDict")
    internal val termDescriptionDict: Map<String, TermDescription>,
    @SerialName("TSO")
    @Serializable(TimestampSerializer::class)
    internal val tso: OffsetDateTime,
    @SerialName("UnlimitSkinOutOfTime")
    internal val unlimitSkinOutOfTime: Long,
    // useAssistSocial
    @SerialName("useAssistSocialPt")
    internal val useAssistSocialPt: Int,
    @SerialName("useAssistSocialPtMaxCount")
    internal val useAssistSocialPtMaxCount: Int,
    // v006
    @SerialName("v006RecruitTimeStep1Refresh")
    @Serializable(TimestampSerializer::class)
    internal val v006RecruitTimeStep1Refresh: OffsetDateTime,
    @SerialName("v006RecruitTimeStep2Check")
    @Serializable(TimestampSerializer::class)
    internal val v006RecruitTimeStep2Check: OffsetDateTime,
    @SerialName("v006RecruitTimeStep2Flush")
    @Serializable(TimestampSerializer::class)
    internal val v006RecruitTimeStep2Flush: OffsetDateTime,
    // voucher
    @SerialName("voucherDiv")
    internal val voucherDiv: Int,
    @SerialName("voucherSkinDesc")
    internal val voucherSkinDescription: String,
    @SerialName("voucherSkinRedeem")
    internal val voucherSkinRedeem: Int,
    //
    @SerialName("weeklyOverrideDesc")
    internal val weeklyOverrideDescription: String
)

@Serializable
public data class CharAssistRefreshTime(
    @SerialName("Hour")
    val hour: Int,
    @SerialName("Minute")
    val minute: Int
)

@Serializable
public data class TermDescription(
    @SerialName("termId")
    val termId: String,
    @SerialName("termName")
    val name: String,
    @SerialName("description")
    val description: String
)

@Serializable
public enum class ProfessionDamageType(public val text: String) {
    NONE("其他"),
    PHYSICAL("物理"),
    MAGICAL("法术"),
    HEAL("治疗");

    override fun toString(): String = text
}