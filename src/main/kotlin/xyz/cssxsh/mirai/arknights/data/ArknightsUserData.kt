package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault

public object ArknightsUserData : AutoSavePluginData("user") {
    @ValueDescription("Key 是QQ号，Value是合成玉数值")
    public val coin: MutableMap<Long, Int> by value<MutableMap<Long, Int>>().withDefault { 3_000 }

//    @ValueDescription("Key 是QQ号，Value是玩家等级")
//    public var level: MutableMap<Long, Int> by value<MutableMap<Long, Int>>().withDefault { ExcelData.const.maxPlayerLevel }

//    @ValueDescription("Key 是QQ号，Value是理智预警时间戳")
//    public val reason: MutableMap<Long, Long> by value<MutableMap<Long, Long>>().withDefault { 0 }

//    @ValueDescription("Key 是QQ号，Value是公招预警预警时间戳")
//    public val recruit: MutableMap<Long, Map<Int, Long>> by value<MutableMap<Long, Map<Int, Long>>>().withDefault { emptyMap() }
}