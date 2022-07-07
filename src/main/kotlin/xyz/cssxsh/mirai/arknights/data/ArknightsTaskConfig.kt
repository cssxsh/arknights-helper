package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*

public object ArknightsTaskConfig : AutoSavePluginConfig("task") {
    @ValueDescription("开启了提醒的QQ号/QQ群号(正负性区别，QQ群是负数)")
    public val contacts: MutableSet<Long> by value()

    @ValueDescription("蹲饼轮询间隔，单位分钟，默认5分钟")
    public var interval: Int by value(5)
}