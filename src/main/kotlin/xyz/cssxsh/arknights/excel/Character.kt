package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

typealias CharacterTable = Map<String, Character>

/**
 * @see Character.group
 */
fun Collection<Character>.group(id: String) = filter { it.group == id }.toSet()

/**
 * @see Character.nation
 */
fun Collection<Character>.nation(id: String) = filter { it.nation == id }.toSet()

/**
 * @see Character.team
 */
fun Collection<Character>.team(id: String) = filter { it.team == id }.toSet()

/**
 * @see Character.itemObtainApproach
 */
fun Collection<Character>.obtain(approach: String) = filter { approach in it.itemObtainApproach.orEmpty() }.toSet()

/**
 * @see Character.position
 */
fun Collection<Character>.position(type: PositionType) = filter { it.position == type }.toSet()

/**
 * @see Character.profession
 */
fun Collection<Character>.professions(vararg types: ProfessionType) = filter { it.profession in types }.toSet()

/**
 * @see Character.profession
 */
fun Collection<Character>.professions(types: Collection<ProfessionType>) = filter { it.profession in types }.toSet()

/**
 * @see Character.isNotObtainable
 */
fun Collection<Character>.obtainable(able: Boolean = true) = filter { it.isNotObtainable != able }.toSet()

/**
 * @see Character.isSpecialCharacter
 */
fun Collection<Character>.special(value: Boolean = true) = filter { it.isSpecialCharacter == value }.toSet()

/**
 * @see Character.maxPotentialLevel
 */
fun Collection<Character>.potential(level: Int) = filter { it.maxPotentialLevel == level }.toSet()

/**
 * @see Character.canUseGeneralPotentialItem
 */
fun Collection<Character>.general(able: Boolean = true) = filter { it.canUseGeneralPotentialItem == able }.toSet()

/**
 * @see Character.rarity
 */
fun Collection<Character>.rarities(levels: IntRange) = filter { it.rarity in levels }.toSet()

/**
 * @see Character.rarity
 */
fun Collection<Character>.rarities(vararg levels: Int) = filter { it.rarity in levels }.toSet()

/**
 * @see Character.rarity
 */
fun Collection<Character>.rarities(levels: Collection<Int>) = filter { it.rarity in levels }.toSet()

/**
 * @see Character.tags
 */
fun Collection<Character>.tags(vararg words: String) = tags(words.toSet()).toSet()

/**
 * @see Character.tags
 */
fun Collection<Character>.tags(words: Collection<String>) = filter { it.tags.orEmpty().containsAll(words) }.toSet()

/**
 * @see Character.name
 */
fun Collection<Character>.names(vararg names: String) = filter { it.name in names }.toSet()

/**
 * @see Character.name
 */
fun Collection<Character>.names(names: Collection<String>) = filter { it.name in names }.toSet()

/**
 * ????????????TAG????????????
 */
fun Collection<Character>.filter(word: String): Set<Character> {
    return when (word) {
        "??????????????????" -> rarities(5)
        "????????????" -> rarities(4)
        "????????????" -> rarities(0)
        "?????????" -> position(PositionType.MELEE)
        "?????????" -> position(PositionType.RANGED)
        "????????????" -> professions(ProfessionType.PIONEER)
        "????????????" -> professions(ProfessionType.SNIPER)
        "????????????" -> professions(ProfessionType.WARRIOR)
        "????????????" -> professions(ProfessionType.CASTER)
        "????????????" -> professions(ProfessionType.TANK)
        "????????????" -> professions(ProfessionType.MEDIC)
        "????????????" -> professions(ProfessionType.SPECIAL)
        "????????????" -> professions(ProfessionType.SUPPORT)
        else -> tags(word)
    }
}

/**
 * ??????????????????
 */
fun CharacterTable.name() = values.map { it.name }.toSet()

/**
 * ??????????????????
 */
fun Character.talents() = talents.orEmpty().flatMap { talent ->
    talent.candidates.orEmpty().mapNotNull { it.name?.trim() }.toSet()
}

/**
 * ??????
 */
val Character.star get() = (0..rarity).map { '*' }.toString()

