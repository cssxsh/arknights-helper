package xyz.cssxsh.arknights.bilibili

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.JsonTest
import java.time.LocalTime

internal class VideoKtTest : JsonTest() {

    init {
        runBlocking {
            video.download(flush = true)
        }
    }

    @Test
    fun anime() {
        video.anime.forEach {
            println(it.title)
            println(it.created)
        }
    }

    @Test
    fun music() {
//        video.music.sortedBy { it.created }.forEach { video ->
//            println("${video.created} ${video.title}")
//        }
        val list = video.music.map { it.created.toLocalTime() }
        val second = list.map { time -> time.toSecondOfDay() }
        println("avg:${LocalTime.ofSecondOfDay(second.average().toLong())}")
        println("max:${LocalTime.ofSecondOfDay(second.maxOrNull()!!.toLong())}")
        println("min:${LocalTime.ofSecondOfDay(second.minOrNull()!!.toLong())}")

        val map = video.music.groupBy { it.created.hour }.mapValues { it.value.size }.toSortedMap()
        println("map:${map}")
    }

    @Test
    fun game() {
//        video.game.sortedBy { it.created }.forEach { video ->
//            println("${video.created} ${video.title}")
//        }
        val list = video.game.map { it.created.toLocalTime() }
        val second = list.map { time -> time.toSecondOfDay() }
        println("avg:${LocalTime.ofSecondOfDay(second.average().toLong())}")
        println("max:${LocalTime.ofSecondOfDay(second.maxOrNull()!!.toLong())}")
        println("min:${LocalTime.ofSecondOfDay(second.minOrNull()!!.toLong())}")

        val map = video.game.groupBy { it.created.hour }.mapValues { it.value.size }.toSortedMap()
        println("map:${map}")
    }

}