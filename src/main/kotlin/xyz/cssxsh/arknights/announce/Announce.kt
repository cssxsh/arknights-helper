package xyz.cssxsh.arknights.announce

import io.ktor.http.*
import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.*

public val Announcement.date: LocalDate get() = LocalDate.now().withMonth(month).withDayOfMonth(day)

public val Announcement.web: Url get() = Url(webUrl)

public val AnnouncementMeta.focus: Announcement? get() = list.firstOrNull { it.id == focusId }

@Serializable
public enum class AnnounceType(platform: String): CacheKey {
    ANDROID("Android"),
    IOS("IOS"),
    BILIBILI("Bilibili");

    override val filename: String = "${platform}.json"

    public override val url: Url =
        Url("https://ak-conf.hypergryph.com/config/prod/announce_meta/$platform/announcement.meta.json")
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
    @Transient
    override var created: OffsetDateTime = OffsetDateTime.now().withMonth(month).withDayOfMonth(day)
        internal set
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