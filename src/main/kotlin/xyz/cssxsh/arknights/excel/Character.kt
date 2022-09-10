package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

public typealias CharacterTable = Map<String, Character>

/**
 * @see Character.group
 */
public fun Collection<Character>.group(id: String): Set<Character> = filter { it.group == id }.toSet()

/**
 * @see Character.nation
 */
public fun Collection<Character>.nation(id: String): Set<Character> = filter { it.nation == id }.toSet()

/**
 * @see Character.team
 */
public fun Collection<Character>.team(id: String): Set<Character> = filter { it.team == id }.toSet()

/**
 * @see Character.itemObtainApproach
 */
public fun Collection<Character>.obtain(approach: String): Set<Character> = filter { approach in it.itemObtainApproach.orEmpty() }.toSet()

/**
 * @see Character.position
 */
public fun Collection<Character>.position(type: PositionType): Set<Character> = filter { it.position == type }.toSet()

/**
 * @see Character.profession
 */
public fun Collection<Character>.professions(vararg types: ProfessionType): Set<Character> = filter { it.profession in types }.toSet()

/**
 * @see Character.profession
 */
public fun Collection<Character>.professions(types: Collection<ProfessionType>): Set<Character> = filter { it.profession in types }.toSet()

/**
 * @see Character.isNotObtainable
 */
public fun Collection<Character>.obtainable(able: Boolean = true): Set<Character> = filter { it.isNotObtainable != able }.toSet()

/**
 * @see Character.isSpecialCharacter
 */
public fun Collection<Character>.special(value: Boolean = true): Set<Character> = filter { it.isSpecialCharacter == value }.toSet()

/**
 * @see Character.maxPotentialLevel
 */
public fun Collection<Character>.potential(level: Int): Set<Character> = filter { it.maxPotentialLevel == level }.toSet()

/**
 * @see Character.canUseGeneralPotentialItem
 */
public fun Collection<Character>.general(able: Boolean = true): Set<Character> = filter { it.canUseGeneralPotentialItem == able }.toSet()

/**
 * @see Character.rarity
 */
public fun Collection<Character>.rarities(levels: IntRange): Set<Character> = filter { it.rarity in levels }.toSet()

/**
 * @see Character.rarity
 */
public fun Collection<Character>.rarities(vararg levels: Int): Set<Character> = filter { it.rarity in levels }.toSet()

/**
 * @see Character.rarity
 */
public fun Collection<Character>.rarities(levels: Collection<Int>): Set<Character> = filter { it.rarity in levels }.toSet()

/**
 * @see Character.tags
 */
public fun Collection<Character>.tags(vararg words: String): Set<Character> = tags(words.toSet()).toSet()

/**
 * @see Character.tags
 */
public fun Collection<Character>.tags(words: Collection<String>): Set<Character> = filter { it.tags.orEmpty().containsAll(words) }.toSet()

/**
 * @see Character.name
 */
public fun Collection<Character>.names(vararg names: String): Set<Character> = filter { it.name in names }.toSet()

/**
 * @see Character.name
 */
public fun Collection<Character>.names(names: Collection<String>): Set<Character> = filter { it.name in names }.toSet()

/**
 * 按照招募TAG过滤干员
 */
public fun Collection<Character>.filter(word: String): Set<Character> {
    return when (word) {
        "高级资深干员" -> rarities(5)
        "资深干员" -> rarities(4)
        "支援机械" -> rarities(0)
        "近战位" -> position(PositionType.MELEE)
        "远程位" -> position(PositionType.RANGED)
        "先锋干员" -> professions(ProfessionType.PIONEER)
        "狙击干员" -> professions(ProfessionType.SNIPER)
        "近卫干员" -> professions(ProfessionType.WARRIOR)
        "术师干员" -> professions(ProfessionType.CASTER)
        "重装干员" -> professions(ProfessionType.TANK)
        "医疗干员" -> professions(ProfessionType.MEDIC)
        "特种干员" -> professions(ProfessionType.SPECIAL)
        "辅助干员" -> professions(ProfessionType.SUPPORT)
        else -> tags(word)
    }
}

/**
 * 所有干员名字
 */
public fun CharacterTable.name(): Set<String> = values.map { it.name }.toSet()

/**
 * 干员全部天赋
 */
public fun Character.talents(): List<String> = talents.orEmpty().flatMap { talent ->
    talent.candidates.orEmpty().mapNotNull { it.name?.trim() }.toSet()
}

/**
 * 星级
 */
public val Character.star: String get() = (0..rarity).map { '*' }.toString()

