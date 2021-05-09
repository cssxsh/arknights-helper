package xyz.cssxsh.arknights

import org.junit.jupiter.api.Test
import xyz.cssxsh.arknights.excel.*

internal class ExcelKtTest : JsonTest() {

    @Test
    fun building() {
        excel.buffs.forEach { (name, list) ->
            println("=======[${name}]")
            list.forEach {
                println(it.name)
            }
        }
    }

    @Test
    fun character() {
        assert(excel.characters.isNotEmpty())
        /*
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
        }*/
        excel.characters.toSortedMap().forEach { (name, character) ->
            println("========[${name}]=======")
            println(character.talents())
            /*
            println("name: ${character.name}")
            println("appellation: ${character.appellation}")
            println("description: ${character.description}")
            println("displayNumber: ${character.displayNumber}")
            println("tags: ${character.tags}")
            println("group: ${character.group}")
            println("nation: ${character.name}")
            println("team: ${character.team}")
            println("isNotObtainable: ${character.isNotObtainable}")
            println("isSpecialCharacter: ${character.isSpecialCharacter}")
            println("itemDescription: ${character.itemDescription}")
            println("itemObtainApproach: ${character.itemObtainApproach}")
            println("itemUsage: ${character.itemUsage}")
            println("maxPotentialLevel: ${character.maxPotentialLevel}")
            println("position: ${character.position}")
            println("profession: ${character.profession}")
            println("rarity: ${character.rarity}")*/
        }
    }

    @Test
    fun const() {
        excel.const.playerApMap.withIndex().groupBy { it.value }.forEach { (value, list) ->
            println("==>${value}")
            println(list.size)
            println(list.map { it.index + 1 })
            // 1..5 => 80..90
            // 6..35 => 91..120
            // 36..85 => 120..130
            // 86..100 => 130..131
            // 101..135 => 131..135
        }
    }

    @Test
    fun gacha() {
        val recruit = excel.gacha.recruit()
        // println(recruit)
        buildString {
            excel.characters.recruit(words = setOf("生存", "减速"), recruit = recruit).forEach { (tags, result) ->
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
            // println(it)
        }
        excel.gacha.pools.open().forEach {
            println(it)
        }
        val obtain = excel.characters.values.rarities(2..5).obtain("招募寻访")
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
        obtain.pool(
            listOf(
                "#NORMAL",
                "******:0.02",
                "*****:0.08",
                "****:0.48",
                "***:0.42"
            )
        ).let {
            assert(it == normal)
        }
    }

    @Test
    fun handbook() {
        assert(excel.handbooks.isNotEmpty())
        val tags = excel.handbooks.tags()
        println(tags)
        excel.handbooks.forEach { (name, info) ->
            println("=======>$name")
            println(info.infos())
        }
    }

    @Test
    fun skill() {
        excel.skills.forEach { (name, skills) ->
            println("=======>$name")
            skills.forEach { skill ->
                println(skill.levels.map { it.name }.toSet())
            }
        }
    }

    @Test
    fun team() {
        excel.powers.forEach { (level, map) ->
            println("=======>${level.name}")
            map.forEach { (team, list) ->
                println("============>${team.name}")
                println(list)
            }
        }
    }

    @Test
    fun story() {
        excel.stories.values.flatten().forEach { story ->
            println("=====>${story.name}")
            println("Action: ${story.action}")
            println("Entry: ${story.entry}")
            println("StartTime: ${story.startTime}")
            println("startShowTime:${story.startShowTime}")
            println("EndTime:${story.endTime}")
            println("EndShowTime:${story.endShowTime}")
            println("RemakeStartTime:${story.remakeStartTime}")
            println("RemakeEndTime:${story.remakeEndTime}")
        }
    }

    @Test
    fun enemy() {
        excel.enemies.values.flatten().forEach { enemy ->
            println(enemy.name)
        }
    }

    @Test
    fun zone() {
        excel.weeks.forEach { (type, list) ->
            println("===>${type}")
            list.forEach { (zone, weekly) ->
                println(zone.title)
                println(weekly)
            }
        }
        excel.zones.forEach { (type, list) ->
            println("===>${type}")
            list.forEach { zone ->
                if (zone.title.isNotEmpty()) {
                    println("------")
                    println(zone.type)
                    println(zone.title)
                }
            }
        }
    }
}