package xyz.cssxsh.arknights.weibo

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.SIGN
import xyz.cssxsh.arknights.read
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val BLOG_API = "https://m.weibo.cn/api/container/getIndex"

const val CONTENT_API = "https://m.weibo.cn/statuses/extend"

private fun File.readMicroBlogHistory(type: BlogUser): List<MicroBlog> {
    return read<Temp<WeiboData>>(type).data().cards.map { it.blog }
}

private suspend fun getLongTextContent(id: Long): String {
    return useHttpClient<Temp<LongTextContent>> { client ->
        client.get(CONTENT_API) { parameter("id", id) }
    }.data().content.replace("<br />", "\n").remove(SIGN)
}

class MicroBlogData(override val dir: File): GameDataDownloader {
    val arknights get() = dir.readMicroBlogHistory(BlogUser.ARKNIGHTS)
    val byproduct get() = dir.readMicroBlogHistory(BlogUser.BYPRODUCT)
    val historicus get() = dir.readMicroBlogHistory(BlogUser.HISTORICUS)
    val mounten get() = dir.readMicroBlogHistory(BlogUser.MOUNTEN)

    val all get() = arknights + byproduct + historicus + mounten

    override val types get() = BlogUser.values().asIterable()
}

enum class BlogUser(val id: Long) : GameDataType {
    ARKNIGHTS(6279793937),
    BYPRODUCT(6441489862),
    MOUNTEN(7506039414),
    HISTORICUS(7499841383);

    override val path = "Blog(${id}).json"

    private val parameters = Parameters.build {
        append("value", "$id")
        append("containerid", "107603$id")
    }

    override val url = Url(BLOG_API).copy(parameters = parameters)
}

private val ImageRegex = """(https://wx\d\.sinaimg\.cn)/([0-9A-z]+)/([^"]+)""".toRegex()

private const val OriginalSize = "orj1080"

private fun JsonObject.findImage(): Url {
    return requireNotNull(ImageRegex.find(toString())) { "匹配失败" }.destructured.let { (host, _, name) ->
        Url("$host/$OriginalSize/$name")
    }
}

val MicroBlog.images get() = pics.map { it.findImage() }

val MicroBlog.content get() = raw ?: text.replace("<br />", "\n").remove(SIGN)

val MicroBlog.url get() = Url("https://weibo.com/detail/$id")

suspend fun MicroBlog.content(): String = if (isLongText) getLongTextContent(id = id) else content

private fun <T> Temp<T>.data() = requireNotNull(data) { message }

@Serializable
private data class Temp<T>(
    @SerialName("data")
    val `data`: T?,
    @SerialName("msg")
    val message: String = "",
    @SerialName("ok")
    val ok: Int
)

@Serializable
private data class WeiboData(
    @SerialName("cardlistInfo")
    private val cardListInfo: JsonObject? = null,
    @SerialName("cards")
    val cards: List<Card> = emptyList(),
    @SerialName("scheme")
    val scheme: String? = null,
    @SerialName("showAppTips")
    private val showAppTips: Int? = null
)

@Serializable
private data class Card(
    @SerialName("card_type")
    val type: Int,
    @SerialName("itemid")
    val itemId: String,
    @SerialName("mblog")
    val blog: MicroBlog,
    @SerialName("scheme")
    val scheme: String
)

