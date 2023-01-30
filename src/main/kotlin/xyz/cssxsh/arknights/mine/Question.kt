package xyz.cssxsh.arknights.mine

import kotlinx.coroutines.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import java.time.*
import java.time.format.*

@Serializable
public data class Question(
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

public enum class QuestionType(public val description: String) {
    BUILDING("基建相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val building = runBlocking { loader.excel.building() }
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "基建技能", range = defaultChoiceRange) {
                for ((characterId, info) in building.characters) {
                    val character = characters[characterId] ?: continue
                    for (char in info.buffs) {
                        for ((buffId) in char.data) {
                            val buff = building.buffs[buffId] ?: continue

                            add(character.name to buff.name)
                        }
                    }
                }
            }
        }
    },
    PLAYER("玩家相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val const = runBlocking { loader.excel.const() }

            val map: Map<String, Pair<Int, Set<Int>>> = buildMap {
                val speed = const.playerApRegenSpeed
                put("理智回复速度是每%d分钟1理智", speed to ((1..10).toSet() - speed))
                val level = (1..const.maxPlayerLevel).random()
                val ap = const.playerApMap[level - 1]
                val exp = const.playerExpMap[level - 1]
                put("等级为${level}, 理智回复上限为%d", ap to (const.playerApMap.toSet() - ap))
                put("等级为${level}, 升级所需经验为%d", exp to (const.playerExpMap.toSet() - exp))
            }

            return JudgmentQuestionBuilder { state ->
                val (text, param) = map.entries.random()
                text.format(if (state) param.first else param.second.random()) to param.first.toString()
            }
        }
    },
    TALENT("天赋相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "天赋", range = defaultChoiceRange) {
                for ((_, character) in characters) {
                    val talents = character.talents ?: continue
                    for (talent in talents) {
                        val candidates = talent.candidates ?: continue
                        for (candidate in candidates) {
                            val name = candidate.name ?: continue

                            add(character.name to name)
                        }
                    }
                }
            }
        }
    },
    POSITION("位置相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "放置位", range = defaultChoiceRange) {
                for ((_, character) in characters) {
                    add(character.name to character.position.text)
                }
            }
        }
    },
    PROFESSION("职业相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "职业", range = defaultChoiceRange) {
                for ((_, character) in characters) {
                    add(character.name to character.profession.text)
                }
            }
        }
    },
    RARITY("星级相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "稀有度", range = defaultChoiceRange) {
                for ((_, character) in characters) {
                    add(character.name to (character.rarity + 1).toString())
                }
            }
        }
    },
    POWER("政权相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val teams = runBlocking { loader.excel.team() }
            val characters = runBlocking { loader.excel.character() }
            val level = PowerLevel.values().random()
            return ChoiceQuestionBuilder(meaning = level.text to "角色", range = defaultChoiceRange) {
                for ((_, team) in teams) {
                    if (team.level != level.ordinal) continue
                    for ((_, character) in characters) {
                        if (level.get(character) != team.id) continue

                        add(team.name to character.name)
                    }
                }
            }
        }
    },
    ILLUST("立绘相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val skins = runBlocking { loader.excel.skin().characterSkins }
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "画师" to "角色", range = defaultChoiceRange) {
                for ((_, skin) in skins) {
                    val character = characters[skin.character] ?: continue
                    val illusts = skin.display.illusts ?: continue
                    for (drawer in illusts) {
                        add(drawer to character.name)
                    }
                }
            }
        }
    },
    VOICE("声优相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val voices = runBlocking { loader.excel.word().voiceLangDict }
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "声优" to "角色", range = defaultChoiceRange) {
                for ((characterId, info) in voices) {
                    val character = characters[characterId] ?: continue
                    for ((_, dict) in info.dict) {
                        for (voice in dict.voices) {
                            add(voice to character.name)
                        }
                    }
                }
            }
        }
    },
    SKILL("技能相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val skills = runBlocking { loader.excel.skill() }
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "技能", range = defaultChoiceRange) {
                for ((_, character) in characters) {
                    for (info in character.skills) {
                        val skillId = info.skill ?: continue
                        val skill = skills[skillId] ?: continue
                        val name = skill.levels.firstOrNull()?.name ?: continue
                        add(character.name to name)
                    }
                }
            }
        }
    },
    EQUIP("模组相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val equips = runBlocking { loader.excel.equip().equips }
            val characters = runBlocking { loader.excel.character() }
            return ChoiceQuestionBuilder(meaning = "角色" to "模组", range = defaultChoiceRange) {
                for ((_, equip) in equips) {
                    val character = characters[equip.character] ?: continue
                    add(character.name to equip.name)
                }
            }
        }
    },
    STORY("剧情相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val stories = runBlocking { loader.excel.story() }
            val story = stories.values.asSequence()
                .filter { it.action != ActionType.NONE }
                .toList()
                .random()
            val problem = "${story.action.text}<${story.name}>开始于"
            return DateTimeQuestionBuilder(problem = problem, datetime = story.start)
        }
    },
    ENEMY("敌方相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val enemies = runBlocking { loader.excel.enemy() }
            val (attribute, value) = listOf<Pair<String, Enemy.() -> String>>(
                "攻击方式" to { type },
                "攻击力" to { attack },
                "防御力" to { defence },
                "法术抗性" to { resistance },
                "耐久" to { endure }
            ).random()
            return ChoiceQuestionBuilder(meaning = "敌方" to attribute, range = defaultChoiceRange) {
                for ((_, enemy) in enemies) {
                    add(enemy.designation to enemy.value())
                }
            }
        }
    },
    WEEKLY("周常相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val table = runBlocking { loader.excel.zone() }
            return ChoiceQuestionBuilder(meaning = "周常" to "开启时间", range = defaultChoiceRange) {
                for ((zoneId, weekly) in table.weekly) {
                    val zone = table.zones[zoneId] ?: continue
                    add(zone.title to weekly.daysOfWeek.joinToString())
                }
            }
        }
    },
    MUSIC("音乐相关") {
        override fun load(loader: QuestionDataLoader): QuestionBuilder {
            val video = loader.video.cache[VideoType.MUSIC].orEmpty().random()
            val problem = "${video.title}(${video.bvid})发布于"
            return DateTimeQuestionBuilder(problem = problem, datetime = video.created)
        }
    },
    OTHER("自选相关") {
        public override fun load(loader: QuestionDataLoader): QuestionBuilder {
            return requireNotNull(loader.custom.question.values.randomOrNull()) { "题目集为空" }
        }
    };

    public fun random(loader: QuestionDataLoader): Question = load(loader).build(this)

    public abstract fun load(loader: QuestionDataLoader): QuestionBuilder
}

