package xyz.cssxsh.arknights.bilibili

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

internal const val BILIBILI_API = "https://api.bilibili.com/x/space/wbi/arc/search"

internal const val BILIBILI_ID = 161775300L

@Serializable
public enum class VideoType(public val tid: Int, public vararg val sub: Int) : CacheKey {
    ANIME(1, 27),
    MUSIC(3, 28, 29, 130, 193),
    GAME(4, 172),
    ENTERTAINMENT(5, 71);

    override val filename: String = "BILIBILI.${name}.json"

    override val url: String get() = BILIBILI_API
}

@Serializable
internal data class Temp(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: JsonElement = JsonNull,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int = 0
)

@Serializable
internal data class VideoHistory(
    @SerialName("list")
    val list: VideoList = VideoList(),
    @SerialName("page")
    val page: VideoPage? = null,
    @SerialName("episodic_button")
    val button: JsonElement = JsonNull,
    @SerialName("is_risk")
    val isRisk: Boolean = false,
    @SerialName("gaia_res_type")
    val gaiaResType: Int = 0,
    @SerialName("gaia_data")
    val gaiaData: JsonElement = JsonNull,
)

@Serializable
internal data class VideoList(
    @SerialName("vlist")
    val videos: List<Video> = emptyList(),
    @SerialName("tlist")
    val t: JsonElement = JsonNull,
    @SerialName("slist")
    val s: JsonElement = JsonNull
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
    val type: Int,
    @SerialName("video_review")
    internal val videoReview: Int = 0,
    @SerialName("hide_click")
    internal val hideClick: Boolean = false,
    @SerialName("is_pay")
    internal val isPay: Int = 0,
    @SerialName("is_union_video")
    internal val isUnionVideo: Int = 0,
    @SerialName("is_steins_gate")
    internal val isSteinsGate: Int = 0,
    @SerialName("is_live_playback")
    internal val isLivePlayback: Int = 0,
    @SerialName("is_avoided")
    internal val isAvoided: Int = 0,
    @SerialName("is_charging_arc")
    internal val isChargingArc: Boolean = false,
    @SerialName("attribute")
    internal val attribute: Int = 0,
    @SerialName("meta")
    internal val meta: JsonElement = JsonNull,
    @SerialName("vt")
    internal val vt: Int = 0,
    @SerialName("enable_vt")
    internal val enableVT: Int = 0,
    @SerialName("vt_display")
    internal val vtDisplay: String = "",
    @SerialName("playback_position")
    internal val playbackPosition: Int = 0
) : CacheInfo {
    override val url: String by lazy { "https://www.bilibili.com/video/${bvid}" }
}