package xyz.cssxsh.arknights.market

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import net.mamoe.mirai.message.data.MarketFace
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString
import xyz.cssxsh.arknights.*
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.full.declaredMemberProperties

typealias FaceItemMap = MutableMap<Int, String>

private val item = { id: Int -> Url("https://gxh.vip.qq.com/qqshow/admindata/comdata/vipEmoji_item_$id/xydata.json") }

fun ArknightsFaceMap(data: MarketFaceMap, items: FaceItemMap): ArknightsFaceMap = data.mapValues { entry ->
    entry.value.face.md5s.map { info ->
        object : ArknightsFace {
            override val id: Int by entry::key
            override val key: String by ReadOnlyProperty { _, _ -> items.getValue(id) }
            override val md5: String by info::md5
            override val content: String by info::name
            override val title: String by entry.value.app::name
        }
    }
}

private fun FaceItemMap.types() = mapValues { (id, _) ->
    object : GameDataType {
        override val path: String = "$id.json"
        override val url: Url = item(id)
    }
}

fun File.readMarketFaceMap(items: FaceItemMap): MarketFaceMap = items.types().mapValues { (_, type) -> read(type) }

data class ArknightsFaceData(override val dir: File, val items: FaceItemMap): GameDataDownloader {
    val data by lazy { dir.readMarketFaceMap(items) }
    val faces by lazy { ArknightsFaceMap(data, items) }

    override val types: Iterable<GameDataType> get() = items.types().values
}

typealias MarketFaceMap = Map<Int, MarketFaceData>

typealias ArknightsFaceMap = Map<Int, List<ArknightsFace>>

val ArknightsFace.image
    get() = Url("https://gxh.vip.qq.com/club/item/parcel/item/${md5.substring(0, 2)}/$md5/300x300.png")

val ArknightsFace.detail
    get() = Url("https://zb.vip.qq.com/hybrid/emoticonmall/detail?id=$id")

val DefaultItems: FaceItemMap by lazy {
    mutableMapOf(
        // 官方
        209583 to "4b4fc6ec7911520c",
        209578 to "d0f95a43b4b9185c",
        208870 to "0f1b1c61b1ddd151",
        208672 to "87303e3409eaef84",
        208584 to "72e1c9ef0a8199f7",
        208406 to "350953236ddc143e",
        208405 to "af0784e0933b3247",
        208293 to "0ef16448e9a175ca",
        208145 to "fa538ccdff18c698",
        208070 to "36072e78f46c8545",
        207738 to "b76b01ec8710b0f9",
        207399 to "aee2526b091c00d1",
        207222 to "bd71d2d5cc25f9eb",
        206976 to "753f520d766feec9",
        206732 to "f45dafcf1804cae2",
        206352 to "7d9bbc07ce293e2f",
        206372 to "ecfc925779fa506f",
        206296 to "0509fe705f0fd64d",
        205782 to "2608036d23a72681",
        205510 to "049ab4fa1be291cf",
        205284 to "935be2d807356258",
        205129 to "940ace4d97232549",
        205061 to "1776acfb8054f8b8",
        204811 to "2cc36e2aaa9c6ece",
        204705 to "00ce0e107391a646",
        204640 to "c20c4db38715c697",
        204500 to "265ad63dff26a8a3",
        204235 to "41ef554fafa04c2d",
        204020 to "89ae01b6bef173ee",
        203857 to "b21407c20c5597a9",
        203764 to "b89f0b1c321192be",
        203470 to "9df977ccd4794f1c",
        203469 to "03ffa370f7e0570d",
        203391 to "e3f69eada31f5294",
        203319 to "757c5a66ca40bab9",
        203318 to "cf479e7c6f0cda90",
        203173 to "c01f45b9a9c1e400",
        203172 to "de0359b402e9c875",
        // 非官方
        205004 to "724f54b2b3f38c90",
        206993 to "91059a4984f182bd",
        206154 to "adb04f2815302da1",
        208165 to "6ef02b55fb4e9ab2",
        209030 to "391484b807727128",
        207070 to "af6f7e0850c46ec7",
        205148 to "6d334fb573e4bd18",
        205242 to "8da95ce6841b88bb",
        209118 to "114131ec35b56cfd",
        207865 to "9fb5ee9294660231",
        208246 to "d0df7f6fa8f75f55",
        209610 to "14fd7dbc47bb0ff8",
        209571 to "d98c062cdf877a2f",
        209306 to "94bab60029cc1be4",
    ).toSortedMap()
}

@Serializable
data class MarketFaceData(
    @SerialName("appData")
    val app: AppData,
    @SerialName("data")
    val face: FaceData,
    @SerialName("timestamp")
    val timestamp: Long,
)

@Serializable
data class AppData(
    @SerialName("name")
    val name: String,
)

@Serializable
data class FaceData(
    @SerialName("additionInfo")
    private val additions: JsonArray,
    @SerialName("baseInfo")
    val bases: List<BaseInfo>,
    @SerialName("__bgColors")
    private val bgColors: JsonObject,
    @SerialName("diversionConfig")
    private val diversionConfig: JsonArray,
    @SerialName("diyEmojiCommonText")
    private val diyEmojiCommonText: JsonArray,
    @SerialName("md5Info")
    val md5s: List<Md5Info>,
    @SerialName("operationInfo")
    private val operations: JsonArray,
)