@Serializable
data class Character(
    /**
     * ???????????????
     */
    @SerialName("appellation")
    override val appellation: String,
    /**
     * ????????????????????????
     */
    @SerialName("canUseGeneralPotentialItem")
    val canUseGeneralPotentialItem: Boolean,
    /**
     * ??????
     */
    @SerialName("description")
    val description: String?,
    /**
     * ??????
     */
    @SerialName("displayNumber")
    override val displayNumber: String?,
    /**
     * ?????? [blacksteel, karlan, sweep, rhine, penguin, lgd, glasgow, abyssal, siesta, babel, elite, sui]
     */
    @SerialName("groupId")
    override val group: String?,
    /**
     * ?????????????????????
     */
    @SerialName("isNotObtainable")
    val isNotObtainable: Boolean,
    /**
     * ?????????????????????
     */
    @SerialName("isSpChar")
    val isSpecialCharacter: Boolean,
    /**
     * ????????????
     */
    @SerialName("itemDesc")
    val itemDescription: String?,
    /**
     * ???????????? [????????????, ???????????????, ????????????, ????????????, ???????????????, ???????????????????????????, ????????????, ????????????]
     */
    @SerialName("itemObtainApproach")
    val itemObtainApproach: String?,
    /**
     * ????????????
     */
    @SerialName("itemUsage")
    val itemUsage: String?,
    /**
     * ???????????? [0, 1, 5]
     */
    @SerialName("maxPotentialLevel")
    val maxPotentialLevel: Int,
    /**
     * ??????
     */
    @SerialName("name")
    override val name: String,
    /**
     * ???????????? [rhodes, columbia, laterano, victoria, sami, kazimierz, siracusa, higashi, kjerag, sargon, lungmen, yan, ursus, egir, leithanien, rim, iberia]
     */
    @SerialName("nationId")
    override val nation: String?,
    /**
     * ?????? XXX
     */
    @SerialName("phases")
    val phases: List<JsonObject>,
    /**
     * ??????
     */
    @SerialName("position")
    val position: PositionType,
    /**
     * ??????
     */
    @SerialName("profession")
    val profession: ProfessionType,
    /**
     * ????????? + 1 == ??????
     */
    @SerialName("rarity")
    val rarity: Int,
    /**
     * ?????? XXX
     */
    @SerialName("skills")
    val skills: List<SkillInfo>,
    /**
     * ??????
     */
    @SerialName("tagList")
    override val tags: List<String>?,
    /**
     * ?????? [action4, reserve1, reserve4, reserve6, student, chiave, rainbow, followers, lee]
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
     * ??????
     */
    @SerialName("talents")
    val talents: List<Talent>?,
    /**
     * ?????? Key
     */
    @SerialName("tokenKey")
    private val tokenKey: String?,
    /**
     * ??????
     */
    @SerialName("trait")
    private val trait: JsonObject?
) : Role, TagInfo

enum class ProfessionType(val text: String) {
    /**
     * ??????
     */
    PIONEER("??????"),

    /**
     * ??????
     */
    SNIPER("??????"),

    /**
     * ??????
     */
    WARRIOR("??????"),

    /**
     * ??????
     */
    CASTER("??????"),

    /**
     * ??????
     */
    TANK("??????"),

    /**
     * ??????
     */
    MEDIC("??????"),

    /**
     * ??????
     */
    SPECIAL("??????"),

    /**
     * ??????
     */
    SUPPORT("??????"),

    /**
     * ??????
     */
    TOKEN("??????"),

    /**
     * ??????
     */
    TRAP("??????");

    companion object {

        val NORMALS = listOf(PIONEER, SNIPER, WARRIOR, CASTER, TANK, MEDIC, SPECIAL, SUPPORT)

        val SPECIALS = listOf(TOKEN, TRAP)
    }
}

enum class PositionType(val text: String) {
    /**
     * ?????????
     */
    RANGED("??????"),

    /**
     * ?????????
     */
    MELEE("??????"),

    /**
     * ?????????
     */
    ALL("??????"),

    /**
     * ???
     */
    NONE("???");
}

@Serializable
data class Talent(
    @SerialName("candidates")
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
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
data class SkillInfo(
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
data class LevelUpCostCond(
    @SerialName("levelUpCost")
    val levelUpCost: List<LegacyItem>?,
    @SerialName("lvlUpTime")
    val levelUpTime: Int,
    @SerialName("unlockCond")
    val unlockCond: UnlockCondition
)