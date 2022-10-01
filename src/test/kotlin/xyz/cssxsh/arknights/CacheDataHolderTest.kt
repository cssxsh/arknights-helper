package xyz.cssxsh.arknights

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*
import java.io.File

internal class CacheDataHolderTest {
    private val video = VideoDataHolder(folder = File("./test")) { false }
    private val blog = MicroBlogDataHolder(folder = File("./test")) { false }
    private val announcement = AnnouncementDataHolder(folder = File("./test")) { false }

    @Test
    fun video(): Unit = runBlocking {
        video.load(VideoType.MUSIC)
    }

    @Test
    fun blog(): Unit = runBlocking {
        blog.load(BlogUser.ARKNIGHTS)
    }

    @Test
    fun announcement(): Unit = runBlocking {
        announcement.load(AnnounceType.BILIBILI)
    }
}