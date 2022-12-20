package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import xyz.cssxsh.mirai.arknights.*
import xyz.cssxsh.mirai.arknights.data.*

public object ArknightsDataCommand : CompositeCommand(
    owner = ArknightsHelperPlugin,
    "ark-data", "方舟数据",
    description = "明日方舟助手数据指令"
) {

    @SubCommand("clear", "清理")
    @Description("清理缓存")
    public suspend fun CommandSender.clear() {
        val message = try {
            ArknightsSubscriber.clear()
            "数据已清理".toPlainText()
        } catch (cause: Exception) {
            (cause.message ?: cause.toString()).toPlainText()
        }
        sendMessage(message = message)
    }

    @SubCommand("cron", "定时")
    @Description("重载定时设置")
    public suspend fun CommandSender.cron() {
        val message = try {
            with(ArknightsHelperPlugin) { ArknightsCronConfig.reload() }
            "定时设置已重载".toPlainText()
        } catch (cause: Exception) {
            (cause.message ?: cause.toString()).toPlainText()
        }
        sendMessage(message = message)
    }

    @SubCommand("voice", "语音")
    public suspend fun UserCommandSender.voice(id: String = "") {
        val words = ArknightsSubscriber.excel.word().charWords
        val characters = ArknightsSubscriber.excel.character()
        val word = words[id] ?: words.values.random()
        val character = characters.getValue(word.character)
        ArknightsHelperPlugin.logger.info("${character.name} - ${word.voiceTitle} - ${word.voiceText}")
        val file = ArknightsSubscriber.static.voice(character = character, word = word)
        val audio = file.toExternalResource().use { (subject as AudioSupported).uploadAudio(it) }
        sendMessage(message = audio)
    }
}