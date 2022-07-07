package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.bilibili.VideoDataType
import xyz.cssxsh.arknights.weibo.BlogUser

public object ArknightsConfig : ReadOnlyPluginConfig("config") {

    @ValueDescription("Key 是别名 Value 是干员名")
    public val roles: MutableMap<String, String> by value(
        mutableMapOf(
            "羊" to "艾雅法拉",
            "鳄鱼" to "艾丝黛尔"
        )
    )

    @ValueDescription("Key 是别名 Value 是材料名")
    public val items: MutableMap<String, String> by value(
        mutableMapOf(
            "绿管" to "晶体元件"
        )
    )

    @ValueName("auto_add_guard")
    @ValueDescription("开启新好友或新群自动蹲饼")
    public val auto: Boolean by value(false)

    @ValueName("video")
    @ValueDescription("开启订阅的b站视频类型 ANIME, MUSIC, GAME, ENTERTAINMENT")
    public val video: Set<VideoDataType> by value(VideoDataType.values().toSet())

    @ValueName("blog")
    @ValueDescription("开启订阅的微博号 BYPRODUCT, MOUNTEN, HISTORICUS")
    public val blog: Set<BlogUser> by value(BlogUser.values().toSet())

    @ValueName("source")
    @ValueDescription("游戏资源")
    public val source: String by value("https://raw.fastgit.org/Kengxxiao/ArknightsGameData/master")

    @ValueName("source_init_timeout")
    @ValueDescription("游戏资源初始化时限")
    public val timeout: Long by value(3600_000L)
}