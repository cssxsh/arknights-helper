package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import kotlin.collections.*

public object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "2.0.0") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    private val commands: List<Command> by services()
    private val config: List<PluginConfig> by services()
    private val data: List<PluginData> by services()
    private val listeners: List<ListenerHost> by services()

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
        for (listener in listeners) (listener as SimpleListenerHost).registerTo(globalEventChannel())
    }

    override fun onDisable() {
        for (command in commands) command.unregister()
        for (listener in listeners) (listener as SimpleListenerHost).cancel()
    }
}