package xyz.cssxsh.arknights.penguin

import io.ktor.http.*
import xyz.cssxsh.arknights.*
import java.io.File

const val PENGUIN_STATS_CN = "penguin-stats.cn"

const val PENGUIN_STATS_IO = "penguin-stats.io"

val penguin = { type: PenguinDataType -> Url("https://${PENGUIN_STATS_CN}/PenguinStats/api/v2/${type.path}") }

enum class PenguinDataType: GameDataType {
    // BASE
    ITEMS,
    STAGES,
    ZONES,
    PERIOD,
    STATS,

    // STATS
    RESULT_MATRIX { override val path get() = "_private/result/matrix/${SERVER}/global.json" },
    RESULT_PATTERN { override val path get() = "_private/result/pattern/${SERVER}/global.json" };

    override val path = "${name.toLowerCase()}.json"
}

suspend fun Iterable<PenguinDataType>.download(dir: File, flush: Boolean = false): List<File> = load(dir, flush, penguin)

fun File.readItems(): List<Item> = read(PenguinDataType.ITEMS)

fun File.readStages(): List<Stage> = read(PenguinDataType.STAGES)

fun File.readZones(): List<Zone> = read(PenguinDataType.ZONES)

fun File.readPeriod(): List<Period> = read(PenguinDataType.PERIOD)

fun File.readStats(): ServerStats = read(PenguinDataType.STATS)

fun File.readMatrices() = read<MatrixData>(PenguinDataType.RESULT_MATRIX).matrices

fun File.readPatterns() = read<PatternData>(PenguinDataType.RESULT_PATTERN).patterns

class PenguinData(dir: File) {
    val items by lazy { dir.readItems() }
    val stages by lazy { dir.readStages() }
    val zones by lazy { dir.readZones() }
    val period by lazy { dir.readPeriod() }
    val stats by lazy { dir.readStats() }
    val matrices by lazy { dir.readMatrices() }
    val patterns by lazy { dir.readPatterns() }
}