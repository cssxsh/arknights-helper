package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsTaskConfig : AutoSavePluginConfig("task") {

    @ValueDescription("开启了提醒的QQ号/QQ群号")
    public val blog: MutableMap<Long, List<BlogUser>> by value()

    @ValueDescription("开启了提醒的QQ号/QQ群号")
    public val video: MutableMap<Long, List<VideoType>> by value()

    @ValueDescription("开启了提醒的QQ号/QQ群号")
    public val announce: MutableMap<Long, AnnounceType> by value()
}