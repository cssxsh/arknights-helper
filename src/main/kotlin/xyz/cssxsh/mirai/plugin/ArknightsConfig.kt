package xyz.cssxsh.mirai.plugin

import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.*
import kotlin.time.*

private val ArknightsGameData get() = ArknightsHelperPlugin.dataFolder.resolve("ArknightsGameData")

private val PenguinStats get() = ArknightsHelperPlugin.dataFolder.resolve("PenguinStats")

internal val BilibiliData get() = ArknightsHelperPlugin.dataFolder.resolve("BilibiliData")

internal val WeiboData get() = ArknightsHelperPlugin.dataFolder.resolve("WeiboData")

internal val ARKNIGHTS_EXCEL_DATA = ExcelDataType.values().toList()

internal val PENGUIN_DATA = PenguinDataType.values().toList()

internal val BILIBILI_VIDEO = VideoDataType.values().toList()

internal val MICRO_BLOG_USER = BlogUser.values().toList()

internal val ExcelData by lazy { ExcelData(ArknightsGameData) }

internal suspend fun downloadExcelData(flush: Boolean) = ARKNIGHTS_EXCEL_DATA.download(ArknightsGameData, flush)

internal val PenguinData by lazy { PenguinData(PenguinStats) }

internal suspend fun downloadPenguinData(flush: Boolean) = PENGUIN_DATA.download(PenguinStats, flush)

internal val VideoData by lazy { VideoData(BilibiliData) }

internal suspend fun downloadVideoData(flush: Boolean) = PENGUIN_DATA.download(PenguinStats, flush)

internal val MicroBlogData by lazy { MicroBlogData(WeiboData) }

internal suspend fun downloadMicroBlogData(flush: Boolean) = MICRO_BLOG_USER.download(WeiboData, flush)

internal val QuestionDataLoader = QuestionDataLoader({ ExcelData }, { VideoData }, { CustomQuestions.values })

internal val Obtain get() = ExcelData.characters.values

internal val PlayerLevelRange get() = 1..ExcelData.const.maxPlayerLevel

internal val RegenSpeed get() = ExcelData.const.playerApRegenSpeed.minutes

const val PoolUseCoin = 600

val RecruitTime = (1).hours..(9).hours

/**
 * 卡池规则MAP
 */
internal val PoolRules get() = ArknightsPoolData.rules

/**
 * 蹲饼联系人
 */
internal val GuardContacts get() = ArknightsTaskData.contacts

/**
 * 自定义问题MAP
 */
internal val CustomQuestions get() = ArknightsMineData.question

/**
 * 轮询速度
 */
internal var GuardInterval
    get() = ArknightsTaskData.interval.minutes
    set(value) {
        ArknightsTaskData.interval = value.inMinutes.toInt()
    }
