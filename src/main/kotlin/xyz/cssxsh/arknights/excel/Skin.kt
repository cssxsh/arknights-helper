package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class SkinTable(
    @SerialName("brandList")
    val brands: Map<String, SkinBrand>,
    @SerialName("buildinEvolveMap")
    val buildingEvolves: Map<String, Map<Int, String>>,
    @SerialName("buildinPatchMap")
    val buildingPatches: Map<String, Map<String, String>>,
    @SerialName("charSkins")
    val characterSkins: Map<String, CharacterSkin>,
    @SerialName("specialSkinInfoList")
    val specialSkinInfos: List<SpecialSkinInfo>
)

@Serializable
public data class SkinBrand(
    @SerialName("brandCapitalName")
    val capital: String,
    @SerialName("brandId")
    val brandId: String,
    @SerialName("brandName")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("groupList")
    val groups: List<SkinGroup>,
    @SerialName("kvImgIdList")
    val images: List<KvImage>,
    @SerialName("sortId")
    val sortId: Int = 0,
    @SerialName("publishTime")
    @Serializable(TimestampSerializer::class)
    val publish: OffsetDateTime
)

@Serializable
public data class SkinGroup(
    @SerialName("skinGroupId")
    val groupId: String,
    @SerialName("publishTime")
    @Serializable(TimestampSerializer::class)
    val publish: OffsetDateTime
)

@Serializable
public data class KvImage(
    @SerialName("kvImgId")
    val kvImgId: String,
    @SerialName("linkedSkinGroupId")
    val linkedSkinGroupId: String
)

@Serializable
public data class SpecialSkinInfo(
    @SerialName("skinId")
    val skinId: String,
    @SerialName("startTime")
    @Serializable(TimestampSerializer::class)
    override val start: OffsetDateTime,
    @SerialName("endTime")
    @Serializable(TimestampSerializer::class)
    override val end: OffsetDateTime
) : Period

@Serializable
public data class CharacterSkin(
    @SerialName("avatarId")
    val avatarId: String,
    @SerialName("battleSkin")
    val battle: BattleSkin,
    @SerialName("buildingId")
    val buildingId: String?,
    @SerialName("charId")
    override val character: String,
    @SerialName("displaySkin")
    val display: DisplaySkin,
    @SerialName("dynEntranceId")
    val dynEntranceId: String?,
    @SerialName("dynIllustId")
    val dynIllustId: String?,
    @SerialName("dynPortraitId")
    val dynPortraitId: String?,
    @SerialName("illustId")
    val illustId: String? = null,
    @SerialName("isBuySkin")
    val isBuySkin: Boolean,
    @SerialName("portraitId")
    val portraitId: String? = null,
    @SerialName("skinId")
    val skinId: String,
    @SerialName("tmplId")
    val tmplId: String?,
    @SerialName("tokenSkinMap")
    val tokens: List<TokenSkin>?,
    @SerialName("voiceId")
    val voiceId: String?,
    @SerialName("voiceType")
    val voiceType: String
) : CharacterId

@Serializable
public data class BattleSkin(
    @SerialName("overwritePrefab")
    val overwritePrefab: Boolean,
    @SerialName("skinOrPrefabId")
    val skinOrPrefabId: String? = null
)

@Serializable
public data class DisplaySkin(
    @SerialName("colorList")
    val colors: List<String>? = null,
    @SerialName("content")
    val content: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("designerList")
    override val designers: List<String>? = null,
    @SerialName("dialog")
    val dialog: String? = null,
    @SerialName("displayTagId")
    val displayTagId: String?,
    @SerialName("drawerList")
    override val illusts: List<String>? = null,
    @SerialName("getTime")
    @Serializable(TimestampSerializer::class)
    val getTime: OffsetDateTime,
    @SerialName("modelName")
    val modelName: String? = null,
    @SerialName("obtainApproach")
    val obtainApproach: String? = null,
    @SerialName("onPeriod")
    val onPeriod: Int,
    @SerialName("onYear")
    val onYear: Int,
    @SerialName("skinGroupId")
    val skinGroupId: String? = null,
    @SerialName("skinGroupName")
    val skinGroupName: String? = null,
    @SerialName("skinGroupSortIndex")
    val skinGroupSortIndex: Int,
    @SerialName("skinName")
    val skinName: String? = null,
    @SerialName("sortId")
    val sortId: Int,
    @SerialName("titleList")
    val titles: List<String>? = null,
    @SerialName("usage")
    val usage: String? = null
) : Illust

@Serializable
public data class TokenSkin(
    @SerialName("tokenId")
    val tokenId: String,
    @SerialName("tokenSkinId")
    val tokenSkinId: String
)