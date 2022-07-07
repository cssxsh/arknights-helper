package xyz.cssxsh.arknights

import io.ktor.http.*

public interface CacheKey {
    public val filename: String
    public val url: Url
}