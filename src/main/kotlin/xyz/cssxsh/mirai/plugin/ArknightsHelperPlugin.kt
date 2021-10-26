package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.plugin.command.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.3.1") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    override fun onEnable() {
        Downloader.ignore = DownloaderIgnore
        downloadGameData()
        ArknightsUserData.reload()
        ArknightsPoolData.reload()
        ArknightsMineData.reload()
        ArknightsTaskData.reload()
        ArknightsConfig.reload()
        ArknightsRecruitCommand.register()
        ArknightsGachaCommand.register()
        ArknightsPlayerCommand.register()
        ArknightsDataCommand.register()
        ArknightsItemCommand.register()
        ArknightsStageCommand.register()
        ArknightsZoneCommand.register()
        ArknightsMineCommand.register()
        ArknightsQuestionCommand.register()
        ArknightsGuardCommand.register()
        ArknightsFaceCommand.register()

        ArknightsSubscriber.start()
    }

    override fun onDisable() {
        ArknightsRecruitCommand.unregister()
        ArknightsGachaCommand.unregister()
        ArknightsPlayerCommand.unregister()
        ArknightsDataCommand.unregister()
        ArknightsItemCommand.unregister()
        ArknightsStageCommand.unregister()
        ArknightsZoneCommand.unregister()
        ArknightsMineCommand.unregister()
        ArknightsQuestionCommand.unregister()
        ArknightsGuardCommand.unregister()
        ArknightsFaceCommand.unregister()

        ArknightsSubscriber.stop()
    }
}