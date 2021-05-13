package xyz.cssxsh.arknights.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.intercept
import xyz.cssxsh.arknights.timestamp
import java.time.format.DateTimeFormatter
import kotlin.time.minutes

@Serializable
data class UserRecruit(
    @SerialName("words")
    val words: Set<String>,
    @SerialName("selected")
    val selected: Set<String>,
    @SerialName("removed")
    val removed: Set<String>,
    @SerialName("time")
    val time: Int,
    @SerialName("role")
    val role: String,
    @SerialName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

private val formatter = DateTimeFormatter.ofPattern("yy-MM-dd hh:mm:ss")

/**
 * 构建TAG记录表
 */
fun Collection<UserRecruit>.table(page: Int): String = buildString {
    val that = this@table
    val records = requireNotNull(that.sortedByDescending { it.timestamp }.chunked(10).getOrNull(page - 1)) {
        "不存在的页号${page}"
    }
    appendLine("# 公招记录第${page}页")
    appendLine("| 干员 | 招募时间 | 词条 | 记录时间 |")
    appendLine("|:---:|:---:|:---:|:---:|")
    records.forEach { recruit ->
        val timestamp = timestamp(recruit.timestamp / 1_000).format(formatter)
        val time = recruit.time.minutes.toComponents { hours, minutes, _, _ -> "%d:%02d".format(hours, minutes) }
        val words = recruit.words.joinToString(" ") { word ->
            when (word) {
                in recruit.removed -> "<$word>"
                in recruit.selected -> "($word)"
                else -> word
            }
        }
        appendLine("| ${recruit.role} | $time | $words | $timestamp |")
    }
}

private fun List<String>.most() = associateWith { tag -> count { tag == it } }.maxByOrNull { it.value }

/**
 * 构建TAG表
 */
fun Collection<UserRecruit>.tag(): String = buildString {
    val that = this@tag
    runCatching {
        val words = that.flatMap { it.words }
        val tags = words.toSet().associateWith { tag -> words.count { tag == it } / that.size.toDouble() }
        appendLine("# TAG出现的概率(样本量${that.size})")
        appendLine("| TAG | 概率 |")
        appendLine("|:---:|:---:|")
        tags.entries.sortedByDescending { it.value }.forEach { (tag, probability) ->
            appendLine("| $tag | ${probability.intercept()} |")
        }
    }
}

/**
 * 构建ROLE表
 */
fun Collection<UserRecruit>.role(exclude: Collection<String> = emptySet()): String = buildString {
    val that = this@role
    val roles = (that.groupBy { it.role } - exclude).entries.sortedByDescending { it.value.size }
    appendLine("# 最匹配的TAG(样本量${that.size})")
    appendLine("| 干员 | 选中 | 出现 |")
    appendLine("|:---:|:---:|:---:|")
    roles.forEach { (role, records) ->
        val select = records.flatMap { it.selected }.most()
        val word = records.flatMap { it.words }.most()
        appendLine("| $role(${records.size}) | ${select?.key}(${select?.value}) | ${word?.key}(${word?.value}) |")
    }
}