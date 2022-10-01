package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.arknights.announce.*
import xyz.cssxsh.arknights.bilibili.*
import xyz.cssxsh.arknights.weibo.*
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsGuardCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-guard", "方舟蹲饼",
    description = "明日方舟助手蹲饼指令"
) {

    @SubCommand("detail", "详情")
    @Description("查看蹲饼详情")
    public suspend fun CommandSender.detail() {
        val message = buildMessageChain {
            appendLine("=== 微博订阅 ===")
            ArknightsTaskConfig.blog.forEach { (id, blogs) ->
                appendLine("$id : $blogs")
            }
            appendLine("=== 视频订阅 ===")
            ArknightsTaskConfig.video.forEach { (id, videos) ->
                appendLine("$id : $videos")
            }
            appendLine("=== 公告订阅 ===")
            ArknightsTaskConfig.announce.forEach { (id, announces) ->
                appendLine("$id : $announces")
            }
        }

        sendMessage(message = message)
    }

    @SubCommand("blog", "微博")
    @Description("设置微博蹲饼内容")
    public suspend fun CommandSender.blog(contact: Long, vararg blogs: String) {
        ArknightsTaskConfig.blog[contact] = blogs.map { BlogUser.valueOf(it) }
        val message = buildMessageChain {
            append("当前微博订阅内容 ")
            append(blogs.joinToString(", ").ifEmpty { "为空" })
        }

        sendMessage(message = message)
    }

    @SubCommand("video", "视频")
    @Description("设置视频蹲饼内容")
    public suspend fun CommandSender.video(contact: Long, vararg videos: String) {
        ArknightsTaskConfig.video[contact] = videos.map { VideoType.valueOf(it) }
        val message = buildMessageChain {
            append("当前视频订阅内容 ")
            append(videos.joinToString(", ").ifEmpty { "为空" })
        }

        sendMessage(message = message)
    }

    @SubCommand("announce", "公告")
    @Description("设置视频蹲饼内容")
    public suspend fun CommandSender.announce(contact: Long, vararg announces: String) {
        ArknightsTaskConfig.announce[contact] = announces.map { AnnounceType.valueOf(it) }
        val message = buildMessageChain {
            append("当前公告订阅内容 ")
            append(announces.joinToString(", ").ifEmpty { "为空" })
        }

        sendMessage(message = message)
    }
}