@Serializable
data class MicroBlog(
    @SerialName("ad_state")
    private val adState: Int? = null,
    @SerialName("alchemy_params")
    private val alchemyParams: JsonObject? = null,
    @SerialName("attitudes_count")
    private val attitudesCount: Int? = null,
    @SerialName("bid")
    private val bid: String,
    @SerialName("bmiddle_pic")
    private val middle: String? = null,
    @SerialName("can_edit")
    private val canEdit: Boolean? = null,
    @SerialName("comments_count")
    private val commentsCount: Int? = null,
    @SerialName("content_auth")
    private val contentAuth: Int? = null,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @SerialName("darwin_tags")
    private val darwinTags: List<JsonObject> = emptyList(),
    @SerialName("edit_at")
    private val editAt: String? = null,
    @SerialName("edit_config")
    private val editConfig: JsonObject? = null,
    @SerialName("edit_count")
    private val editCount: Int? = null,
    @SerialName("enable_comment_guide")
    private val enableCommentGuide: Boolean? = null,
    @SerialName("expire_time")
    private val expireTime: Int? = null,
    @SerialName("extern_safe")
    private val externSafe: Int? = null,
    @SerialName("favorited")
    private val favorited: Boolean? = null,
    @SerialName("fid")
    private val fid: Long? = null,
    @SerialName("hide_flag")
    private val hideFlag: Int? = null,
    @SerialName("id")
    val id: Long,
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("is_paid")
    private val isPaid: Boolean? = null,
    @SerialName("isTop")
    private val isTop: Int? = null,
    @SerialName("mark")
    private val mark: String? = null,
    @SerialName("mblog_menu_new_style")
    private val mblogMenuNewStyle: Int? = null,
    @SerialName("mblog_vip_type")
    private val mblogVipType: Int? = null,
    @SerialName("mblogtype")
    private val mblogtype: Int? = null,
    @SerialName("mid")
    private val mid: String,
    @SerialName("mlevel")
    private val mlevel: Int? = null,
    @SerialName("more_info_type")
    private val moreInfoType: Int? = null,
    @SerialName("number_display_strategy")
    private val numberDisplayStrategy: JsonObject? = null,
    @SerialName("original_pic")
    private val original: String? = null,
    @SerialName("page_info")
    private val pageInfo: JsonObject? = null,
    @SerialName("pending_approval_count")
    private val pendingApprovalCount: Int? = null,
    @SerialName("pic_ids")
    val pictures: List<String> = emptyList(),
    @SerialName("pic_num")
    private val picNum: Int? = null,
    @SerialName("picStatus")
    private val picStatus: String? = null,
    @SerialName("pic_types")
    private val picTypes: String? = null,
    @SerialName("pics")
    internal val pics: List<JsonObject> = emptyList(),
    @SerialName("raw_text")
    val raw: String? = null,
    @SerialName("repost_type")
    private val repostType: Int? = null,
    @SerialName("reposts_count")
    private val repostsCount: Int? = null,
    @SerialName("retweeted_status")
    val retweeted: MicroBlog? = null,
    @SerialName("reward_exhibition_type")
    private val rewardExhibitionType: Int? = null,
    @SerialName("rid")
    private val rid: String? = null,
    @SerialName("safe_tags")
    private val safeTags: Int? = null,
    @SerialName("show_additional_indication")
    private val showAdditionalIndication: Int? = null,
    @SerialName("source")
    private val source: String? = null,
    @SerialName("text")
    val text: String,
    @SerialName("textLength")
    private val textLength: Int? = null,
    @SerialName("thumbnail_pic")
    private val thumbnail: String? = null,
    @SerialName("title")
    private val title: JsonObject? = null,
    @SerialName("user")
    val user: MicroBlogUser,
    @SerialName("version")
    private val version: Int? = null,
    @SerialName("visible")
    private val visible: JsonObject? = null
)

@Serializable
data class MicroBlogUser(
    @SerialName("avatar_hd")
    val avatar: String,
    @SerialName("badge")
    private val badge: JsonObject,
    @SerialName("close_blue_v")
    private val closeBlueV: Boolean,
    @SerialName("cover_image_phone")
    private val coverImagePhone: String,
    @SerialName("description")
    val description: String,
    @SerialName("follow_count")
    private val followCount: Int,
    @SerialName("follow_me")
    private val followMe: Boolean,
    @SerialName("followers_count")
    private val followersCount: Int,
    @SerialName("following")
    private val following: Boolean,
    @SerialName("gender")
    private val gender: String,
    @SerialName("id")
    val id: Long,
    @SerialName("like")
    private val like: Boolean,
    @SerialName("like_me")
    private val likeMe: Boolean,
    @SerialName("mbrank")
    private val mbrank: Int,
    @SerialName("mbtype")
    private val mbtype: Int,
    @SerialName("profile_image_url")
    private val profileImageUrl: String,
    @SerialName("profile_url")
    private val profileUrl: String,
    @SerialName("screen_name")
    val name: String,
    @SerialName("statuses_count")
    private val statusesCount: Int,
    @SerialName("urank")
    private val urank: Int,
    @SerialName("verified")
    private val verified: Boolean,
    @SerialName("verified_reason")
    private val verifiedReason: String,
    @SerialName("verified_type")
    private val verifiedType: Int,
    @SerialName("verified_type_ext")
    private val verifiedTypeExt: Int
)

@Serializer(OffsetDateTime::class)
object WeiboDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("E MMM d HH:mm:ss Z yyyy", Locale.ENGLISH)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime =
        OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) =
        encoder.encodeString(value.format(formatter))
}

@Serializable
data class LongTextContent(
    @SerialName("attitudes_count")
    private val attitudesCount: Int = 0,
    @SerialName("comments_count")
    private val commentsCount: Int = 0,
    @SerialName("longTextContent")
    val content: String,
    @SerialName("ok")
    val ok: Int,
    @SerialName("reposts_count")
    private val repostsCount: Int = 0
)