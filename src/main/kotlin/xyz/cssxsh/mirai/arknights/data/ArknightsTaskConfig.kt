package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsTaskConfig : AutoSavePluginConfig("task") {
    @ValueDescription("开启了提醒的QQ号/QQ群号(正负性区别，QQ群是负数)")
    public val contacts: MutableSet<Long> by value()

    @ValueDescription("蹲饼轮询间隔，单位分钟，默认5分钟")
    public var interval: Int by value(5)

    @ValueDescription("蹲饼 默认")
    public val default: DataCron by value(DefaultCronParser.parse("0 0 8-20 * * ? ").asData())

    @ValueDescription("蹲饼 微博")
    public val weibo: MutableMap<BlogUser, DataCron> by value()

    @ValueDescription("蹲饼 视频")
    public val video: MutableMap<VideoDataType, DataCron> by value()

    @ValueDescription("蹲饼 公告")
    public val announce: DataCron by value()
}