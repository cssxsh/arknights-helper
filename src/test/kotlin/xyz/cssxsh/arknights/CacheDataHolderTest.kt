package xyz.cssxsh.arknights

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.arknights.penguin.*
import java.io.File

internal class CacheDataHolderTest {
    private val folder = File("./test").apply { mkdirs() }
    private val video = VideoDataHolder(folder = folder) { false }
    private val blog = MicroBlogDataHolder(folder = folder) { false }
    private val announcement = AnnouncementDataHolder(folder = folder) { false }
    private val excel = ExcelDataHolder(folder = folder) { false }
    private val penguin = PenguinDataHolder(folder = folder) { false }

    @Test
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
        announcement.load(AnnounceType.BILIBILI)
        announcement.raw(AnnounceType.BILIBILI).forEach {
            announcement.download(it.webUrl)
        }
    }

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
        excel.word()
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
}