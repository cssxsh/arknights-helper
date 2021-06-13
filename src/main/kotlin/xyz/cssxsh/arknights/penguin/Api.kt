package xyz.cssxsh.arknights.penguin

import io.ktor.http.*
import xyz.cssxsh.arknights.*
import java.io.File

private const val PENGUIN_STATS_CN = "penguin-stats.cn"

private const val PENGUIN_STATS_IO = "penguin-stats.io"

enum class PenguinDataType : GameDataType {
    // BASE
    ITEMS,
    STAGES,
    ZONES,
    PERIOD,
    STATS,

    // STATS
    RESULT_MATRIX {
        override val path get() = "_private/result/matrix/${SERVER}/global.json"
    },
    RESULT_PATTERN {
        override val path get() = "_private/result/pattern/${SERVER}/global.json"
    };

    override val path get() = "${name.lowercase()}.json"

    override val url: Url get() = Url("https://${PENGUIN_STATS_CN}/PenguinStats/api/v2/${path}")
}

private fun File.readItems(): List<Item> = read(PenguinDataType.ITEMS)

private fun File.readStages(): List<Stage> = read(PenguinDataType.STAGES)

private fun File.readZones(): List<Zone> = read(PenguinDataType.ZONES)

private fun File.readPeriod(): List<Period> = read(PenguinDataType.PERIOD)

private fun File.readStats(): ServerStats = read(PenguinDataType.STATS)

private fun File.readMatrices() = read<MatrixData>(PenguinDataType.RESULT_MATRIX).matrices

private fun File.readPatterns() = read<PatternData>(PenguinDataType.RESULT_PATTERN).patterns

class PenguinData(override val dir: File): GameDataDownloader {
    val items by lazy { dir.readItems() }
    val stages by lazy { dir.readStages() }
    val zones by lazy { dir.readZones() }
    val period by lazy { dir.readPeriod() }
    val stats by lazy { dir.readStats() }
    val matrices by lazy { dir.readMatrices() }
    val patterns by lazy { dir.readPatterns() }

    override val types get() = PenguinDataType.values().asIterable()
}