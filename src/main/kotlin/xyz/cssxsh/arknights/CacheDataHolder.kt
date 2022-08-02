package xyz.cssxsh.arknights

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
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

    public suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            try {
                return@supervisorScope block(http)
            } catch (cause: Throwable) {
                if (ignore(cause).not()) throw cause
            }
        }
        throw CancellationException()
    }
}