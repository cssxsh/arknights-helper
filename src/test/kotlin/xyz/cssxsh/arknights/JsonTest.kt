package xyz.cssxsh.arknights

import java.io.File

interface JsonTest {

    val dir: File get() = File("./test/ArknightsGameData")
}