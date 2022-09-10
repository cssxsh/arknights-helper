package xyz.cssxsh.arknights.penguin

import io.ktor.client.request.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File
import java.util.*

public class PenguinDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<PenguinDataType, CacheInfo>() {

    public override val cache: MutableMap<PenguinDataType, Any> = EnumMap(PenguinDataType::class.java)

    private suspend inline fun <reified T: Any> PenguinDataType.get(): T = mutex.withLock {
        val raw = cache[this]
        return if (raw == null) {
            val read = CustomJson.decodeFromString<T>(file.readText())
            cache[this] = read
            read
        } else {
            raw as T
        }
    }

    override suspend fun load(key: PenguinDataType): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)

        cache.remove(key)
    }

    @Deprecated(message = "raw is empty", level = DeprecationLevel.HIDDEN)
    override suspend fun raw(key: PenguinDataType): List<CacheInfo> = emptyList()

    override suspend fun clear(): Unit = Unit

    public suspend fun items(): List<Item> = PenguinDataType.ITEMS.get()

    public suspend fun stages(): List<Stage> = PenguinDataType.STAGES.get()

    public suspend fun zones(): List<Zone> = PenguinDataType.ZONES.get()

    public suspend fun period(): List<Period> = PenguinDataType.PERIOD.get()

    public suspend fun stats(): ServerStats = PenguinDataType.STATS.get()

    public suspend fun matrices(): List<Matrix> = PenguinDataType.RESULT_MATRIX.get<MatrixData>().matrices

    public suspend fun patterns(): List<PatternMatrix> = PenguinDataType.RESULT_PATTERN.get<PatternData>().patterns
}