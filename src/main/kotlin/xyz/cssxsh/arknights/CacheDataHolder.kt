package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import java.io.File

public abstract class CacheDataHolder<K : CacheKey, R : CacheInfo> {
    protected open val mutex: Mutex = Mutex()
    protected open val http: HttpClient = HttpClient(OkHttp) {
        BrowserUserAgent()
        ContentEncoding()
        expectSuccess = true
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            requestTimeoutMillis = null
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }
    public abstract val folder: File

    public abstract val loaded: Set<K>

    public abstract suspend fun load(key: K)

    public abstract suspend fun raw(): List<R>

    public abstract suspend fun clear()

    protected abstract val ignore: suspend (exception: Throwable) -> Boolean

    protected val CacheKey.file: File get() = folder.resolve(filename)

    protected suspend inline fun <reified T> K.read(): T = mutex.withLock {
        return CustomJson.decodeFromString(file.readText())
    }

    protected suspend inline fun <reified T> K.write(data: T): Unit = mutex.withLock {
        file.writeText(CustomJson.encodeToString(data))
    }

    protected suspend fun HttpStatement.copyTo(target: File): Unit = supervisorScope {
        while (isActive) {
            try {
                execute { response ->
                    target.outputStream().use { output ->
                        val channel = response.bodyAsChannel()

                        while (!channel.isClosedForRead) channel.copyTo(output)
                    }
                }
            } catch (cause: Throwable) {
                if (ignore(cause).not()) throw cause
            }
        }
    }
}