package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.arknights.command.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.4.2") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    override fun onEnable() {
        Downloader.ignore = DownloaderIgnore
        // Data and config
        for (data in ArknightsHelperData) {
            (data as? PluginConfig)?.reload() ?: data.reload()
        }
        System.setProperty("xyz.cssxsh.arknights.source", ArknightsConfig.source)
        launch {
            try {
                downloadGameData()
            } catch (cause: Throwable) {
                logger.warning({ "数据下载失败, 功能可能会不正常" }, cause)
            }
        }.invokeOnCompletion {
            // Command
            for (command in ArknightsHelperCommand) {
                command.register()
            }

            ArknightsSubscriber.start()
        }
    }

    override fun onDisable() {
        // Command
        for (command in ArknightsHelperCommand) {
            command.unregister()
        }

        ArknightsSubscriber.stop()
    }
}