package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*
import xyz.cssxsh.arknights.*
import java.time.*

@Serializable
public data class Word(
    @SerialName("charDefaultTypeDict")
    val characterDefaultTypes: Map<String, VoiceLanguageType>,
    @SerialName("charWords")
    val characterWords: Map<String, CharacterWord>,
    @SerialName("defaultLangType")
    val defaultLanguageType: VoiceLanguageType,
    @SerialName("displayGroupTypeList")
    val displayGroupTypes: Set<VoiceLanguageGroupType>,
    @SerialName("displayTypeList")
    val displayTypes: Set<VoiceLanguageType>,
    @SerialName("newTagList")
    val newTags: Set<String>,
    @SerialName("playVoiceRange")
    val playVoiceRange: String,
    @SerialName("startTimeWithTypeDict")
    val startTimeWithTypes: Map<VoiceLanguageType, List<CharacterStartTime>>,
    @SerialName("voiceLangDict")
    val voiceLanguages: Map<String, CharacterVoiceLanguageInfo>,
    @SerialName("voiceLangGroupTypeDict")
    val voiceLanguageGroupTypes: Map<VoiceLanguageGroupType, VoiceLanguageGroupTypeInfo>,
    @SerialName("voiceLangTypeDict")
    val voiceLanguageTypes: Map<VoiceLanguageType, VoiceLanguageTypeInfo>
)

@Serializable
public enum class VoiceLanguageType {
    CN_MANDARIN,
    CN_TOPOLECT,
    JP,
    EN,
    KR,
    LINKAGE,
    ITA
}

@Serializable
public enum class VoiceLanguageGroupType {
    CN_MANDARIN,
    CUSTOM,
    JP,
    EN,
    KR,
    LINKAGE
}

@Serializable
public data class VoiceLanguageTypeInfo(
    @SerialName("name")
    val name: String,
    @SerialName("groupType")
    val groupType: VoiceLanguageGroupType
)

@Serializable
public data class VoiceLanguageGroupTypeInfo(
    @SerialName("name")
    val name: String,
    @SerialName("members")
    val members: List<VoiceLanguageType>
)

@Serializable
public data class CharacterStartTime(
    @SerialName("charSet")
    val characters: List<String>,
    @SerialName("timestamp")
    @Serializable(TimestampSerializer::class)
    val timestamp: OffsetDateTime
)

@Serializable
public data class CharacterVoiceLanguageInfo(
    @SerialName("wordkeys")
    val keys: Set<String>,
    @SerialName("charId")
    override val character: String,
    @SerialName("dict")
    val dict: Map<VoiceLanguageType, CharacterVoiceInfo>
) : CharacterId

@Serializable
public data class CharacterVoiceInfo(
    @SerialName("cvName")
    val voices: List<String>,
    @SerialName("voiceLangType")
    val language: VoiceLanguageType,
    @SerialName("voicePath")
    val path: String? = null,
    @SerialName("wordkey")
    val wordKey: String
)

@Serializable
public data class CharacterWord(
    @SerialName("charId")
    override val character: String,
    @SerialName("charWordId")
    val characterWordId: String,
    @SerialName("lockDescription")
    val lockDescription: String?,
    @SerialName("placeType")
    val placeType: String,
    @SerialName("unlockParam")
    val unlockParam: List<UnlockParam>,
    @SerialName("unlockType")
    val unlockType: String,
    @SerialName("voiceAsset")
    val voiceAsset: String,
    @SerialName("voiceId")
    val voiceId: String,
    @SerialName("voiceIndex")
    val voiceIndex: Int,
    @SerialName("voiceText")
    val voiceText: String,
    @SerialName("voiceTitle")
    val voiceTitle: String,
    @SerialName("voiceType")
    val voiceType: String,
    @SerialName("wordKey")
    val wordKey: String
) : CharacterId {

    @Serializable
    public data class UnlockParam(
        @SerialName("valueInt")
        val valueInt: Int,
        @SerialName("valueStr")
        val valueStr: String?
    )
}