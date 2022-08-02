package xyz.cssxsh.mirai.arknights.data

import kotlinx.serialization.modules.*
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsCronConfig : AutoSavePluginConfig("cron") {

    override val serializersModule: SerializersModule = SerializersModule {
        contextual(DataCron)
    }

    @ValueDescription("定时器 默认")
    public var default: DataCron by value(DefaultCronParser.parse("0 0 * * * ? ").asData())

    @ValueDescription("定时器 微博")
    public val blog: MutableMap<BlogUser, DataCron> by value()

    @ValueDescription("定时器 视频")
    public val video: MutableMap<VideoType, DataCron> by value()

    @ValueDescription("定时器 公告")
    public var announce: DataCron by value(DefaultCronParser.parse("0 0 * * * ? ").asData())
}