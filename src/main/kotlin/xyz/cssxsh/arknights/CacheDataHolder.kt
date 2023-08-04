package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import java.io.*

public abstract class CacheDataHolder<K : CacheKey, R : CacheInfo> {
    protected open val mutex: Mutex = Mutex()
    protected open val http: HttpClient = HttpClient(OkHttp) {
        install(UserAgent) {
            agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"
        }
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

    protected abstract val cache: MutableMap<K, *>

    public abstract suspend fun load(key: K)

    public abstract suspend fun raw(key: K): List<R>

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
        val ignored = mutableListOf<Throwable>()
        while (isActive) {
            try {
                execute { response ->
                    response.bodyAsChannel().copyAndClose(target.writeChannel(coroutineContext))
                    val timestamp = (response.lastModified() ?: response.date())?.time
                    if (timestamp != null) target.setLastModified(timestamp)
                }
                break
            } catch (cause: Throwable) {
                if (ignore(cause).not()) {
                    for (suppressed in ignored) {
                        cause.addSuppressed(suppressed)
                    }
                    throw IllegalStateException("${this@copyTo} copy to ${target.toPath().toUri()}", cause)
                }
                ignored.add(element = cause)
            }
        }
    }
}