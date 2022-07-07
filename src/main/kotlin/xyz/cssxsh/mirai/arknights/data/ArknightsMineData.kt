package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.arknights.mine.CustomQuestion
import xyz.cssxsh.arknights.mine.QuestionType

public object ArknightsMineData : AutoSavePluginData("mine") {
    private val default = CustomQuestion(
        problem = "以下那个干员被称为老女人",
        options = mapOf(
            "凯尔希" to true,
            "华法琳" to false,
            "黑" to false,
            "斯卡蒂" to false
        ),
        tips = "还行，合成玉没有被扣",
        coin = -1000,
        timeout = 30 * 1000L
    )

    @ValueDescription("Key 是问题ID，Value是问题")
    public val question: MutableMap<String, CustomQuestion> by value(mutableMapOf("default" to default))

    @ValueDescription("正确数 错误数 和 超时数")
    public val count: MutableMap<QuestionType, MutableList<Int>> by value()
}