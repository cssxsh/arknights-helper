package xyz.cssxsh.arknights.bilibili

import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.*

private const val BILIBILI_API = "https://api.bilibili.com/x/space/arc/search"

private const val BILIBILI_ID = 161775300L

private const val PAGE_SIZE = 100

private const val PAGE_NUM = 1

private const val ORDER = "pubdate"

private fun File.readVideoHistory(type: VideoDataType): List<Video> {
    return with(read<Temp>(type)) { requireNotNull(data) { "$type error $code $message" } }.list.videos
}

class VideoData(override val dir: File) : GameDataDownloader {
    val anime get() = dir.readVideoHistory(VideoDataType.ANIME)
    val music get() = dir.readVideoHistory(VideoDataType.MUSIC)
    val game get() = dir.readVideoHistory(VideoDataType.GAME)
    val entertainment get() = dir.readVideoHistory(VideoDataType.ENTERTAINMENT)

    val all get() = anime + music + game + entertainment

    override val types get() = VideoDataType.values().asIterable()
}

val Video.url get() = Url("https://www.bilibili.com/video/${bvid}")
val Video.cover get() = Url(pic)

enum class VideoDataType(private val tid: Int) : GameDataType {
    ANIME(1),
    MUSIC(3),
    GAME(4),
    ENTERTAINMENT(5);

    override val duration: Long = 10_000

    override val path = "$name.json"

    @OptIn(ExperimentalSerializationApi::class)
    override val readable: (ByteArray) -> Boolean = { bytes ->
        CustomJson.decodeFromString<Temp>(bytes.decodeToString()).data != null
    }

    @OptIn(InternalAPI::class)
    private val parameters = Parameters.build {
        append("mid", BILIBILI_ID.toString())
        append("ps", PAGE_SIZE.toString())
        append("pn", PAGE_NUM.toString())
        append("order", ORDER)
        append("tid", tid.toString())
    }

    override val url: Url = Url(BILIBILI_API).copy(parameters = parameters)
}

@Serializable
private data class Temp(
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
private data class VideoHistory(
    @SerialName("list")
    val list: VideoList,
)

@Serializable
private data class VideoList(
    @SerialName("vlist")
    val videos: List<Video>
)

@Serializable
data class Video(
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
    val created: OffsetDateTime,
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
)