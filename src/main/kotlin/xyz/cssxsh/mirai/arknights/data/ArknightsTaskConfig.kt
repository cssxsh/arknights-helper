package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsTaskConfig : AutoSavePluginConfig("task") {

    @ValueDescription("订阅 微博 类型")
    public val blog: MutableMap<Long, List<BlogUser>> by value {
        put(12345, BlogUser.values().asList())
    }

    @ValueDescription("订阅 视频 类型")
    public val video: MutableMap<Long, List<VideoType>> by value {
        put(12345, VideoType.values().asList())
    }

    @ValueDescription("订阅 公告 类型")
    public val announce: MutableMap<Long, List<AnnounceType>> by value {
        put(12345, AnnounceType.values().asList())
    }

    @ValueDescription("订阅 周常 类型")
    public val weekly: MutableMap<Long, List<WeeklyType>> by value {
        put(12345, WeeklyType.values().asList())
    }

    @ValueDescription("订阅 活动 类型")
    public val activity: MutableMap<Long, List<ActivityThemeType>> by value {
        put(12345, ActivityThemeType.values().asList())
    }
}