package xyz.cssxsh.arknights

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.weibo.*
import java.io.File

internal class CacheDataHolderTest {
    private val folder = File("./test").apply { mkdirs() }
    private val video = VideoDataHolder(folder = folder) { false }
    private val blog = MicroBlogDataHolder(folder = folder) { false }
    private val announcement = AnnouncementDataHolder(folder = folder) { false }
    private val excel = ExcelDataHolder(folder = folder) { false }

    @Test
    fun video(): Unit = runBlocking {
        video.load(VideoType.MUSIC)
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
    }

    @Test
    fun character(): Unit = runBlocking {
        excel.load(ExcelDataType.CHARACTER)
        excel.character()
    }
}