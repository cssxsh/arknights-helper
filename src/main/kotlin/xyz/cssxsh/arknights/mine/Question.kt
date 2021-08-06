package xyz.cssxsh.arknights.mine

import kotlinx.serialization.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import java.time.*
import java.time.format.*

@Serializable
data class Question(
    @SerialName("problem")
    val problem: String,
    @SerialName("options")
    val options: Map<Char, String>,
    @SerialName("answer")
    val answer: Set<Char>,
    @SerialName("tips")
    val tips: String? = null,
    @SerialName("coin")
    val coin: Int,
    @SerialName("timeout")
    val timeout: Long,
    @SerialName("type")
    val type: QuestionType
)

enum class QuestionType(val description: String, private val load: (QuestionDataLoader) -> QuestionBuild) {
    BUILDING("基建相关", { randomBuildingQuestion(it.excel().buffs) }),
    PLAYER("玩家相关", { randomPlayerQuestion(it.excel().const) }),
    TALENT("天赋相关", { randomTalentQuestion(it.excel().characters) }),
    POSITION("位置相关", { randomPositionQuestion(it.excel().characters) }),
    PROFESSION("职业相关", { randomProfessionQuestion(it.excel().characters) }),
    RARITY("星级相关", { randomRarityQuestion(it.excel().characters) }),
    POWER("政权相关", { randomPowerQuestion(it.excel().powers) }),
    ILLUST("立绘相关", { randomIllustQuestion(it.excel().handbooks) }),
    VOICE("声优相关", { randomCharacterVoiceQuestion(it.excel().handbooks) }),
    INFO("档案相关", { randomInfoQuestion(it.excel().handbooks) }),
    SKILL("技能相关", { randomSkillQuestion(it.excel().skills) }),
    STORY("剧情相关", { randomStoryQuestion(it.excel().stories) }),
    ENEMY("敌方相关", { randomEnemyInfoQuestion(it.excel().enemies) }),
    WEEKLY("周常相关", { randomWeeklyQuestion(it.excel().weeks) }),
    MUSIC("音乐相关", { randomMusicQuestion(it.video().music) }),
    OTHER("自选相关", { requireNotNull(it.others().values.randomOrNull()) { "题目集为空" } });

    fun random(loader: QuestionDataLoader): Question = load(loader).build(this)
}

private fun Boolean.Companion.random() = listOf(true, false).random()

private val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")

private val prefix get() = listOf(-1L, 1L).random()

private fun OffsetDateTime.randomDays(offset: Int = 1) = plusDays(offset * prefix)

private fun OffsetDateTime.randomMinutes(offset: Int = 30) = plusMinutes(offset * prefix)

private val DefaultChoiceRange = 'A'..'D'

data class QuestionDataLoader(
    val excel: () -> ExcelData,
    val video: () -> VideoData,
    val others: () -> Map<String, CustomQuestion>,
)

sealed class QuestionBuild {
    abstract fun build(type: QuestionType): Question
}

data class ChoiceQuestion(
    val meaning: Pair<String, String>,
    val range: CharRange,
    val relation: () -> List<Pair<String, String>>
) : QuestionBuild() {
    override fun build(type: QuestionType): Question {
        val reversal = Boolean.random()
        val key = { pair: Pair<String, String> -> if (reversal) pair.second else pair.first }
        val value = { pair: Pair<String, String> -> if (reversal) pair.first else pair.second }

        val relation = relation().toMutableList().apply { shuffle() }
        val map = relation.groupBy(key, value)

        val options = (range zip map.entries).toMap()
        val random = options.values.flatMap { it.value }.random()
        val answer = options.filter { (_, list) -> random in list.value }.keys
        val problem = "下列选项中" + (if (reversal) "%1\$s[%3\$s]的%2\$s是" else "有%2\$s[%3\$s]的%1\$s是")
        return Question(
            problem = problem.format(meaning.first, meaning.second, random),
            options = options.mapValues { (_, entry) -> entry.key },
            answer = answer,
            coin = options.size * 100,
            timeout = options.size * 10_000L,
            type = type
        )
    }
}

