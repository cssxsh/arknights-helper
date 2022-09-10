package xyz.cssxsh.mirai.arknights

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.arknights.excel.*
import xyz.cssxsh.mirai.arknights.data.*
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