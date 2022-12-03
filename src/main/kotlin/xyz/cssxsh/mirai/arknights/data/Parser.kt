package xyz.cssxsh.mirai.arknights.data

import com.cronutils.descriptor.*
import com.cronutils.model.*
import com.cronutils.model.definition.*
import com.cronutils.model.time.*
import com.cronutils.parser.*
import net.mamoe.mirai.console.command.descriptor.*
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

internal const val CRON_TYPE_KEY = "xyz.cssxsh.mirai.cron.type"

internal val DefaultCronParser: CronParser by lazy {
    val type = CronType.valueOf(System.getProperty(CRON_TYPE_KEY, "QUARTZ"))
    CronParser(CronDefinitionBuilder.instanceDefinitionFor(type))
}

internal const val CRON_LOCALE_KEY = "xyz.cssxsh.mirai.cron.locale"

internal val DefaultCronDescriptor: CronDescriptor by lazy {
    val locale = System.getProperty(CRON_LOCALE_KEY)?.let { Locale.forLanguageTag(it) } ?: Locale.getDefault()
    CronDescriptor.instance(locale)
}

internal fun Cron.asData(): DataCron = this as? DataCron ?: DataCron(delegate = this)

internal fun Cron.toExecutionTime(): ExecutionTime = ExecutionTime.forCron((this as? DataCron)?.delegate ?: this)

internal fun Cron.description(): String = DefaultCronDescriptor.describe(this)

internal fun Cron.next(): Long {
    return toExecutionTime()
        .timeToNextExecution(ZonedDateTime.now())
        .orElse(Duration.ofMinutes(30))
        .toMillis()
}

internal val CronCommandArgumentContext: CommandArgumentContext = buildCommandArgumentContext {
    Cron::class with { text ->
        try {
            DefaultCronParser.parse(text)
        } catch (cause: Exception) {
            throw CommandArgumentParserException(
                message = cause.message ?: "Cron 表达式读取错误，建议找在线表达式生成器生成",
                cause = cause
            )
        }
    }
}