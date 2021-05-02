package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.plugin.data.*
import kotlin.math.abs
import kotlin.time.*

val RESOURCES: ResourceMap = mapOf(
    GameDataType.EXCEL to setOf(
        CHARACTER,
        GACHA,
        HANDBOOK,
        CONST
    )
)

internal val character by lazy { data.readCharacterTable() }

internal val gacha by lazy { data.readGachaTable() }

internal val handbook by lazy { data.readHandbookTable() }

internal val const by lazy { data.readConstInfo() }

internal val obtain by lazy { character.values.rarities(2..5).obtain("招募寻访") }

internal val PlayerLevelRange get() = 1..const.maxPlayerLevel

internal val RegenSpeed get() = const.playerApRegenSpeed.minutes

const val POOL_USE_COIN = 600

val RECRUIT_TIME = (9).hours..(1).hours

fun CoroutineScope.clock(interval: Duration = (1).minutes) = launch {
    while (isActive) {
        ArknightsUserData.reason.forEach { (id, timestamp) ->
            if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed.toLongMilliseconds()) {
                launch {
                    runCatching {
                        val user = requireNotNull(UserOrNull(id)) { "未找到用户" }
                        val massage = " 理智警告 ".toPlainText()
                        if (user is Member) {
                            user.group.sendMessage(massage + At(user))
                        } else {
                            user.sendMessage(massage)
                        }
                    }.onFailure {
                        logger.warning({ "定时器播报失败" }, it)
                    }
                }
            }
        }
        ArknightsUserData.recruit.forEach { (id, sites) ->
            sites.forEach { (site, timestamp) ->
                if (abs(timestamp - System.currentTimeMillis()) < RegenSpeed.toLongMilliseconds()) {
                    launch {
                        runCatching {
                            val user = requireNotNull(UserOrNull(id)) { "未找到用户" }
                            val massage = " 公招位置${site}警告 ".toPlainText()
                            if (user is Member) {
                                user.group.sendMessage(massage + At(user))
                            } else {
                                user.sendMessage(massage)
                            }
                        }.onSuccess {
                            ArknightsUserData.recruit[id] = ArknightsUserData.recruit[id].toMutableMap().apply {
                                put(site, 0)
                            }
                        }.onFailure {
                            logger.warning({ "定时器播报失败" }, it)
                        }
                    }
                }
            }
        }
        delay(interval)
    }
}