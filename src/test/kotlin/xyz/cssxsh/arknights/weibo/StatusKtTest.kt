package xyz.cssxsh.arknights.weibo

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.JsonTest

internal class StatusKtTest : JsonTest() {

    init {
        runBlocking {
            blogs.download(flush = true)
        }
    }

    @Test
    fun arknights() = runBlocking {
        blogs.arknights.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.created)
        }
    }

    @Test
    fun byproduct() = runBlocking {
        blogs.byproduct.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.created)
        }
    }

    @Test
    fun historicus() = runBlocking {
        blogs.historicus.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.created)
        }
    }

    @Test
    fun mounten() = runBlocking {
        blogs.mounten.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.created)
        }
    }

    @Test
    fun picture() = runBlocking {
        blogs.picture.forEach { blog ->
            println("=====================================>")
            println(blog.url)
            println(blog.created)
        }
    }
}