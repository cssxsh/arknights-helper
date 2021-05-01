package xyz.cssxsh.arknights.excel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import java.io.File

const val CHARACTER = "character_table.json"

fun File.character(): CharacterTable = read(name = CHARACTER, type = GameDataType.EXCEL)

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
 * XXX
 */
fun Collection<Character>.filter(word: String): Set<Character> {
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
 * XXX
 */
fun CharacterTable.name() = values.map { it.name }.toSet()

@Serializable
data class Character(
    /**
     * 程序中名称
     */
    @SerialName("appellation")
    val appellation: String,
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
    val displayNumber: String?,
    /**
     * 势力 [blacksteel, karlan, sweep, rhine, penguin, lgd, glasgow, abyssal, siesta, babel, elite, sui]
     */
    @SerialName("groupId")
    val group: String?,
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
    val name: String,
    /**
     * 国家代号 [rhodes, columbia, laterano, victoria, sami, kazimierz, siracusa, higashi, kjerag, sargon, lungmen, yan, ursus, egir, leithanien, rim, iberia]
     */
    @SerialName("nationId")
    val nation: String?,
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
     * 技能 XXX
     */
    @SerialName("skills")
    val skills: List<JsonObject>,
    /**
     * 标签
     */
    @SerialName("tagList")
    val tags: List<String>?,
    /**
     * 团队 [action4, reserve1, reserve4, reserve6, student, chiave, rainbow, followers, lee]
     */
    @SerialName("teamId")
    val team: String?,
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
     * XXX
     */
    @SerialName("talents")
    private val talents: List<JsonObject>?,
    /**
     * XXX
     */
    @SerialName("tokenKey")
    private val tokenKey: String?,
    /**
     * 特质
     */
    @SerialName("trait")
    private val trait: JsonObject?
)

enum class ProfessionType {
    /**
     * 先锋
     */
    PIONEER,

    /**
     * 狙击
     */
    SNIPER,

    /**
     * 近卫
     */
    WARRIOR,

    /**
     * 术师
     */
    CASTER,

    /**
     * 重装
     */
    TANK,

    /**
     * 医疗
     */
    MEDIC,

    /**
     * 特种
     */
    SPECIAL,

    /**
     * 辅助
     */
    SUPPORT,

    /**
     * 替身
     */
    TOKEN,

    /**
     * 装置
     */
    TRAP;

    companion object {

        val NORMALS = listOf(PIONEER, SNIPER, WARRIOR, CASTER, TANK, MEDIC, SPECIAL, SUPPORT)

        val SPECIALS = listOf(TOKEN, TRAP)
    }
}

enum class PositionType {
    /**
     * 远程位
     */
    RANGED,

    /**
     * 近战位
     */
    MELEE,

    /**
     * 全场位
     */
    ALL,

    /**
     * 无
     */
    NONE
}

