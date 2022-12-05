package xyz.cssxsh.arknights.prts

import xyz.cssxsh.arknights.*
import xyz.cssxsh.arknights.excel.*

public sealed class StaticData : CacheKey {
    public companion object {
        internal const val VOICE_TYPE_KEY = "xyz.cssxsh.arknights.prts.voice"
    }
    public abstract val character: String
    public abstract val type: String

    public class Voice(word: CharWord): StaticData() {
        override val character: String = word.character
        override val filename: String = "${word.charWordId}.wav"
        override val type: String = System.getProperty(VOICE_TYPE_KEY, "voice")
        override val url: String = when {
            word.wordKey != word.character -> "https://static.prts.wiki/voice_custom/${word.wordKey.lowercase()}/${word.voiceId}.wav"
            else -> "https://static.prts.wiki/$type/${word.wordKey.lowercase()}/${word.voiceId}.wav"
        }
    }
}