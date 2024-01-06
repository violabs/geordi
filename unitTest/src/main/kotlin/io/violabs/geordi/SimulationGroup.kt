package io.violabs.geordi

class SimulationGroup(private val properties: Array<out String>) {
    val content = mutableMapOf<String, Scenario>()

    fun with(vararg scenarioParts: Any?): SimulationGroup = apply {
        properties
            .mapIndexed { index, property -> property to scenarioParts[index] }
            .toMap()
            .let {
                val scenarioLabel = it.values.firstOrNull() ?: (content.size + 1).toString()

                content[scenarioLabel.toString()] = Scenario(it)
            }
    }

    fun isolate(): SimulationGroup = apply {
        content.values.last().isolated = true
    }

    fun ignore(): SimulationGroup = apply {
        TODO()
    }

    override fun toString(): String {
        return content.toString()
    }

    companion object {
        fun vars(vararg properties: String): SimulationGroup {
            return SimulationGroup(properties)
        }
    }

}