package xyz.cssxsh.arknights.penguin

import io.ktor.client.request.*
import kotlinx.coroutines.sync.*
import xyz.cssxsh.arknights.*
import java.io.File

public class PenguinDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<PenguinDataType, CacheInfo>() {

    public override val loaded: MutableSet<PenguinDataType> = HashSet()

    override suspend fun load(key: PenguinDataType): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)

        loaded.add(key)
    }

    override suspend fun raw(): List<CacheInfo> = emptyList()

    override suspend fun clear(): Unit = Unit

    public suspend fun items(): List<Item> = PenguinDataType.ITEMS.read()

    public suspend fun stages(): List<Stage> = PenguinDataType.STAGES.read()

    public suspend fun zones(): List<Zone> = PenguinDataType.ZONES.read()

    public suspend fun period(): List<Period> = PenguinDataType.PERIOD.read()

    public suspend fun stats(): ServerStats = PenguinDataType.STATS.read()

    public suspend fun matrices(): List<Matrix> = PenguinDataType.RESULT_MATRIX.read<MatrixData>().matrices

    public suspend fun patterns(): List<PatternMatrix> = PenguinDataType.RESULT_PATTERN.read<PatternData>().patterns
}