package xyz.cssxsh.arknights.mine

import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.JsonTest

internal class QuestionKtTest : JsonTest() {

    private fun withQuestion(block: () -> QuestionBuild) {
        block().build(QuestionType.OTHER).run {
            buildString {
                appendLine("问题：${problem} $answer")
                options.forEach { (index, text) ->
                    appendLine("${index}.${text}")
                }
            }
        }.let {
            println(it)
        }
    }

    private fun withQuestions(block: () -> Collection<QuestionBuild>) {
        block().forEach { withQuestion { it } }
    }

    @Test
    fun randomBuildingQuestion() = withQuestion {
        randomBuildingQuestion(excel.buffs)
    }

    @Test
    fun randomPlayerQuestion() = withQuestion {
        randomPlayerQuestion(excel.const)
    }

    @Test
    fun randomCharacterQuestion() = withQuestions {
        listOf(
            randomTalentQuestion(excel.characters),
            randomPositionQuestion(excel.characters),
            randomProfessionQuestion(excel.characters),
            randomRarityQuestion(excel.characters)
        )
    }

    @Test
    fun randomHandbookQuestion() = withQuestions {
        listOf(
            randomIllustQuestion(excel.handbooks),
            randomCharacterVoiceQuestion(excel.handbooks),
            randomInfoQuestion(excel.handbooks)
        )
    }

    @Test
    fun randomSkillQuestion() = withQuestion {
        randomSkillQuestion(excel.skills)
    }

    @Test
    fun randomVideoQuestion() = withQuestions {
        listOf(
            randomMusicQuestion(video.music)
        )
    }

    @Test
    fun randomPowerQuestion() = withQuestion {
        randomPowerQuestion(excel.powers)
    }

    @Test
    fun randomStoryQuestion() = withQuestion {
        randomStoryQuestion(excel.stories)
    }

    @Test
    fun randomEnemyInfoQuestion() = withQuestion {
        randomEnemyInfoQuestion(excel.enemies)
    }

    @Test
    fun randomWeeklyQuestion() = withQuestion {
        randomWeeklyQuestion(excel.weeks)
    }
}