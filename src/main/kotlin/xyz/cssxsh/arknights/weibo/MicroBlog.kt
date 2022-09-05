package xyz.cssxsh.arknights.weibo

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import xyz.cssxsh.arknights.*
import java.time.*
import java.time.format.*
import java.util.*

internal const val BLOG_API: String = "https://m.weibo.cn/api/container/getIndex"

internal const val CONTENT_API: String = "https://m.weibo.cn/statuses/extend"

@Serializable
public enum class BlogUser(public val id: Long) : CacheKey {
    ARKNIGHTS(6279793937),
    BYPRODUCT(6441489862),
    MOUNTEN(7506039414),
    HISTORICUS(7499841383);

    override val filename: String = "Blog(${id}).json"

    override val url: String get() = BLOG_API
}

private val ImageServer = listOf("wx1", "wx2", "wx3", "wx4")

internal val ImageExtensions = listOf("jpg", "gif", "png")

internal fun extension(pid: String) = ImageExtensions.first { it.startsWith(pid[21]) }

internal fun image(pid: String) = "https://${ImageServer.random()}.sinaimg.cn/large/${pid}.${extension(pid)}"

public val MicroBlog.url: String get() = "https://weibo.com/${user.id}/${bid.ifBlank { id }}"

internal fun <T> Temp<T>.data() = requireNotNull(data) { message }

internal fun PictureData.blogs(): Sequence<MicroBlog> = sequence {
    for (card in cards) {
        for (group in card.group) {
            for (picture in group.pictures) {
                (picture.blog)
            }
        }
    }
}

internal fun WeiboData.blogs(): Sequence<MicroBlog> = sequence {
    for (card in cards) {
        yield(card.blog ?: continue)
    }
}

@Serializable
internal data class Temp<T>(
    @SerialName("data")
    val `data`: T?,
    @SerialName("msg")
    val message: String = "",
    @SerialName("ok")
    val ok: Int
)

@Serializable
internal data class WeiboData(
    @SerialName("cards")
    val cards: List<Card> = emptyList(),
)

@Serializable
internal data class Card(
    @SerialName("mblog")
    val blog: MicroBlog? = null,
)

@Serializable
public data class MicroBlog(
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    override val created: OffsetDateTime = OffsetDateTime.now(),
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
    val user: MicroBlogUser = MicroBlogUser("", "", 0, ""),
) : CacheInfo {
    override val url: String by lazy { "https://weibo.com/${user.id}/${bid.ifBlank { id }}" }
}

@Serializable
public data class MicroBlogUser(
    @SerialName("avatar_hd")
    val avatar: String,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("screen_name")
    val name: String
)

internal object WeiboDateTimeSerializer : KSerializer<OffsetDateTime> {

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
public data class LongTextContent(
    @SerialName("longTextContent")
    val content: String,
    @SerialName("ok")
    val ok: Int,
)

@Serializable
internal data class PictureData(
    @SerialName("cards")
    val cards: List<PictureCard> = emptyList()
)

@Serializable
internal data class PictureCard(
    @SerialName("card_group")
    val group: List<PictureCardGroup> = emptyList()
)

@Serializable
internal data class PictureCardGroup(
    @SerialName("pics")
    val pictures: List<PictureItem> = emptyList()
)

@Serializable
internal data class PictureItem(
    @SerialName("mblog")
    val blog: MicroBlog,
)