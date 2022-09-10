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
import kotlin.reflect.full.*

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
        val text = getResource("META-INF/services/${T::class.qualifiedName}") ?: return emptyList()
        return text.lineSequence().mapNotNull { name ->
            try {
                val clazz = jvmPluginClasspath.pluginClassLoader.loadClass(name)
                val instance = clazz.kotlin.objectInstance ?: clazz.kotlin.createInstance()
                instance as T
            } catch (_: ClassNotFoundException) {
                logger.warning { "SPI 服务 $name 注册错误" }
                null
            } catch (cause: Exception) {
                logger.warning({ "SPI 服务 $name 初始化错误" }, cause)
                null
            }
        }.toList()
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
        for (command in commands) command.unregister()

        ArknightsSubscriber.stop()
    }
}