package xyz.cssxsh.arknights

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.prts.*
import java.time.*

internal class CacheDataHolderTest {
    private val folder = java.io.File("./run").apply { mkdirs() }
    private val ignore: suspend (Throwable) -> Boolean = { it is java.io.IOException }
    private val video = VideoDataHolder(folder = folder, ignore = ignore)
    private val blog = MicroBlogDataHolder(folder = folder, ignore = ignore)
    private val announcement = AnnouncementDataHolder(folder = folder, ignore = ignore)
    private val excel = ExcelDataHolder(folder = folder, ignore = ignore)
    private val penguin = PenguinDataHolder(folder = folder, ignore = ignore)
    private val static = StaticDataHolder(folder = folder, ignore = ignore)

    // region Cake

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun video(): Unit = runBlocking {
        video.load(VideoType.MUSIC)
        video.raw(VideoType.MUSIC).forEach {
            video.cover(it)
        }
    }

    @Test
    fun blog(): Unit = runBlocking {
        blog.load(BlogUser.ARKNIGHTS)
        blog.raw(BlogUser.ARKNIGHTS).forEach {
            blog.images(it)
        }
    }

    @Test
    fun announcement(): Unit = runBlocking {
        announcement.load(AnnounceType.ANDROID)
        announcement.raw(AnnounceType.ANDROID).forEach {
            announcement.download(it.webUrl)
        }
    }

    // endregion

    // region Excel

    @Test
    fun character(): Unit = runBlocking {
        excel.load(ExcelDataType.CHARACTER)
        excel.character()
    }

    @Test
    fun building(): Unit = runBlocking {
        excel.load(ExcelDataType.BUILDING)
        excel.building()
    }

    @Test
    fun const(): Unit = runBlocking {
        excel.load(ExcelDataType.CONST)
        excel.const()
    }

    @Test
    fun enemy(): Unit = runBlocking {
        excel.load(ExcelDataType.ENEMY)
        excel.enemy()
    }

    @Test
    fun gacha(): Unit = runBlocking {
        excel.load(ExcelDataType.GACHA)
        excel.gacha()
    }

    @Test
    fun handbook(): Unit = runBlocking {
        excel.load(ExcelDataType.HANDBOOK)
        excel.handbook()
    }

    @Test
    fun word(): Unit = runBlocking {
        excel.load(ExcelDataType.WORD)
        val table = excel.word()
        for ((id, voice) in table.voiceLanguages) {
            for ((type, info) in voice.dict) {
                if (info.voices.size == 1) continue
                println("$id $type $info")
            }
        }
    }

    @Test
    fun skill(): Unit = runBlocking {
        excel.load(ExcelDataType.SKILL)
        excel.skill()
    }

    @Test
    fun story(): Unit = runBlocking {
        excel.load(ExcelDataType.STORY)
        excel.story()
    }

    @Test
    fun team(): Unit = runBlocking {
        excel.load(ExcelDataType.TEAM)
        excel.team()
    }

    @Test
    fun zone(): Unit = runBlocking {
        excel.load(ExcelDataType.ZONE)
        excel.zone()
    }

    @Test
    fun voice(): Unit = runBlocking {
        excel.load(ExcelDataType.CHARACTER)
        excel.load(ExcelDataType.WORD)
        val characters = excel.character()
        val table = excel.word()
        val cache = hashSetOf<String>()
        for ((_, word) in table.characterWords) {
            if (cache.add(word.character).not()) continue
            val character = characters.getValue(word.character)
            val info = table.voiceLanguages.getValue(word.character)
            val voice = info.dict.values.first()
            if (voice.language == VoiceLanguageType.LINKAGE) continue
            val key = StaticData.Voice(character = character, word = word, voice = voice)
            println(key.url)
        }
    }

    @Test
    fun skin(): Unit = runBlocking {
        excel.load(ExcelDataType.SKIN)
        val table = excel.skin()
        table.brands.forEach { (_, brand) ->
            println(brand.name)
        }
    }

    @Test
    fun activity(): Unit = runBlocking {
        excel.load(ExcelDataType.ACTIVITY)
        val table = excel.activity()
        val now = OffsetDateTime.now()
        table.basic.forEach { (_, activity) ->
            if (now in activity) {
                println(activity.name)
                println(activity.displayType ?: activity.type)
                println(activity.start)
                println(activity.reward)
                println(activity.end)
            }
        }
        table.themes.forEach { theme ->
            if (now in theme) {
                println(theme.id)
                println(theme.type)
                println(theme.start)
                println(theme.nodes)
            }
        }
    }

    @Test
    fun equip(): Unit = runBlocking {
        excel.load(ExcelDataType.EQUIP)
        excel.equip()
    }

    // endregion

    // region Penguin

    @Test
    fun items(): Unit = runBlocking {
        penguin.load(PenguinDataType.ITEMS)
        penguin.items()
    }

    @Test
    fun stages(): Unit = runBlocking {
        penguin.load(PenguinDataType.STAGES)
        penguin.stages()
    }

    @Test
    fun zones(): Unit = runBlocking {
        penguin.load(PenguinDataType.ZONES)
        penguin.zones()
    }

    @Test
    fun period(): Unit = runBlocking {
        penguin.load(PenguinDataType.PERIOD)
        penguin.period()
    }

    @Test
    fun stats(): Unit = runBlocking {
        penguin.load(PenguinDataType.STATS)
        penguin.stats()
    }

    @Test
    fun matrices(): Unit = runBlocking {
        penguin.load(PenguinDataType.RESULT_MATRIX)
        penguin.matrices()
    }

    @Test
    fun patterns(): Unit = runBlocking {
        penguin.load(PenguinDataType.RESULT_PATTERN)
        penguin.patterns()
    }

    // endregion
}