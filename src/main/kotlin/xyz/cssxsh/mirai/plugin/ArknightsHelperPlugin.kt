package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.arknights.download
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.ArknightsUserData
import kotlin.time.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "0.1.0-dev-1") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    @ConsoleExperimentalApi
    override fun onEnable() {
        launch {
            runCatching {
                download(dataFolder, RESOURCES)
            }.onSuccess {
                logger.info { "${RESOURCES}数据加载完毕" }
            }.onFailure {
                logger.warning({ "${RESOURCES}数据加载失败" }, it)
            }
        }
        ArknightsUserData.reload()
        ArknightsRecruitCommand.register()
        ArknightsGachaCommand.register()
    }

    @ConsoleExperimentalApi
    override fun onDisable() {
        ArknightsRecruitCommand.unregister()
        ArknightsGachaCommand.unregister()
    }
}