@Serializable
public data class Character(
    /**
     * 程序中名称
     */
    @SerialName("appellation")
    override val appellation: String,
    /**
     * 能否使用通用信物
     */
    @SerialName("canUseGeneralPotentialItem")
    val canUseGeneralPotentialItem: Boolean,
    /**
     * 简介
     */
    @SerialName("description")
    val description: String?,
    /**
     * 代号
     */
    @SerialName("displayNumber")
    override val displayNumber: String?,
    /**
     * 势力 [blacksteel, karlan, sweep, rhine, penguin, lgd, glasgow, abyssal, siesta, babel, elite, sui]
     */
    @SerialName("groupId")
    override val group: String?,
    /**
     * 是否为不可获得
     */
    @SerialName("isNotObtainable")
    val isNotObtainable: Boolean,
    /**
     * 是否为特殊角色
     */
    @SerialName("isSpChar")
    val isSpecialCharacter: Boolean,
    /**
     * 招募简介
     */
    @SerialName("itemDesc")
    val itemDescription: String?,
    /**
     * 招募方法 [招募寻访, 信用交易所, 活动获得, 限时礼包, 凭证交易所, 招募寻访、见习任务, 主线剧情, 周年奖励]
     */
    @SerialName("itemObtainApproach")
    val itemObtainApproach: String?,
    /**
     * 招募发言
     */
    @SerialName("itemUsage")
    val itemUsage: String?,
    /**
     * 最大潜能 [0, 1, 5]
     */
    @SerialName("maxPotentialLevel")
    val maxPotentialLevel: Int,
    /**
     * 名称
     */
    @SerialName("name")
    override val name: String,
    /**
     * 国家代号 [rhodes, columbia, laterano, victoria, sami, kazimierz, siracusa, higashi, kjerag, sargon, lungmen, yan, ursus, egir, leithanien, rim, iberia]
     */
    @SerialName("nationId")
    override val nation: String?,
    /**
     * 阶段 XXX
     */
    @SerialName("phases")
    val phases: List<JsonObject>,
    /**
     * 范围
     */
    @SerialName("position")
    val position: PositionType,
    /**
     * 职业
     */
    @SerialName("profession")
    val profession: ProfessionType,
    /**
     * 稀有度 + 1 == 星级
     */
    @SerialName("rarity")
    val rarity: Int,
    /**
     * 技能
     */
    @SerialName("skills")
    val skills: List<SkillInfo>,
    /**
     * 标签
     */
    @SerialName("tagList")
    override val tags: List<String>?,
    /**
     * 团队 [action4, reserve1, reserve4, reserve6, student, chiave, rainbow, followers, lee]
     */
    @SerialName("teamId")
    override val team: String?,
    /**
     * XXX
     */
    @SerialName("allSkillLvlup")
    private val allSkillLvlup: List<JsonObject>,
    /**
     * XXX
     */
    @SerialName("favorKeyFrames")
    private val favorKeyFrames: List<JsonObject>?,
    /**
     * XXX
     */
    @SerialName("potentialItemId")
    private val potentialItem: String,
    /**
     * XXX
     */
    @SerialName("potentialRanks")
    private val potentialRanks: List<JsonObject>,
    /**
     * 天赋
     */
    @SerialName("talents")
    val talents: List<Talent>?,
    /**
     * 替身 Key
     */
    @SerialName("tokenKey")
    private val tokenKey: String?,
    /**
     * 特质
     */
    @SerialName("trait")
    private val trait: JsonObject?
) : Role, TagInfo

public enum class ProfessionType(public val text: String) {
    /**
     * 先锋
     */
    PIONEER("先锋"),

    /**
     * 狙击
     */
    SNIPER("狙击"),

    /**
     * 近卫
     */
    WARRIOR("近卫"),

    /**
     * 术师
     */
    CASTER("术师"),

    /**
     * 重装
     */
    TANK("重装"),

    /**
     * 医疗
     */
    MEDIC("医疗"),

    /**
     * 特种
     */
    SPECIAL("特种"),

    /**
     * 辅助
     */
    SUPPORT("辅助"),

    /**
     * 替身
     */
    TOKEN("替身"),

    /**
     * 装置
     */
    TRAP("装置");

    public companion object {

        public val NORMALS: List<ProfessionType> =
            listOf(PIONEER, SNIPER, WARRIOR, CASTER, TANK, MEDIC, SPECIAL, SUPPORT)

        public val SPECIALS: List<ProfessionType> = listOf(TOKEN, TRAP)
    }
}

public enum class PositionType(public val text: String) {
    /**
     * 远程位
     */
    RANGED("远程"),

    /**
     * 近战位
     */
    MELEE("近战"),

    /**
     * 全场位
     */
    ALL("全场"),

    /**
     * 无
     */
    NONE("无");
}

@Serializable
public data class Talent(
    @SerialName("candidates")
    val candidates: List<Candidate>? = null
)

@Serializable
public data class Candidate(
    @SerialName("blackboard")
    val blackboard: List<Blackboard>?,
    @SerialName("description")
    val description: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("prefabKey")
    val prefabKey: String?,
    @SerialName("rangeId")
    val rangeId: String?,
    @SerialName("requiredPotentialRank")
    val requiredPotentialRank: Int,
    @SerialName("unlockCondition")
    val unlockCondition: UnlockCondition?
)

@Serializable
public data class SkillInfo(
    @SerialName("levelUpCostCond")
    val levelUpCostCond: List<LevelUpCostCond>,
    @SerialName("overridePrefabKey")
    val overridePrefabKey: String?,
    @SerialName("overrideTokenKey")
    val overrideTokenKey: String?,
    @SerialName("skillId")
    override val skill: String?,
    @SerialName("unlockCond")
    val unlockCond: UnlockCondition
) : SkillId

@Serializable
public data class LevelUpCostCond(
    @SerialName("levelUpCost")
    val levelUpCost: List<LegacyItem>?,
    @SerialName("lvlUpTime")
    val levelUpTime: Int,
    @SerialName("unlockCond")
    val unlockCond: UnlockCondition
)