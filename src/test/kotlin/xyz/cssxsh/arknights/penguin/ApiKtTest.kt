package xyz.cssxsh.arknights.penguin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import xyz.cssxsh.arknights.*
import xyz.cssxsh.mirai.plugin.*

internal class ApiKtTest : JsonTest() {

    init {
        runBlocking {
            PenguinDataType.values().toList().download(dir = data, flush = true)
        }
    }

    private fun Pair<Matrix, Stage>.getContent() = buildString {
        appendLine("概率: ${first.quantity}/${first.times}=${first.probability}")
        appendLine("单件期望理智: $single")
        appendLine("最短通关用时: ${stage.clear}")
        appendLine("单件期望用时: $short")
    }

    private fun item(name: String, limit: Int) = buildString {
        val (item, list) = (PenguinData.items to PenguinData.matrices.now()).item(name)
        appendLine("${item.alias.get()} 统计结果 By 企鹅物流数据统计")
        if (list.isEmpty()) {
            appendLine("列表为空，请尝试更新数据")
            return@buildString
        }
        var count = 0
        (list with PenguinData.stages).sortedBy { it.single }.forEach { pair ->
            if (pair.stage.isGacha || count >= limit) return@forEach
            appendLine("=======> 作战: ${pair.stage.code} (cost=${pair.stage.cost}) ")
            append(pair.getContent())
            count++
        }
    }

    private fun stage(code: String, limit: Int) = buildString {
        val (stage, list) = (PenguinData.stages to PenguinData.matrices.now()).stage(code)
        appendLine("[${stage.code}] (cost=${stage.cost}) 统计结果 By 企鹅物流数据统计")
        if (list.isEmpty()) {
            appendLine("列表为空，请尝试更新数据")
            return@buildString
        }
        var count = 0
        (list with PenguinData.items).sortedByDescending { it.rarity }.forEach { (matrix, item) ->
            if (item.type != ItemType.MATERIAL || count >= limit) return@forEach
            appendLine("=======> 掉落: ${item.name} (rarity=${item.rarity}) ")
            append((matrix to stage).getContent())
            count++
        }
    }

    private fun zone(name: String, limit: Int) = buildString {
        val (_, list) = (PenguinData.zones to PenguinData.stages).name(name)
        list.forEach { stage ->
            append(stage(stage.code, limit))
        }
    }

    @Test
    fun items() {
        assertTrue(penguin.items.isNotEmpty())
        penguin.items.name("海胆").let {
            assertTrue(it.type == ItemType.MATERIAL)
            println(it)
        }
        penguin.items.types().forEach { (type, list) ->
            assertTrue(type in ItemType.values())
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
        penguin.items.types(ItemType.MATERIAL).forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
        penguin.items.rarities().forEach { (rarity, list) ->
            println("===> $rarity")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
        penguin.items.rarities(3).forEach { (rarity, list) ->
            println("===> $rarity")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
    }

    @Test
    fun stages() {
        assertTrue(penguin.stages.isNotEmpty())
        penguin.stages.name("0-1").let {
            assertTrue(it.type == StageType.MAIN)
            println(it)
        }
        penguin.stages.types().forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.code })
        }
        penguin.stages.types(StageType.DAILY).forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.code })
        }
        (penguin.stages to penguin.items).drop("海胆").types().forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.code })
        }
        penguin.stages.gacha(true).types().forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.code })
        }
        penguin.stages.drop(penguin.items.name("海胆")).cost().forEach { (cost, list) ->
            println("===> $cost")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.code })
        }
    }

    @Test
    fun zones() {
        assertTrue(penguin.zones.isNotEmpty())
        penguin.zones.name("序章").let {
            assertTrue(it.type == ZoneType.MAINLINE)
            println(it)
        }
        penguin.zones.types().forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
        penguin.zones.types(ZoneType.WEEKLY).forEach { (type, list) ->
            println("===> $type")
            assertTrue(list.isNotEmpty())
            println(list.joinToString { it.name })
        }
        println(zone("覆潮之下", 3))
    }

    @Test
    fun period() {
        println(penguin.period.size)
    }

    @Test
    fun stats() {
        println(penguin.stats.totalCost)
    }

    @Test
    fun matrices() {
        assertTrue(penguin.matrices.isNotEmpty())
        (penguin.items to penguin.matrices.now()).item("刺球").let { (item, list) ->
            println("[${item.name}] 统计结果")
            list.sortedByDescending { it.probability }.subList(0, minOf(5, list.size - 1)).forEach { matrix ->
                val stage = penguin.stages.id(matrix.stageId)
                if (stage.isGacha) return@forEach
                println("作战: ${stage.code} (cost=${stage.cost}) ")
                println("${matrix.start}~${matrix.end}")
                println("概率: ${matrix.quantity}/${matrix.times}=${matrix.probability}")
                println("单件期望理智: ${(stage.cost * matrix.probability)}")
                println("最短通关用时: ${stage.clear}")
                println("单件期望用时: ${(stage.clear / matrix.probability)}")
            }
        }
        (penguin.stages to penguin.matrices).stage("0-1").let { (stage, list) ->
            println(stage.code)
            assertTrue(list.isNotEmpty())
            list.forEach {
                println(it.stageId)
                println(it.probability)
            }
        }
        penguin.matrices.now().forEach {
            println(it.stageId)
            println(it.probability)
        }
    }
}