@Serializable
data class CustomQuestion(
    @SerialName("problem")
    val problem: String,
    @SerialName("options")
    val options: Map<String, Boolean>,
    @SerialName("coin")
    val coin: Int,
    @SerialName("tips")
    val tips: String,
    @SerialName("timeout")
    val timeout: Long
) : QuestionBuild() {
    constructor(problem: String, right: List<String>, error: List<String>, coin: Int, tips: String, timeout: Long): this(
        problem = problem,
        options = right.associateWith { true } + error.associateWith { false },
        coin = coin,
        tips = tips,
        timeout = timeout
    )

    override fun build(type: QuestionType): Question {
        val map = (('A'..'Z') zip options.entries.toMutableList().apply { shuffle() }).toMap()
        return Question(
            problem = problem,
            options = map.mapValues { (_, entry) -> entry.key },
            answer = map.filter { (_, entry) -> entry.value }.keys,
            coin = coin,
            tips = tips,
            timeout = timeout,
            type = type
        )
    }
}

data class DateTimeQuestion(
    @SerialName("problem")
    val problem: String,
    @SerialName("datetime")
    val datetime: OffsetDateTime
) : QuestionBuild() {
    override fun build(type: QuestionType): Question {
        val range = DefaultChoiceRange
        val list = listOf(
            datetime,
            datetime.randomDays(),
            datetime.randomMinutes(),
            datetime.randomDays().randomMinutes()
        )
        val map = (range zip list).toMap()
        val answer = map.entries.filter { it.value == datetime }.map { it.key }.toSet()
        return Question(
            problem = problem,
            options = map.mapValues { (_, datetime) -> datetime.format(formatter) },
            answer = answer,
            coin = 1800,
            timeout = 3 * 60_000L,
            type = type
        )
    }
}

data class JudgmentQuestion(val generate: (Boolean) -> Pair<String, String>) : QuestionBuild() {
    override fun build(type: QuestionType): Question {
        val state = Boolean.random()
        val (problem, tips) = generate(state)
        return Question(
            problem = problem,
            options = mapOf('Y' to "对", 'N' to "错"),
            answer = setOf(if (state) 'Y' else 'N'),
            coin = 600,
            tips = tips,
            timeout = 60_000L,
            type = type
        )
    }
}

typealias ConstInfoQuestion = (const: ConstInfo) -> QuestionBuild

typealias BuffQuestion = (buffs: BuffMap) -> QuestionBuild

typealias CharacterQuestion = (characters: CharacterMap) -> QuestionBuild

typealias HandbookQuestion = (handbooks: HandbookMap) -> QuestionBuild

typealias SkillQuestion = (skills: SkillMap) -> QuestionBuild

typealias VideoQuestion = (videos: Collection<Video>) -> QuestionBuild

typealias PowerQuestion = (teams: PowerMap) -> QuestionBuild

typealias StoryQuestion = (stories: StoryMap) -> QuestionBuild

typealias EnemyQuestion = (enemies: EnemyMap) -> QuestionBuild

typealias WeeklyQuestion = (zones: WeeklyMap) -> QuestionBuild

val randomPlayerQuestion: ConstInfoQuestion = { const ->
    val list = buildMap<String, Pair<Int, Set<Int>>> {
        val speed = const.playerApRegenSpeed
        put("理智回复速度是每%d分钟1理智", speed to ((1..10).toSet() - speed))
        val level = (1..const.maxPlayerLevel).random()
        val ap = const.playerApMap[level - 1]
        val exp = const.playerExpMap[level - 1]
        put("等级为${level}, 理智回复上限为%d", ap to (const.playerApMap.toSet() - ap))
        put("等级为${level}, 升级所需经验为%d", exp to (const.playerExpMap.toSet() - exp))
    }

    JudgmentQuestion { state ->
        list.entries.random().let { (text, param) ->
            text.format(if (state) param.first else param.second.random()) to param.first.toString()
        }
    }
}

val randomBuildingQuestion: BuffQuestion = { buffs ->
    ChoiceQuestion(meaning = "角色" to "基建技能", range = DefaultChoiceRange) {
        buffs.flatMap { (name, list) ->
            list.map { buff -> name to buff.name }
        }
    }
}

