package xyz.cssxsh.mirai.arknights

import net.mamoe.mirai.console.events.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*

/**
 * 表示一个启动事件
 */
public class StartupEvent(public val plugin: JvmPlugin) : ConsoleEvent, AbstractEvent()