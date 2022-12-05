package xyz.cssxsh.mirai.arknights.data

import kotlinx.serialization.modules.*
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.penguin.*
import xyz.cssxsh.arknights.weibo.*

public object ArknightsCronConfig : ReadOnlyPluginConfig("cron") {

    override val serializersModule: SerializersModule = SerializersModule {
        contextual(DataCron)
    }

    private fun cron(expression: String): DataCron {
        return DefaultCronParser.parse(expression).asData()
    }

    @ValueDescription("定时器 默认")
    public val default: DataCron by value(cron("0 0,1,15 10-20 * * ? "))

    @ValueDescription("定时器 微博")
    public val blog: MutableMap<BlogUser, DataCron> by value {
        put(BlogUser.ARKNIGHTS, cron("0 0/5 10-20 * * ? "))
        put(BlogUser.BYPRODUCT, cron("0 5,6 10-20 * * ? "))
        put(BlogUser.MOUNTEN, cron("0 7,8 10-20 * * ? "))
        put(BlogUser.HISTORICUS, cron("0 0 16 * * ? "))
    }

    @ValueDescription("定时器 视频")
    public val video: MutableMap<VideoType, DataCron> by value {
        put(VideoType.ANIME, cron("0 0/5 10-20 * * ? "))
        put(VideoType.MUSIC, cron("0 1/5 10-20 * * ? "))
        put(VideoType.GAME, cron("0 2/5 10-20 * * ? "))
        put(VideoType.ENTERTAINMENT, cron("0 0 16 * * ? "))
    }

    @ValueDescription("定时器 公告")
    public val announce: DataCron by value(cron("0 0 4-20 * * ? "))

    @ValueDescription("定时器 掉落")
    public val penguin: MutableMap<PenguinDataType, DataCron> by value {
        put(PenguinDataType.ITEMS, cron("0 0 16 * * ? "))
        put(PenguinDataType.STAGES, cron("0 0 16 * * ? "))
        put(PenguinDataType.ZONES, cron("0 0 16 * * ? "))
        put(PenguinDataType.PERIOD, cron("0 0 16 * * ? "))
        put(PenguinDataType.STATS, cron("0 0 16 * * ? "))
        put(PenguinDataType.RESULT_MATRIX, cron("0 0 4 * * ? "))
        put(PenguinDataType.RESULT_PATTERN, cron("0 0 4 * * ? "))
    }

    @ValueDescription("定时器 数据")
    public val excel: DataCron by value(cron("0 0 10-20 * * ? "))

    @ValueDescription("定时器 周常")
    public val clock: DataCron by value(cron("0 0 6,12 * * ? "))
}