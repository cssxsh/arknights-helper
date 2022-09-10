package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.mine.*

public object ArknightsMineData : AutoSavePluginData("mine"), CustomQuestionHolder {
    private val default = CustomQuestionInfo(
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
    public override val question: MutableMap<String, CustomQuestionInfo> by value(mutableMapOf("default" to default))

    @ValueDescription("正确数 错误数 和 超时数")
    public val count: MutableMap<QuestionType, MutableList<Int>> by value()
}