package xyz.cssxsh.arknights

import xyz.cssxsh.arknights.announce.AnnouncementData
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.MicroBlogData
import java.io.File

abstract class JsonTest {

    private val dir: File get() = File("./test/ArknightsGameData/${SERVER.locale}/gamedata")

    val excel by lazy { ExcelData(dir) }

    val penguin by lazy { PenguinData(File("./test/penguin")) }

    val video by lazy { VideoData(File("./test/bilibili")) }

    val blogs by lazy { MicroBlogData(File("./test/weibo")) }

    val announce by lazy { AnnouncementData(File("./test/announce")) }
}