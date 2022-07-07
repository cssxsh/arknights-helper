package xyz.cssxsh.mirai.arknights

import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.mirai.arknights.data.*

private fun resolve(name: String) = ArknightsHelperPlugin.dataFolder.resolve(name)

private val VideoTypes get() = ArknightsConfig.video

private val MicroBlogTypes get() = ArknightsConfig.blog

internal val ExcelData by lazy { ExcelData(resolve("ArknightsGameData")) }

internal val PenguinData by lazy { PenguinData(resolve("PenguinStats")) }

internal val VideoData by lazy { VideoData(resolve("BilibiliData"), VideoTypes) }

internal val MicroBlogData: MicroBlogDataHolder by lazy {
    MicroBlogDataHolder(resolve("WeiboData"), MicroBlogTypes, DownloaderIgnore)
}

internal val AnnouncementData by lazy { AnnouncementData(resolve("AnnouncementData")) }

internal val QuestionDataLoader = QuestionDataLoader({ ExcelData }, { VideoData }, { CustomQuestions })

internal val Obtain get() = ExcelData.characters.values

internal val PlayerLevelRange get() = 1..ExcelData.const.maxPlayerLevel

internal val RegenSpeed get() = ExcelData.const.playerApRegenSpeed * 60 * 1000L

internal const val PoolUseCoin = 600

internal val RecruitTime = (1 * 60 * 60 * 1000)..(9 * 60 * 60 * 1000)

/**
 * 卡池规则MAP
 */
internal val PoolRules by ArknightsPoolConfig::rules

/**
 * 蹲饼联系人
 */
internal val GuardContacts by ArknightsTaskConfig::contacts

/**
 * 自定义问题MAP
 */
internal val CustomQuestions by ArknightsMineData::question

/**
 * 答题统计
 */
internal val MineCount by ArknightsMineData::count

/**
 * 轮询速度
 */
internal var GuardInterval by ArknightsTaskConfig::interval

/**
 * 公招结果
 */
internal val RecruitResult by ArknightsUserData::result

/**
 * 自定义干员别名
 */
internal val RoleAlias by ArknightsConfig::roles

/**
 * 自定义材料别名
 */
internal val ItemAlias by ArknightsConfig::items

/**
 * 自定义材料别名
 */
internal val AutoAddGuard by ArknightsConfig::auto
