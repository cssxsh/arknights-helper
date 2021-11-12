package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.plugin.command.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.3.2") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    override fun onEnable() {
        Downloader.ignore = DownloaderIgnore
        downloadGameData()
        // Data and config
        for (data in ArknightsHelperData) {
            (data as? PluginConfig)?.reload() ?: data.reload()
        }
        // Command
        for (command in ArknightsHelperCommand) {
            command.register()
        }

        ArknightsSubscriber.start()
    }

    override fun onDisable() {
        // Command
        for (command in ArknightsHelperCommand) {
            command.unregister()
        }

        ArknightsSubscriber.stop()
    }
}