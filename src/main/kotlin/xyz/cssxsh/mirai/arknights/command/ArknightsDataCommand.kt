package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.arknights.user.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.ArknightsConfig

public object ArknightsDataCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "data", "数据",
    description = "明日方舟助手数据指令"
) {

    @SubCommand("arknights", "方舟")
    @Description("方舟数据下载")
    public suspend fun CommandSenderOnMessage<*>.arknights(): Unit = reply {
        runCatching {
            ExcelData.download(flush = true)
            "ExcelData 数据加载完毕"
        }.getOrElse {
            "ExcelData 数据加载失败, ${it.message}"
        }.toPlainText()
    }

    @SubCommand("penguin", "企鹅", "企鹅物流")
    @Description("企鹅物流数据下载")
    public suspend fun CommandSenderOnMessage<*>.penguin(): Unit = reply {
        runCatching {
            PenguinData.download(flush = true)
            "PenguinData 数据加载完毕"
        }.getOrElse {
            "PenguinData 数据加载失败, ${it.message}"
        }.toPlainText()
    }

    @SubCommand("name", "alias", "别称", "别名")
    @Description("企鹅物流材料别称")
    public suspend fun CommandSenderOnMessage<*>.name(): Unit = reply { alias() }

    @SubCommand("reload", "重载")
    @Description("重载Config数据")
    public suspend fun CommandSenderOnMessage<*>.reload(): Unit = reply {
        with(ArknightsHelperPlugin) { ArknightsConfig.reload() }
        "数据已重载".toPlainText()
    }

    @SubCommand("recruit", "公招")
    @Description("提交公招结果")
    public suspend fun CommandSenderOnMessage<*>.recruit(times: Int = 1): Unit = reply {
        for (index in 1..times) {
            sendMessage(buildString {
                appendLine("格式: (时间，干员，出现的词条, 选择的的词条, 移除的的词条): ")
                if (index == 1) appendLine("例如:  1:00，翎羽，近卫 医疗 先锋 新手 输出, 新手 先锋, 新手")
            })
            val content = nextContent()
            if ("停止" in content) break
            runCatching {
                val list = content.split(',', '，', ';', '；', '\n', '\\', '|')

                val time = list[0].split(':', '：', '.', '-').let { (h, m) -> h.toLong() * 60 + m.toLong() }
                check(time * 60 * 1000 in RecruitTime) { "招募时间不正确" }

                val role = role(name = list[1].trim())

                val words = tag(words = list[2].split(' ', '\t'))
                check(words.size == 5) { "词条数量不足5" }

                val selected = tag(words = list.getOrNull(3).orEmpty().split(' ', '\t'))
                check(words.containsAll(selected)) { "选择的词条应在出现的词条中" }
                check(selected.size in 0..3) { "词条数量应在0..3" }

                val removed = tag(words = list.getOrNull(4).orEmpty().split(' ', '\t'))
                check(selected.containsAll(removed)) { "移除的词条应在选择的词条中" }

                result = result + UserRecruit(words, selected, removed, time, role)
            }.onFailure {
                sendMessage("错误: ${it.message}")
            }
        }
        "公招结果已录入".toPlainText()
    }

    @SubCommand("tag", "标签分析")
    @Description("标签分析")
    public suspend fun CommandSenderOnMessage<*>.tag(): Unit = reply {
        RecruitResult.values.flatten().tag().toPlainText()
    }

    @SubCommand("role", "干员分析")
    @Description("标签分析")
    public suspend fun CommandSenderOnMessage<*>.role(): Unit = reply {
        val exclude = ExcelData.characters.values.rarities(1, 2).map { it.name }
        RecruitResult.values.flatten().role(exclude).toPlainText()
    }

    @SubCommand("clear", "清理")
    @Description("清理缓存")
    public suspend fun CommandSenderOnMessage<*>.clear(): Unit = reply {
        val list = listOf(MicroBlogData, VideoData, AnnouncementData)
        for (downloader in list) {
            for (file in downloader.dir.listFiles().orEmpty()) {
                if (file.isDirectory) {
                    file.deleteRecursively()
                }
            }
        }
        "MicroBlogData, VideoData, AnnouncementData 数据已清理".toPlainText()
    }
}