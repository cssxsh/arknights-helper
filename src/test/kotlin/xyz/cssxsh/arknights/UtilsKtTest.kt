package xyz.cssxsh.arknights

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.excel.*
import java.io.File
import java.util.*

internal class UtilsKtTest {

    private val dir: File get() = File("./test/ArknightsGameData")

    @Test
    fun client(): Unit = runBlocking {
        val url = jsdelivr(path = "${SERVER}/gamedata/buff_table.json")
        println(url)
        val message: HttpMessage = useHttpClient { it.head(url) }
        println(message.headers.date())
        println(message.headers.expires())
        println(message.headers.lastModified())
    }

    @Test
    fun download(): Unit = runBlocking {
       val map: ResourceMap = mapOf(
           GameDataType.EXCEL to setOf(CHARACTER)
       )
       download(dir = dir, map = map).values.flatMap { it.values }.forEach { file ->
           println("$file -> ${file.lastModified()}")
       }
    }
}