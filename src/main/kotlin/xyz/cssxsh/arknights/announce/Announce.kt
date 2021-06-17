package xyz.cssxsh.arknights.announce

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.arknights.*
import java.io.File
import java.time.LocalDate

val Announcement.date: LocalDate get() = LocalDate.now().withMonth(month).withDayOfMonth(day)

val Announcement.web: Url get() = Url(webUrl)

val AnnouncementMeta.focus get() = list.first { it.id == focusId }

enum class AnnounceType(platform: String) : GameDataType {
    ANDROID("Android"),
    IOS("IOS"),
    BILIBILI("Bilibili");

    override val path: String = "$platform.json"

    override val url: Url = Url("https://ak-fs.hypergryph.com/announce/$platform/announcement.meta.json")
}

class AnnouncementData(override val dir: File): GameDataDownloader {
    val android get() = dir.read<AnnouncementMeta>(AnnounceType.ANDROID)
    val bilibili get() = dir.read<AnnouncementMeta>(AnnounceType.BILIBILI)
    val ios get() = dir.read<AnnouncementMeta>(AnnounceType.IOS)

    val all get() = (android.list + bilibili.list + ios.list).sortedBy { it.id }

    override val types get() = AnnounceType.values().asIterable()
}

@Serializable
data class AnnouncementMeta(
    @SerialName("announceList")
    val list: List<Announcement>,
    @SerialName("extra")
    val extra: AnnouncementExtra,
    @SerialName("focusAnnounceId")
    val focusId: Int? = null
)

@Serializable
data class Announcement(
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
)

enum class AnnouncementGroup {
    ACTIVITY,
    SYSTEM
}

@Serializable
data class AnnouncementExtra(
    @SerialName("enable")
    val enable: Boolean,
    @SerialName("name")
    val name: String
)