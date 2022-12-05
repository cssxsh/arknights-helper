package xyz.cssxsh.mirai.arknights.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.arknights.mine.*

public object ArknightsMineData : AutoSavePluginData("mine"), CustomQuestionHolder {
    @ValueDescription("Key 是问题ID，Value是问题")
    public override val question: MutableMap<String, CustomQuestionInfo> by value {
        put("kalts", CustomQuestionInfo(
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
        ))
        put("amiya", CustomQuestionInfo(
            problem = "阿米娅的种族是",
            options = mapOf(
                "卡特斯、奇美拉" to true,
                "驴" to false,
            ),
            tips = "合成玉保住了",
            coin = -1000,
            timeout = 10 * 1000L
        ))
        put("ansel", CustomQuestionInfo(
            problem = "安塞尔的性别是",
            options = mapOf(
                "男" to true,
                "女" to false,
            ),
            tips = "噫，变态",
            coin = -1000,
            timeout = 30 * 1000L
        ))
    }

    @ValueDescription("正确数 错误数 和 超时数")
    public val count: MutableMap<QuestionType, MutableList<Int>> by value()
}