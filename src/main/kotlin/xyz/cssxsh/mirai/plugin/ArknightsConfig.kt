package xyz.cssxsh.mirai.plugin

import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.market.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.*
import kotlin.time.*

private fun resolve(name: String) = ArknightsHelperPlugin.dataFolder.resolve(name)

internal val ExcelData by lazy { ExcelData(resolve("ArknightsGameData")) }

internal val PenguinData by lazy { PenguinData(resolve("PenguinStats")) }

internal val VideoData by lazy { VideoData(resolve("BilibiliData")) }

internal val MicroBlogData by lazy { MicroBlogData(resolve("WeiboData")) }

internal val ArknightsFaceData by lazy { ArknightsFaceData(resolve("ArknightsFaceData"), FaceItems) }

internal val QuestionDataLoader = QuestionDataLoader({ ExcelData }, { VideoData }, { CustomQuestions.values })

internal val Obtain get() = ExcelData.characters.values

internal val PlayerLevelRange get() = 1..ExcelData.const.maxPlayerLevel

internal val RegenSpeed get() = ExcelData.const.playerApRegenSpeed.minutes

const val PoolUseCoin = 600

val RecruitTime = (1).hours..(9).hours

/**
 * 卡池规则MAP
 */
internal val PoolRules by ArknightsPoolData::rules

/**
 * 蹲饼联系人
 */
internal val GuardContacts by ArknightsTaskData::contacts

/**
 * 自定义问题MAP
 */
internal val CustomQuestions by ArknightsMineData::question

/**
 * 轮询速度
 */
internal var GuardInterval by ArknightsTaskData::interval

/**
 * 要加载的表情列表
 */
internal val FaceItems by ArknightsTaskData::faces
