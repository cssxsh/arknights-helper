package xyz.cssxsh.mirai.arknights.command

import net.mamoe.mirai.console.command.*

sealed interface ArknightsHelperCommand : Command {

    companion object : Collection<ArknightsHelperCommand> {
        private val commands by lazy {
            ArknightsHelperCommand::class.sealedSubclasses.mapNotNull { kClass -> kClass.objectInstance }
        }

        override val size: Int get() = commands.size

        override fun contains(element: ArknightsHelperCommand): Boolean = commands.contains(element)

        override fun containsAll(elements: Collection<ArknightsHelperCommand>): Boolean = commands.containsAll(elements)

        override fun isEmpty(): Boolean = commands.isEmpty()

        override fun iterator(): Iterator<ArknightsHelperCommand> = commands.iterator()

        operator fun get(name: String): ArknightsHelperCommand = commands.first { it.primaryName.equals(name, true) }
    }
}