package xyz.cssxsh.arknights.weibo

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.*
import java.time.format.*
import java.util.*

const val BLOG_API = "https://m.weibo.cn/api/container/getIndex"

const val CONTENT_API = "https://m.weibo.cn/statuses/extend"

private fun File.readMicroBlogHistory(type: BlogUser): List<MicroBlog> {
    return read<Temp<WeiboData>>(type).data().cards.map { it.blog }
}

private fun File.readMicroBlogPicture(type: BlogUser): List<MicroBlog> {
    val map = mutableMapOf<Long, MicroBlog>()
    val epoch = 515483463L
    // XXX
    fun timestamp(id: Long): Long = (id shr 22) + epoch

    read<Temp<PictureData>>(type).data().blogs.forEach { new ->
        map.compute(new.id) { _, old ->
            old?.copy(pictures = old.pictures + new.pictures) ?: new
        }
    }

    return map.values.map {
        it.copy(created = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp(it.id)), it.created.offset))
    }
}

@OptIn(ExperimentalSerializationApi::class)
private suspend fun getLongTextContent(id: Long): String {
    val json = Downloader.useHttpClient { client ->
        lateinit var builder: HttpRequestBuilder
        client.get<String>(CONTENT_API) {
            parameter("id", id)
            builder = this
        }.also {
            if ("请求超时</p>" in it) throw HttpRequestTimeoutException(builder)
        }
    }
    val content = CustomJson.decodeFromString<Temp<LongTextContent>>(json).data().content
    return content.replace("<br />", "\n").remove(SIGN)
}

class MicroBlogData(override val dir: File) : GameDataDownloader {
    val arknights get() = dir.readMicroBlogHistory(BlogUser.ARKNIGHTS)
    val byproduct get() = dir.readMicroBlogHistory(BlogUser.BYPRODUCT)
    val historicus get() = dir.readMicroBlogHistory(BlogUser.HISTORICUS)
    val mounten get() = dir.readMicroBlogHistory(BlogUser.MOUNTEN)
    val picture get() = dir.readMicroBlogPicture(BlogUser.PICTURE)

    val all get() = arknights + byproduct + historicus + mounten

    override val types get() = BlogUser.values().asIterable()
}

enum class BlogUser(val id: Long) : GameDataType {
    PICTURE(6279793937) {
        override val path: String = "BlogPicture(${id}).json"
        override val url: Url = Url("$BLOG_API?containerid=107803$id")
    },
    ARKNIGHTS(6279793937),
    BYPRODUCT(6441489862),
    MOUNTEN(7506039414),
    HISTORICUS(7499841383);

    override val duration: Long = 5_000L

    override val path = "Blog(${id}).json"

    override val url = Url("$BLOG_API?containerid=107603$id")
}

private val ImageServer = listOf("wx1", "wx2", "wx3", "wx4")

internal val ImageExtensions = mapOf(
    ContentType.Image.JPEG to "jpg",
    ContentType.Image.GIF to "gif",
    ContentType.Image.PNG to "png",
)

internal fun extension(pid: String) = ImageExtensions.values.first { it.startsWith(pid[21]) }

internal fun image(pid: String) = Url("https://${ImageServer.random()}.sinaimg.cn/large/${pid}.${extension(pid)}")

val MicroBlog.images get() = pictures.map { image(pid = it) }

val MicroBlog.content get() = raw ?: text.replace("<br />", "\n").remove(SIGN)

val MicroBlog.url get() = Url("https://weibo.com/${user?.id ?: "detail"}/${bid.ifBlank { id }}")

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
    val created: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("id")
    val id: Long,
    @SerialName("bid")
    val bid: String = "",
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("pic_ids")
    val pictures: Set<String> = emptySet(),
    @SerialName("raw_text")
    val raw: String? = null,
    @SerialName("retweeted_status")
    val retweeted: MicroBlog? = null,
    @SerialName("text")
    val text: String = "",
    @SerialName("user")
    val user: MicroBlogUser? = null,
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

@OptIn(ExperimentalSerializationApi::class)
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