val randomTalentQuestion: CharacterQuestion = { characters ->
    ChoiceQuestion(meaning = "角色" to "天赋", range = DefaultChoiceRange) {
        characters.flatMap { (name, character) ->
            character.talents().map { talent -> name to talent }
        }
    }
}

val randomPositionQuestion: CharacterQuestion = { characters ->
    ChoiceQuestion(meaning = "角色" to "放置位", range = DefaultChoiceRange) {
        characters.map { (name, character) -> name to  character.position.text }
    }
}

val randomProfessionQuestion: CharacterQuestion = { characters ->
    ChoiceQuestion(meaning = "角色" to "职业", range = DefaultChoiceRange) {
        characters.map { (name, character) -> name to character.profession.text }
    }
}

val randomRarityQuestion: CharacterQuestion = { characters ->
    ChoiceQuestion(meaning = "角色" to "稀有度", range = DefaultChoiceRange) {
        characters.map { (name, character) -> name to (character.rarity + 1).toString() }
    }
}

val randomTagQuestion: CharacterQuestion = { characters ->
    ChoiceQuestion(meaning = "角色" to "TAG", range = DefaultChoiceRange) {
        characters.flatMap { (name, character) ->
            character.tags.orEmpty().map { tag -> name to tag }
        }
    }
}

val randomPowerQuestion: PowerQuestion = { teams ->
    val level = PowerLevel.values().random()
    ChoiceQuestion(meaning = level.text to "角色", range = DefaultChoiceRange) {
        teams.getValue(level).filterKeys { it.id != DefaultTeam }.flatMap { (team, characters) ->
            characters.map { name -> team.name to name }
        }
    }
}

val randomIllustQuestion: HandbookQuestion = { handbooks ->
    ChoiceQuestion(meaning = "画师" to "角色", range = DefaultChoiceRange) {
        handbooks.map { (name, handbook) -> handbook.illust to name }
    }
}

val randomCharacterVoiceQuestion: HandbookQuestion = { handbooks ->
    ChoiceQuestion(meaning = "声优" to "角色", range = DefaultChoiceRange) {
        handbooks.map { (name, handbook) -> handbook.voice to name }
    }
}

val randomInfoQuestion: HandbookQuestion = { handbooks ->
    val tag = handbooks.tags().random()
    ChoiceQuestion(meaning = "角色" to tag, range = DefaultChoiceRange) {
        handbooks.mapNotNull { (name, handbook) ->
            handbook.infos()[tag]?.let { value -> name to value }
        }
    }
}

val randomSkillQuestion: SkillQuestion = { skills ->
    ChoiceQuestion(meaning = "角色" to "技能", range = DefaultChoiceRange) {
        skills.flatMap { (name, list) ->
            list.map { skill -> name to skill.levels.first().name }
        }
    }
}

val randomMusicQuestion: VideoQuestion = { videos ->
    val video = videos.random()
    val problem = "${video.title}(${video.bvid})发布于"
    DateTimeQuestion(problem = problem, datetime = video.created)
}

val randomStoryQuestion: StoryQuestion = { stories ->
    val story = (stories - ActionType.NONE).values.flatten().random()
    val problem = "${story.action.text}<${story.name}>开始于"
    DateTimeQuestion(problem = problem, datetime = story.start)
}

val randomEnemyInfoQuestion: EnemyQuestion = { enemies ->
    val (attribute, value) = listOf<Pair<String, Enemy.() -> String>>(
        "攻击方式" to { type },
        "攻击力" to { attack },
        "防御力" to { defence },
        "法术抗性" to { resistance },
        "耐久" to { endure }
    ).random()
    ChoiceQuestion(meaning = "敌方" to attribute , range = DefaultChoiceRange) {
        enemies.flatMap { (_, enemies) ->
            enemies.map { enemy ->
                enemy.designation to enemy.value()
            }
        }
    }
}

val randomWeeklyQuestion: WeeklyQuestion = { weeks ->
    ChoiceQuestion(meaning = "周常" to "开启时间", range = DefaultChoiceRange) {
        weeks.flatMap { (_, list) ->
            list.map { (zone, weekly) ->
                zone.title to weekly.daysOfWeek.joinToString()
            }
        }
    }
}