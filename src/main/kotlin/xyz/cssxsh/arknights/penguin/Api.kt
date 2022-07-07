package xyz.cssxsh.arknights.penguin

import io.ktor.http.*
import xyz.cssxsh.arknights.*
import java.io.*

public enum class PenguinDataType : GameDataType {
    // BASE
    ITEMS,
    STAGES,
    ZONES,
    PERIOD,
    STATS,

    // STATS
    RESULT_MATRIX {
        override val path get() = "_private/result/matrix/${SERVER}/global"
    },
    RESULT_PATTERN {
        override val path get() = "_private/result/pattern/${SERVER}/global"
    };

    override val path get() = name.lowercase()

    override val url: Url get() = Url("https://penguin-stats.cn/PenguinStats/api/v2/${path}")
}

private fun File.readItems(): List<Item> = read(PenguinDataType.ITEMS)

private fun File.readStages(): List<Stage> = read(PenguinDataType.STAGES)

private fun File.readZones(): List<Zone> = read(PenguinDataType.ZONES)

private fun File.readPeriod(): List<Period> = read(PenguinDataType.PERIOD)

private fun File.readStats(): ServerStats = read(PenguinDataType.STATS)

private fun File.readMatrices() = read<MatrixData>(PenguinDataType.RESULT_MATRIX).matrices

private fun File.readPatterns() = read<PatternData>(PenguinDataType.RESULT_PATTERN).patterns

public class PenguinData(override val dir: File) : GameDataDownloader {
    public val items by lazy { dir.readItems() }
    public val stages by lazy { dir.readStages() }
    public val zones by lazy { dir.readZones() }
    public val period by lazy { dir.readPeriod() }
    public val stats by lazy { dir.readStats() }
    public val matrices by lazy { dir.readMatrices() }
    public val patterns by lazy { dir.readPatterns() }

    override val types get() = PenguinDataType.values().asIterable()
}