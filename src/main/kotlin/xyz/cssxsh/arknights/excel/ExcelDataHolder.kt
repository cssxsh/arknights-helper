package xyz.cssxsh.arknights.excel

import io.ktor.client.request.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File

public class ExcelDataHolder(override val folder: File, override val ignore: suspend (Throwable) -> Boolean) :
    CacheDataHolder<ExcelDataType, CacheInfo>() {

    public override val loaded: MutableSet<ExcelDataType> = HashSet()

    override suspend fun load(key: ExcelDataType): Unit = mutex.withLock {
        http.prepareGet(key.url).copyTo(target = key.file)

        loaded.add(key)
    }

    override suspend fun raw(): List<CacheInfo> = emptyList()

    override suspend fun clear(): Unit = Unit

    private suspend inline fun <reified T> ExcelDataType.read(): T = mutex.withLock {
        return CustomJson.decodeFromString(file.readText())
    }

    public suspend fun version(): ExcelDataVersion = mutex.withLock {
        val text = ExcelDataType.VERSION.file.readText()
        lateinit var stream: String
        lateinit var change: String
        lateinit var versionControl: String
        for ((name, value) in text.lines().filter(String::isNotBlank).map { it.split(":") }) {
            when (name) {
                "Stream" -> stream = value
                "Change" -> change = value
                "VersionControl" -> versionControl = value
                else -> Unit
            }
        }
        return ExcelDataVersion(
            stream = stream,
            change = change,
            versionControl = versionControl
        )
    }

    public suspend fun building(): Building = ExcelDataType.BUILDING.read()

    public suspend fun character(): CharacterTable = ExcelDataType.CHARACTER.read()

    public suspend fun const(): ConstInfo = ExcelDataType.CONST.read()

    public suspend fun enemy(): EnemyTable = ExcelDataType.ENEMY.read()

    public suspend fun gacha(): GachaTable = ExcelDataType.GACHA.read()

    public suspend fun handbook(): HandbookTable = ExcelDataType.HANDBOOK.read()

    public suspend fun skill(): SkillTable = ExcelDataType.SKILL.read()

    public suspend fun story(): StoryTable = ExcelDataType.STORY.read()

    public suspend fun team(): TeamTable = ExcelDataType.TEAM.read()

    public suspend fun zone(): ZoneTable = ExcelDataType.ZONE.read()
}