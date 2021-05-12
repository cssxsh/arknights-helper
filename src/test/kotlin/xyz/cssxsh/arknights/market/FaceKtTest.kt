package xyz.cssxsh.arknights.market

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class FaceKtTest {

    private val data = ArknightsFaceData(File("./test/MarketFace/"), DefaultItems)

    @Test
    fun load(): Unit = runBlocking {
        data.download(flush = false)
        data.faces.forEach { (name, list) ->
            println("====>[${name}]")
            list.forEach {
                println("${it.content} ${it.image}")
            }
        }
    }
}