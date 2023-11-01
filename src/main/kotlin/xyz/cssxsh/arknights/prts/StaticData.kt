package xyz.cssxsh.arknights.prts

import io.ktor.http.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.excel.*

public sealed class StaticData : CacheKey {
    public abstract val character: String
    public abstract val type: ContentType

    public class Voice(character: Character, word: CharacterWord, voice: CharacterVoiceInfo) : StaticData() {
        override val character: String = word.character
        override val filename: String = "${character.name}/${word.voiceId}_${word.voiceTitle}_${voice.language}.wav"
        override val type: ContentType = ContentType("audio", "wav")
        public val group: String = when (voice.language) {
            VoiceLanguageType.JP -> "voice"
            VoiceLanguageType.CN_MANDARIN -> "voice_cn"
            VoiceLanguageType.CN_TOPOLECT, VoiceLanguageType.ITA, VoiceLanguageType.RUS, VoiceLanguageType.GER -> "voice_custom"
            VoiceLanguageType.EN -> "voice_en"
            VoiceLanguageType.KR -> "voice_kr"
            VoiceLanguageType.LINKAGE -> (voice.path ?: "voice")
                .removeSuffix("/").substringAfterLast("/").lowercase()
        }
        override val url: String =
            "https://static.prts.wiki/${group}/${voice.wordKey.replace("#", "_").lowercase()}/${word.voiceId}.wav"
    }
}