private fun Boolean.Companion.random() = listOf(true, false).random()

private val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")

private fun OffsetDateTime.randomDays() = plusDays((-7L..7L).minus(0).random())

private fun OffsetDateTime.randomHours() = plusHours((-6L..6L).minus(0).random())

private val defaultChoiceRange = 'A'..'D'

public interface QuestionDataLoader {
    public val excel: ExcelDataHolder
    public val video: VideoDataHolder
    public val custom: CustomQuestionHolder
}

public interface CustomQuestionHolder {
    public val question: Map<String, CustomQuestionInfo>
}

public sealed class QuestionBuilder {
    public abstract fun build(type: QuestionType): Question
}

public class ChoiceQuestionBuilder(
    public val meaning: Pair<String, String>,
    public val range: CharRange,
    public val fill: MutableList<Pair<String, String>>.() -> Unit
) : QuestionBuilder() {
    override fun build(type: QuestionType): Question {
        val reversal = Boolean.random()
        val key = { pair: Pair<String, String> -> if (reversal) pair.second else pair.first }
        val value = { pair: Pair<String, String> -> if (reversal) pair.first else pair.second }

        val relation = ArrayList<Pair<String, String>>().apply { fill(); shuffle() }
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
public data class CustomQuestionInfo(
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
) : QuestionBuilder() {
    public constructor(
        problem: String,
        right: List<String>,
        error: List<String>,
        coin: Int,
        tips: String,
        timeout: Long
    ) : this(
        problem = problem,
        options = right.associateWith { true } + error.associateWith { false },
        coin = coin,
        tips = tips,
        timeout = timeout
    )

    override fun build(type: QuestionType): Question {
        val map = (('A'..'Z') zip options.keys.shuffled()).toMap()
        return Question(
            problem = problem,
            options = map,
            answer = map.mapNotNullTo(HashSet()) { if (options[it.value] == true) it.key else null },
            coin = coin,
            tips = tips,
            timeout = timeout,
            type = type
        )
    }
}

public class DateTimeQuestionBuilder(
    public val problem: String,
    public val datetime: OffsetDateTime
) : QuestionBuilder() {
    override fun build(type: QuestionType): Question {
        val range = 'A'..'D'
        val list = mutableListOf(
            datetime,
            datetime.randomDays(),
            datetime.randomHours(),
            datetime.randomDays().randomHours()
        )
        list.shuffle()
        val map = (range zip list).toMap()
        val answer = map.mapNotNullTo(HashSet()) { if (it.value == datetime) it.key else null }
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

public class JudgmentQuestionBuilder(
    public val generate: (Boolean) -> Pair<String, String>
) : QuestionBuilder() {
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