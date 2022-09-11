package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.data.*
import kotlin.collections.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.4.2") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    private val commands: List<Command> by services()
    private val config: List<PluginConfig> by services()
    private val data: List<PluginData> by services()

    @Suppress("INVISIBLE_MEMBER")
    private inline fun <reified T : Any> services(): Lazy<List<T>> = lazy {
        with(net.mamoe.mirai.console.internal.util.PluginServiceHelper) {
            jvmPluginClasspath.pluginClassLoader
                .findServices<T>()
                .loadAllServices()
        }
    }

    override fun onEnable() {
        for (config in config) config.reload()
        for (data in data) data.reload()
        for (command in commands) command.register()

        System.setProperty(ExcelDataHolder.GAME_SOURCE_HOST_KEY, ArknightsConfig.host)

        ArknightsSubscriber.registerTo(globalEventChannel())
        ArknightsSubscriber.start()
    }

    override fun onDisable() {
        for (command in commands) command.unregister()

        ArknightsSubscriber.cancel()
    }
}