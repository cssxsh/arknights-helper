package xyz.cssxsh.arknights.excel

import kotlinx.serialization.*

@Serializable
public data class Word(
    @SerialName("charDefaultTypeDict")
    val charDefaultTypeDict: Map<String, VoiceLangType>,
    @SerialName("charWords")
    val charWords: Map<String, CharWord>,
    @SerialName("defaultLangType")
    val defaultLangType: VoiceLangType,
    @SerialName("displayGroupTypeList")
    val displayGroupTypeList: Set<VoiceLangGroupType>,
    @SerialName("displayTypeList")
    val displayTypeList: Set<VoiceLangType>,
    @SerialName("newTagList")
    val newTagList: Set<String>,
    @SerialName("playVoiceRange")
    val playVoiceRange: String,
    @SerialName("startTimeWithTypeDict")
    val startTimeWithTypeDict: Map<VoiceLangType, List<ChatStartTime>>,
    @SerialName("voiceLangDict")
    val voiceLangDict: Map<String, ChatVoiceInfo>,
    @SerialName("voiceLangGroupTypeDict")
    val voiceLangGroupTypeDict: Map<VoiceLangGroupType, VoiceLangGroupTypeInfo>,
    @SerialName("voiceLangTypeDict")
    val voiceLangTypeDict: Map<VoiceLangType, VoiceLangTypeInfo>
)

@Serializable
public enum class VoiceLangType {
    CN_MANDARIN,
    CN_TOPOLECT,
    JP,
    EN,
    KR,
    LINKAGE,
    ITA
}

@Serializable
public enum class VoiceLangGroupType {
    CN_MANDARIN,
    CUSTOM,
    JP,
    EN,
    KR,
    LINKAGE
}

@Serializable
public data class VoiceLangTypeInfo(
    @SerialName("name")
    val name: String,
    @SerialName("groupType")
    val groupType: VoiceLangGroupType
)

@Serializable
public data class VoiceLangGroupTypeInfo(
    @SerialName("name")
    val name: String,
    @SerialName("members")
    val members: List<VoiceLangType>
)

@Serializable
public data class ChatStartTime(
    @SerialName("charSet")
    val chars: List<String>,
    @SerialName("timestamp")
    val timestamp: Long
)

@Serializable
public data class ChatVoiceInfo(
    @SerialName("wordkeys")
    val keys: Set<String>,
    @SerialName("charId")
    override val character: String,
    @SerialName("dict")
    val dict: Map<VoiceLangType, ChatVoiceDict>
) : CharacterId

@Serializable
public data class ChatVoiceDict(
    @SerialName("cvName")
    val cvName: String,
    @SerialName("voiceLangType")
    val voiceLangType: VoiceLangType,
    @SerialName("voicePath")
    val voicePath: String? = null,
    @SerialName("wordkey")
    val wordKey: String
)

@Serializable
public data class CharWord(
    @SerialName("charId")
    override val character: String,
    @SerialName("charWordId")
    val charWordId: String,
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