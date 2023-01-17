package xyz.cssxsh.arknights.announce

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

public val AnnouncementMeta.focus: Announcement? get() = list.firstOrNull { it.id == focusId }

@Serializable
public enum class AnnounceType(override val url: String) : CacheKey {
    ANDROID("https://ak-conf.hypergryph.com/config/prod/announce_meta/Android/announcement.meta.json"),
    IOS("https://ak-conf.hypergryph.com/config/prod/announce_meta/IOS/announcement.meta.json"),
    BILIBILI("https://ak-conf.hypergryph.com/config/prod/announce_meta/Bilibili/announcement.meta.json");

    override val filename: String = "ANNOUNCE.${name}.json"
}

@Serializable
public data class AnnouncementMeta(
    @SerialName("announceList")
    val list: List<Announcement>,
    @SerialName("extra")
    val extra: AnnouncementExtra,
    @SerialName("focusAnnounceId")
    val focusId: Int? = null
)

@Serializable
public data class Announcement(
    @SerialName("announceId")
    val id: Int,
    @SerialName("day")
    val day: Int,
    @SerialName("group")
    val group: AnnouncementGroup,
    @SerialName("isWebUrl")
    val isWebUrl: Boolean,
    @SerialName("month")
    val month: Int,
    @SerialName("title")
    val title: String,
    @SerialName("webUrl")
    val webUrl: String = ""
) : CacheInfo {
    override val created: OffsetDateTime by lazy {
        try {
            val second = webUrl.substringAfterLast('_')
                .substringBeforeLast('.')
                .toLong()
            TimestampSerializer.timestamp(second = second)
        } catch (_: Exception) {
            LocalDate.now()
                .withMonth(month).withDayOfMonth(day)
                .atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
        }
    }
    public val type: AnnounceType by lazy {
        val value = webUrl
            .substringAfter("announce/")
            .substringBefore("/announcement")
            .uppercase()
        AnnounceType.valueOf(value)
    }
    override val url: String get() = webUrl
}

public enum class AnnouncementGroup {
    ACTIVITY,
    SYSTEM
}

@Serializable
public data class AnnouncementExtra(
    @SerialName("enable")
    val enable: Boolean,
    @SerialName("name")
    val name: String
)