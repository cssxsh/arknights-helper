package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import xyz.cssxsh.arknights.excel.GachaPoolRule

public object ArknightsPoolConfig : AutoSavePluginConfig("pool") {
    @ValueDescription("Key 是QQ号/QQ群号，Value是规则名")
    public val pool: MutableMap<Long, String> by value<MutableMap<Long, String>>().withDefault { GachaPoolRule.NORMAL.name }

    private val default get() = GachaPoolRule.values().associate { it.name to it.rule }

    @ValueDescription("Key 规则名，Value是卡池规则")
    public val rules: MutableMap<String, String> by value<MutableMap<String, String>>().withDefault { default.getValue(it) }
}