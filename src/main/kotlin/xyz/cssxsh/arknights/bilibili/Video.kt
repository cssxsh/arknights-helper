package xyz.cssxsh.arknights.bilibili

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

internal const val BILIBILI_API = "https://api.bilibili.com/x/space/arc/search"

internal const val BILIBILI_ID = 161775300L

public val Video.url: String get() = "https://www.bilibili.com/video/${bvid}"

@Serializable
public enum class VideoType(public val tid: Int) : CacheKey {
    ANIME(1),
    MUSIC(3),
    GAME(4),
    ENTERTAINMENT(5);

    override val filename: String = "${name}.json"

    override val url: String = BILIBILI_API
}

@Serializable
internal data class Temp(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: VideoHistory? = null,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int = 0
)

@Serializable
internal data class VideoHistory(
    @SerialName("list")
    val list: VideoList,
    @SerialName("page")
    val page: VideoPage
)

@Serializable
internal data class VideoList(
    @SerialName("vlist")
    val videos: List<Video>
)
@Serializable
internal data class VideoPage(
    @SerialName("pn")
    val index: Int,
    @SerialName("ps")
    val size: Int,
    @SerialName("count")
    val count: Int
)

@Serializable
public data class Video(
    @SerialName("aid")
    val aid: Int,
    @SerialName("author")
    val author: String,
    @SerialName("bvid")
    val bvid: String,
    @SerialName("comment")
    val comment: Int,
    @SerialName("copyright")
    val copyright: String,
    @SerialName("created")
    @Serializable(TimestampSerializer::class)
    override val created: OffsetDateTime,
    @SerialName("description")
    val description: String,
    @SerialName("length")
    val length: String,
    @SerialName("mid")
    val mid: Int,
    @SerialName("pic")
    val pic: String,
    @SerialName("play")
    val play: Int,
    @SerialName("review")
    val review: Int,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    val title: String,
    @SerialName("typeid")
    val tid: Int,
) : CacheInfo