package xyz.cssxsh.arknights

import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.excel.*

internal class ExcelKtTest : JsonTest {

    private val character by lazy { dir.readCharacterTable() }

    private val gacha by lazy { dir.readGachaTable() }

    private val handbook by lazy { dir.readHandbookTable() }

    @Test
    fun character() {
        assert(character.isNotEmpty())
        character.values.let { characters ->
            println(characters.flatMap { it.tags.orEmpty() }.toSet())
            println(characters.group("sui").map { it.name })
            println(characters.nation("yan").map { it.name })
            println(characters.team("lee").map { it.name })
            println(characters.obtain("招募寻访").map { it.name })
            println(characters.position(PositionType.ALL).map { it.name })
            println(characters.professions(ProfessionType.TOKEN).map { it.name })
            println(characters.professions(ProfessionType.SPECIALS).map { it.name })
            println(characters.obtainable(false).map { it.name })
            println(characters.special().map { it.name })
            println(characters.potential(1).map { it.name })
            println(characters.general(false).map { it.name })
            println(characters.rarities(1, 2).map { it.name })
            println(characters.rarities(1..2).map { it.name })
            println(characters.rarities(listOf(1, 2)).map { it.name })
            println(characters.tags("新手").map { it.name })
            println(characters.tags("新手", "防护").map { it.name })
            println(characters.tags(listOf("新手", "费用回复")).map { it.name })
        }
        character.toSortedMap().forEach { (name, char) ->
            println("========[${name}]=======")
            println("name: ${char.name}")
            println("appellation: ${char.appellation}")
            println("description: ${char.description}")
            println("displayNumber: ${char.displayNumber}")
            println("tags: ${char.tags}")
            println("group: ${char.group}")
            println("nation: ${char.nation}")
            println("team: ${char.team}")
            println("isNotObtainable: ${char.isNotObtainable}")
            println("isSpecialCharacter: ${char.isSpecialCharacter}")
            println("itemDescription: ${char.itemDescription}")
            println("itemObtainApproach: ${char.itemObtainApproach}")
            println("itemUsage: ${char.itemUsage}")
            println("maxPotentialLevel: ${char.maxPotentialLevel}")
            println("position: ${char.position}")
            println("profession: ${char.profession}")
            println("rarity: ${char.rarity}")
        }
    }

    @Test
    fun gacha() {
        val recruit = gacha.recruit()
        println(recruit)
        buildString {
            character.recruit(words = setOf("生存", "减速"), recruit = recruit).forEach { (tags, result) ->
                append("====> $tags ")
                if ((result.keys - 0).all { it >= 3 }) {
                    appendLine("${(result.keys - 0).minOrNull()!! + 1}星保底")
                } else {
                    appendLine("")
                }
                result.toSortedMap().forEach { (rarity, character) ->
                    appendLine("${rarity + 1}星干员: ${character.map { it.name }}")
                }
            }
        }.let {
            println(it)
        }
        gacha.pools.open().forEach {
            println(it)
        }
        val obtain = character.values.rarities(2..5).obtain("招募寻访")
        val normal: PoolData = listOf(
            obtain.rarities(5) to 0.02,
            obtain.rarities(4) to 0.08,
            obtain.rarities(3) to 0.48,
            obtain.rarities(2) to 0.42
        )
        val result: RecruitResult = (1..10).map { gacha(pool = normal) }.groupBy { it.rarity }
        result.toSortedMap().forEach { (rarity, character) ->
            println("${rarity + 1}星干员: ${character.map { it.name }}")
        }
    }

    @Test
    fun handbook() {
        assert(handbook.character.isNotEmpty())
        handbook.sex().map { (male, infos) ->
            println(male)
            println(infos.size)
        }
    }
}