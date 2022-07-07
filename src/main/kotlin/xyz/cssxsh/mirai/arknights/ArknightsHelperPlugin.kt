package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.arknights.data.*
import java.util.*
import kotlin.collections.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.4.2") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    private val commands: List<Command> by lazy { service() }
    private val config: List<PluginConfig> by lazy { service() }
    private val data: List<PluginData> by lazy { service() }

    private inline fun <reified T> service(): List<T> {
        val result: MutableList<T> = ArrayList()
        ServiceLoader.load(T::class.java, jvmPluginClasspath.pluginClassLoader)
            .stream().forEach { provider ->
                try {
                    val instance = provider.type().kotlin.objectInstance ?: provider.get()
                    result.add(instance)
                } catch (cause: Throwable) {
                    logger.warning { "${provider.type().name} 注册失败" }
                }
            }

        return result
    }

    override fun PluginComponentStorage.onLoad() {
        runAfterStartup {
            ArknightsSubscriber.start()
        }
    }

    override fun onEnable() {
        Downloader.ignore = DownloaderIgnore
        for (config in config) config.reload()
        for (data in data) data.reload()
        for (command in commands) command.register()

        System.setProperty("xyz.cssxsh.arknights.source", ArknightsConfig.source)

        launch {
            try {
                downloadGameData()
            } catch (cause: Throwable) {
                logger.warning({ "数据下载失败, 功能可能会不正常" }, cause)
            }
        }
    }

    override fun onDisable() {
        // Command
        for (command in commands) {
            command.unregister()
        }

        ArknightsSubscriber.stop()
    }
}