package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*

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

    @ValueName("host")
    @ValueDescription("游戏资源")
    public val host: String by value("raw.fastgit.org")

    @ValueName("source_init_timeout")
    @ValueDescription("游戏资源初始化时限")
    public val timeout: Long by value(3600_000L)
}