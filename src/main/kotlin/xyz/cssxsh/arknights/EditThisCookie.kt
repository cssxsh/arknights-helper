package xyz.cssxsh.arknights

import io.ktor.http.*
import io.ktor.util.date.*
import kotlinx.serialization.*

@Serializable
public data class EditThisCookie(
    @SerialName("domain")
    val domain: String,
    @SerialName("expirationDate")
    val expirationDate: Double? = null,
    @SerialName("hostOnly")
    val hostOnly: Boolean = false,
    @SerialName("httpOnly")
    val httpOnly: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String,
    @SerialName("sameSite")
    val sameSite: String = "unspecified",
    @SerialName("secure")
    val secure: Boolean,
    @SerialName("session")
    val session: Boolean = false,
    @SerialName("storeId")
    val storeId: String = "0",
    @SerialName("value")
    val value: String
) {
    public fun toCookie(): Cookie = Cookie(
        name = name,
        value = value,
        encoding = CookieEncoding.RAW,
        expires = expirationDate?.run { GMTDate(times(1000).toLong()) },
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly
    )
}