@Serializable
data class BaseInfo(
    @SerialName("authorId")
    val author: Int,
    @SerialName("childMagicEmojiId")
    private val childMagicEmojiId: String,
    @SerialName("desc")
    val description: String,
    @SerialName("favourite")
    val favourite: Int,
    @SerialName("feeType")
    val fee: Int,
    @SerialName("icon")
    val icon: Int,
    @SerialName("_id")
    private val id_: String,
    @SerialName("id")
    val id: String,
    @SerialName("isApng")
    val isApng: Int,
    @SerialName("isOriginal")
    val isOriginal: Int,
    @SerialName("label")
    val label: List<String>,
    @SerialName("name")
    val name: String,
    @SerialName("providerId")
    val provider: Int,
    @SerialName("QQgif")
    val gif: Int,
    @SerialName("qzone")
    val zone: Int,
    @SerialName("realSize")
    val size: Int,
    @SerialName("ringType")
    val ring: Int,
    @SerialName("sex")
    val sex: Int,
    @SerialName("sougou")
    val sougou: Int,
    @SerialName("tag")
    val tag: String,
    @SerialName("type")
    val type: Int,
    @SerialName("updateTipBeginTime")
    private val updateTipBeginTime: Int,
    @SerialName("updateTipEndTime")
    private val updateTipEndTime: Int,
    @SerialName("updateTipWording")
    private val updateTipWording: String,
    @SerialName("valid")
    val valid: Int,
    @SerialName("validArea")
    val validArea: Int,
    @SerialName("validBefore")
    val validBefore: String,
    @SerialName("zip")
    val zip: String,
)

@Serializable
data class Md5Info(
    @SerialName("md5")
    val md5: String,
    @SerialName("name")
    val name: String,
)

interface ArknightsFace {
    /**
     * 同一个表情包 key 一样
     */
    val key: String

    /**
     * 图片 md5
     */
    val md5: String

    /**
     * 表情 content
     */
    val content: String

    /**
     * 表情包 id
     */
    val id: Int

    /**
     * 表情包名称
     */
    val title: String

    /**
     * 明日方舟斯卡蒂
     * [https://zb.vip.qq.com/hybrid/emoticonmall/detail?id=209583]
     */
    enum class Skadi(override val content: String, override val md5: String) : ArknightsFace {
        SING("唱歌", "a580df9d18500c2493baf5a0da24daf2"),
        CRY("哭", "fb9200ead299cf537e65ec03d3742f03"),
        DRAG("拖拽", "c59047e56074b79160e2d6ac5f0c7eac"),
        PUFF("噗噗", "aab71545de693c5d93a1d54a8c9a8e64"),
        HUG("拥抱", "06587d73cc5eb545dd638dd402128eeb"),
        ANGRY("生气", "9664b677235954217e95f5eff9f7f896"),
        WIPE("擦汗", "baf8d4034f58acad2ecaa9a06acb91a2"),
        GAZE("凝视", "bba590a4427e75e3cb4541fca24045cd"),
        SWIM("游泳", "9f56b8605ec9739c019df342e4a8694a"),
        EAT("吃东西", "5d83709789bf907017c5db9e6770b591"),
        SUNSET("夕阳", "9dc1acbce62724a928ab1cec0cb18497"),
        SLEEP("睡觉", "43b10bb9712f419f62ec3d6f37dcbffa"),
        FIGHT("打架", "84f7d247c4bc3e4795c3d49ac798e5d7"),
        DAZE("发呆", "963fca6346eb4ce2149d8d41dbccfba5"),
        BREAD("两片面包", "eea4dac29830b937c5a0420a18949a63"),
        DOUBT("疑惑", "a69f3b12887e3657e2ad8ac01a4b5a14");

        override val id: Int get() = 209583

        override val key: String get() = "4b4fc6ec7911520c"

        override val title: String get() = "明日方舟斯卡蒂"
    }
}

private val MarketFaceImplKClass by lazy {
    Class.forName("net.mamoe.mirai.internal.message.MarketFaceImpl").kotlin
}

private val MarketFaceBodyKClass by lazy {
    Class.forName("net.mamoe.mirai.internal.network.protocol.data.proto.ImMsgBody").kotlin.nestedClasses.first {
        it.simpleName == "MarketFace"
    }
}

/**
 * 构建 MarketFace 实例
 */
fun ArknightsFace.impl(): MarketFace {
    val body = MarketFaceBodyKClass.constructors.first { it.parameters.size == 13 }.run {
        callBy(parameters.associateWith { parameter ->
            when (parameter.name) {
                "faceId" -> md5.decodeHex().toByteArray()
                "faceInfo" -> 1
                "faceName" -> "[$content]".toByteArray()
                "imageHeight" -> 200
                "imageWidth" -> 200
                "itemType" -> 6
                "key" -> key.toByteArray()
                "mediaType" -> 0
                "mobileParam" -> byteArrayOf()
                "param" -> byteArrayOf()
                "pbReserve" -> byteArrayOf()
                "subType" -> 3
                "tabId" -> id
                else -> throw IllegalArgumentException()
            }
        })
    }
    return MarketFaceImplKClass.constructors.first { it.parameters.size == 1 }.call(body) as MarketFace
}

val MarketFace.md5 by ReadOnlyProperty { face, _ ->
    requireNotNull(face::class == MarketFaceImplKClass) { "需要MarketFaceImpl实例作为参数" }
    val delegate = MarketFaceImplKClass.declaredMemberProperties.first { it.name == "delegate" }.getter.call(face)
    val md5 = MarketFaceBodyKClass.declaredMemberProperties.first { it.name == "faceId" }.getter.call(delegate) as ByteArray
    md5.toByteString().hex()
}

val MarketFace.hash by ReadOnlyProperty { face, _ ->
    requireNotNull(face::class == MarketFaceImplKClass) { "需要MarketFaceImpl实例作为参数" }
    val delegate = MarketFaceImplKClass.declaredMemberProperties.first { it.name == "delegate" }.getter.call(face)
    val hash = MarketFaceBodyKClass.declaredMemberProperties.first { it.name == "key" }.getter.call(delegate) as ByteArray
    hash.toString()
}
