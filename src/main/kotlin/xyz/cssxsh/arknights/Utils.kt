package xyz.cssxsh.arknights

import kotlinx.serialization.json.*
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

internal const val IGNORE_UNKNOWN_KEYS = "xyz.cssxsh.arknights.ignore"

public val CustomJson: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = System.getProperty(IGNORE_UNKNOWN_KEYS, "false").toBoolean()
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
}

public enum class ServerType(public val locale: Locale) {
    CN(Locale.CHINA),
    US(Locale.US),
    JP(Locale.JAPAN),
    KR(Locale.KOREA),
    TW(Locale.TAIWAN);
}

public var SERVER: ServerType = ServerType.CN

public typealias Server<T> = Map<ServerType, T>

internal fun OkHttpClient.Builder.doh(urlString: String, ipv6: Boolean) {
    if (urlString.isEmpty()) return
    val doh = DnsOverHttps.Builder()
        .client(OkHttpClient())
        .url(urlString.toHttpUrl())
        .includeIPv6(ipv6)
        .build()
    dns(dns = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                doh.lookup(hostname)
            } catch (_: UnknownHostException) {
                Dns.SYSTEM.lookup(hostname)
            }
        }
    })
}