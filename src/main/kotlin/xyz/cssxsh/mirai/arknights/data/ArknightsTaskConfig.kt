package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsTaskConfig : AutoSavePluginConfig("task") {

    @ValueDescription("开启了提醒的QQ号/QQ群号(正负性区别，QQ群是负数)")
    public val contacts: MutableSet<Long> by value()

    @ValueName("auto_add_guard")
    @ValueDescription("开启新好友或新群自动蹲饼")
    public var auto: Boolean by value(false)

    @ValueDescription("订阅 微博 类型")
    public val blog: MutableMap<Long, List<BlogUser>> by value()

    @ValueDescription("订阅 视频 类型")
    public val video: MutableMap<Long, List<VideoType>> by value()

    @ValueDescription("订阅 公告 类型")
    public val announce: MutableMap<Long, AnnounceType> by value()
}