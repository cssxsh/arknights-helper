package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.descriptor.CommandValueArgumentParser
import net.mamoe.mirai.console.command.descriptor.buildCommandArgumentContext
import xyz.cssxsh.arknights.mine.QuestionType

val ArknightsCommandArgumentContext = buildCommandArgumentContext {
    QuestionType::class with object : CommandValueArgumentParser<QuestionType> {
        override fun parse(raw: String, sender: CommandSender): QuestionType = enumValueOf(raw.toUpperCase())
    }
}