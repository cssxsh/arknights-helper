package xyz.cssxsh.arknights.bilibili

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime

private const val BILIBILI_API = "https://api.bilibili.com/x/space/arc/search"

private const val BILIBILI_ID = 161775300L

private const val PAGE_SIZE = 100

private const val PAGE_NUM = 1

private const val ORDER = "pubdate"

private fun File.readVideoHistory(type: VideoDataType): List<Video> {
    return read<Temp>(type).let { requireNotNull(it.data) { it.message } }.list.videos
}

class VideoData(override val dir: File): GameDataDownloader {
    val anime get() = dir.readVideoHistory(VideoDataType.ANIME)
    val music get() = dir.readVideoHistory(VideoDataType.MUSIC)
    val game get() = dir.readVideoHistory(VideoDataType.GAME)

    val all get() = anime + music + game

    override val types get() = VideoDataType.values().asIterable()
}

val Video.url get() = Url("https://www.bilibili.com/video/${bvid}")

enum class VideoDataType(val id: Int) : GameDataType {
    ANIME(1),
    MUSIC(3),
    GAME(4);

    override val path = name.toLowerCase() + ".json"

    private val parameters = Parameters.build {
        append("mid", BILIBILI_ID.toString())
        append("ps", PAGE_SIZE.toString())
        append("pn", PAGE_NUM.toString())
        append("order", ORDER)
        append("tid", id.toString())
    }

    override val url: Url = Url(BILIBILI_API).copy(parameters = parameters)
}

@Serializable
private data class Temp(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: VideoHistory?,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int
)

@Serializable
private data class VideoHistory(
    @SerialName("episodic_button")
    private val episodicButton: JsonObject? = null,
    @SerialName("list")
    val list: VideoList,
    @SerialName("page")
    private val page: JsonObject
)

@Serializable
private data class VideoList(
    @SerialName("tlist")
    private val types: Map<Int, JsonObject>?,
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
    @SerialName("hide_click")
    val hideClick: Boolean,
    @SerialName("is_live_playback")
    val isLivePlayback: Int,
    @SerialName("is_pay")
    val isPay: Int,
    @SerialName("is_steins_gate")
    val isSteinsGate: Int,
    @SerialName("is_union_video")
    val isUnionVideo: Int,
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
    @SerialName("video_review")
    val videoReview: Int
)