package xyz.cssxsh.arknights

import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.MicroBlogData
import java.io.File

abstract class JsonTest {

    private val dir: File get() = File("./test/ArknightsGameData/${SERVER.locale}/gamedata")

    val excel by lazy { ExcelData(dir) }

    val data = File("./test/penguin")

    val penguin by lazy { PenguinData(File("./test/penguin")) }

    val bilibili = File("./test/bilibili")

    val video by lazy { VideoData(bilibili) }

    val weibo = File("./test/weibo")

    val blogs by lazy { MicroBlogData(weibo) }
}