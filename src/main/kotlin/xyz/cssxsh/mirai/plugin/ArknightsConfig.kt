package xyz.cssxsh.mirai.plugin

import xyz.cssxsh.arknights.GameDataType
import xyz.cssxsh.arknights.ResourceMap
import xyz.cssxsh.arknights.excel.*

val RESOURCES: ResourceMap = mapOf(
    GameDataType.EXCEL to setOf(
        CHARACTER,
        GACHA,
        HANDBOOK
    )
)

internal val character by lazy { data.readCharacterTable() }

internal val gacha by lazy { data.readGachaTable() }

internal val handbook by lazy { data.readHandbookTable() }

internal val handbook by lazy { data.handbook() }

val POOL_USE_COIN = 600
