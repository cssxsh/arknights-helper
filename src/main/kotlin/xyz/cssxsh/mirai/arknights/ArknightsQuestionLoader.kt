package xyz.cssxsh.mirai.arknights

import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.mine.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsQuestionLoader : QuestionDataLoader {
    override val excel: ExcelDataHolder get() = ArknightsSubscriber.excel
    override val video: VideoDataHolder get() = ArknightsSubscriber.videos
    override val custom: CustomQuestionHolder get() = ArknightsMineData
}