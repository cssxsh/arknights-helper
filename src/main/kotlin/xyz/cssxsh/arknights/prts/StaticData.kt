package xyz.cssxsh.arknights.prts

import io.ktor.http.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.excel.*

public sealed class StaticData : CacheKey {
    public companion object {
        internal const val VOICE_TYPE_KEY = "xyz.cssxsh.arknights.prts.voice"
    }

    public abstract val character: String
    public abstract val type: ContentType

    public class Voice(character: Character, word: CharacterWord) : StaticData() {
        override val character: String = word.character
        override val filename: String = "${character.name}/${word.voiceTitle}.wav"
        override val type: ContentType = ContentType("audio", "wav")
        public val group: String = when {
            word.character == "char_4019_ncdeer" -> "voice_cn"
            "TTA" in word.wordKey || "CN_TOPOLECT" in word.wordKey -> "voice_custom"
            else -> System.getProperty(VOICE_TYPE_KEY, "voice")
        }
        override val url: String =
            "https://static.prts.wiki/${group}/${word.wordKey.replace("#", "_").lowercase()}/${word.voiceId}.wav"
    }
}