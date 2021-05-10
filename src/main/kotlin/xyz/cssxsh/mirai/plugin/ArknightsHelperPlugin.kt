package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.Job
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.mirai.plugin.command.*
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

    private lateinit var clock: Job

    private lateinit var subscribe: Job

    private lateinit var guard: Job

    private lateinit var group: Job

    private lateinit var friend: Job

    @ConsoleExperimentalApi
    override fun onEnable() {
        downloadExternalData()
        ArknightsUserData.reload()
        ArknightsPoolData.reload()
        ArknightsMineData.reload()
        ArknightsTaskData.reload()
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

        clock = clock()
        subscribe = subscribe()
        guard = guard()
        group = group()
        friend = friend()
    }

    @ConsoleExperimentalApi
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

        clock.cancel()
        subscribe.cancel()
        guard.cancel()
        group.cancel()
        friend.cancel()
    }
}