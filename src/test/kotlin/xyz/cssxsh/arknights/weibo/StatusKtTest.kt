package xyz.cssxsh.arknights.weibo

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.JsonTest

internal class StatusKtTest : JsonTest() {

    init {
        runBlocking {
            BlogUser.values().toList().download(dir = weibo, flush = true)
        }
    }

    @Test
    fun arknights() = runBlocking {
        blogs.arknights.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.createdAt)
        }
    }
}