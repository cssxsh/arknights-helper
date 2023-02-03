package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.*
import kotlin.collections.*

public object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "2.0.4") {
        name("arknights-helper")
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.meme-helper", true)
    }
) {

    private val commands: List<Command> by spi()
    private val config: List<PluginConfig> by spi()
    private val data: List<PluginData> by spi()
    private val listeners: List<ListenerHost> by spi()

    @Suppress("INVISIBLE_MEMBER")
    private inline fun <reified T : Any> spi(): Lazy<List<T>> = lazy {
        with(net.mamoe.mirai.console.internal.util.PluginServiceHelper) {
            jvmPluginClasspath.pluginClassLoader
                .findServices<T>()
                .loadAllServices()
        }
    }

    init {
        System.setProperty(IGNORE_UNKNOWN_KEYS, "true")
    }

    override fun PluginComponentStorage.onLoad() {
        runAfterStartup {
            launch {
                StartupEvent(plugin = this@ArknightsHelperPlugin).broadcast()
            }
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