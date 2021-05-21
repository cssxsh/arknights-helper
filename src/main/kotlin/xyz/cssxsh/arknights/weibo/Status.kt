package xyz.cssxsh.arknights.weibo

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val BLOG_API = "https://m.weibo.cn/api/container/getIndex"

const val CONTENT_API = "https://m.weibo.cn/statuses/extend"

private fun File.readMicroBlogHistory(type: BlogUser): List<MicroBlog> {
    return read<Temp<WeiboData>>(type).data().cards.map { it.blog }
}

private fun File.readMicroBlogPicture(type: BlogUser): List<MicroBlog> {
    return read<Temp<PictureData>>(type).data().blogs.associateBy { it.id }.values.toList()
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
    val picture get() = dir.readMicroBlogPicture(BlogUser.PICTURE)

    val all get() = arknights + byproduct + historicus + mounten

    override val types get() = BlogUser.values().asIterable()
}

enum class BlogUser(val id: Long) : GameDataType {
    ARKNIGHTS(6279793937),
    BYPRODUCT(6441489862),
    MOUNTEN(7506039414),
    HISTORICUS(7499841383),
    PICTURE(6279793937) {
        override val path: String = "BlogPicture(${id}).json"
        override val url: Url = Url("$BLOG_API?containerid=107803$id")
    };

    override val path = "Blog(${id}).json"

//    private val parameters = Parameters.build {
//        append("value", "$id")
//        append("containerid", "107603$id")
//    }

    override val url = Url("$BLOG_API?containerid=107603$id")
}

private val ImageSizeRegex = """(?<=https://wx\d\.sinaimg\.cn/)([0-9A-z]+)""".toRegex()

private const val OriginalSize = "orj1080"

val MicroBlog.images get() = pictures.map { Url(it.url.replace(ImageSizeRegex, OriginalSize)) }

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
    @SerialName("cards")
    val cards: List<Card> = emptyList(),
)

@Serializable
private data class Card(
    @SerialName("mblog")
    val blog: MicroBlog,
)

@Serializable
data class MicroBlog(
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("id")
    val id: Long,
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("pics")
    val pictures: List<MicroPicture> = emptyList(),
    @SerialName("raw_text")
    val raw: String? = null,
    @SerialName("retweeted_status")
    val retweeted: MicroBlog? = null,
    val text: String,
    @SerialName("user")
    val user: MicroBlogUser = PictureUser,
)

@Serializable
data class MicroPicture(
    @SerialName("url")
    val url: String
)

@Serializable
data class MicroBlogUser(
    @SerialName("avatar_hd")
    val avatar: String,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("screen_name")
    val name: String
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
    @SerialName("longTextContent")
    val content: String,
    @SerialName("ok")
    val ok: Int,
)

private val PictureUser = MicroBlogUser(
    "",
    "",
    0,
    "此微博被锁定为热门，机器人无法获取详情，请打开链接自行查看"
)

private val PictureData.blogs get() = cards.flatMap { it.group }.flatMap { it.pictures }.map { it.blog }

@Serializable
private data class PictureData(
    @SerialName("cards")
    val cards: List<PictureCard>
)

@Serializable
private data class PictureCard(
    @SerialName("card_group")
    val group: List<PictureCardGroup>,
)

@Serializable
private data class PictureCardGroup(
    @SerialName("pics")
    val pictures: List<PictureItem> = emptyList()
)

@Serializable
private data class PictureItem(
    @SerialName("mblog")
    val blog: MicroBlog,
)