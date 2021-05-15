package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import xyz.cssxsh.mirai.plugin.command.*
import kotlin.time.*

object ArknightsHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.arknights-helper", "1.2.1") {
        name("arknights-helper")
        author("cssxsh")
    }
) {

    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    override fun onEnable() {
        downloadExternalData()
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