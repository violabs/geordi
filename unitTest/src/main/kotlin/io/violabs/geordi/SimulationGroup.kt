package io.violabs.geordi

/**
 * This class is designed to manage and organize multiple scenarios within a simulation environment.
 * Each simulation group can contain multiple scenarios, each identified and organized based on specific properties.
 * Used by [WarpDriveEngine] to generate tests for each scenario.
 *
 * @property properties An array of property names that are used to identify different parts of a scenario.
 */
class SimulationGroup(private val properties: Array<out String>) {

    // The map associates a string label with a `Scenario` object.
    // The label is derived from the properties of the scenario.
    // if provided, use the scenario label, otherwise use an index for the key.
    val content = mutableMapOf<String, Scenario>()

    /**
     * Adds a new scenario to the simulation group.
     *
     * This method takes a variable number of arguments representing parts of a scenario. It maps these parts to
     * the provided properties and adds the resulting scenario to the group.
     *
     * @param scenarioParts Variable number of arguments representing different parts of a scenario.
     * @return The current instance of [SimulationGroup] for chaining calls.
     */
    // TODO: Add support for validating size must match. Possibly, type must match
    fun with(vararg scenarioParts: Any?): SimulationGroup = apply {
        properties
            .mapIndexed { index, property -> property to scenarioParts[index] }
            .toMap()
            .let {
                val scenarioLabel = it.values.firstOrNull() ?: (content.size + 1).toString()

                content[scenarioLabel.toString()] = Scenario(it)
            }
    }

    fun with(fn: SimEx.() -> Unit): SimulationGroup {
        val ex = SimEx(properties, content)

        fn(ex)

        if (ex.rawScenario.isNotEmpty()) {
            val scenarioLabel = ex.rawScenario.values.firstOrNull() ?: (content.size + 1).toString()

            content[scenarioLabel.toString()] = Scenario(ex.rawScenario)
        }

        return this
    }

    /**
     * Marks the last added scenario as isolated.
     *
     * This method sets the `isolated` flag of the most recently added scenario to `true`.
     * It indicates that this scenario is to be run in isolation. In this instance, isolation does not
     * mean only one scenario, but a isolated group. Multiple scenarios can be isolated.
     *
     * Ideally, this method should be called after adding a scenario to the group.
     *
     * @return The current instance of [SimulationGroup] for chaining calls.
     */
    fun isolate(): SimulationGroup = apply {
        content.values.last().isolated = true
    }

    fun ignore(): SimulationGroup = apply {
        TODO()
    }

    override fun toString(): String {
        return content.toString()
    }

    /**
     * A class representing a simulation expression (`SimEx`) for dynamic scenario creation and management.
     *
     * This class allows for the construction of scenarios using a fluent interface, leveraging infix and operator functions.
     * It is designed to accumulate properties and their corresponding values in scenarios and manage them efficiently.
     *
     * @property properties An array of property names that are used to identify different parts of a scenario.
     * @property scenarioMap A mutable map to store the created scenarios, keyed by a unique label.
     */
    class SimEx(
        private val properties: Array<out String>,
        private val scenarioMap: MutableMap<String, Scenario>
    ) {
        /**
         * Position index used internally to track the current property being set in the scenario.
         */
        private var position = 0

        /**
         * A mutable map that temporarily holds the raw data of a scenario being constructed.
         */
        val rawScenario: MutableMap<String, Any?> = mutableMapOf()

        /**
         * Overloaded plus operator to add to scenario creation.
         *
         * @param that The object to be combined with.
         * @return Returns the combined object.
         */
        operator fun Any.plus(that: Any): Any = this and that

        /**
         * Infix function to associate the current property with a value.
         *
         * This function maps the provided value to the current property and increments the position for the next property.
         * If the scenarioMap is empty and it's the first position, it initializes the mapping process.
         *
         * @param other The value to be associated with the current property.
         * @return Returns the provided value.
         */
        infix fun Any.and(other: Any): Any {
            if (scenarioMap.isEmpty() && position == 0) {
                rawScenario[properties[position]] = this
                position++
            }

            rawScenario[properties[position]] = other

            return other
        }

        /**
         * Overloaded divide operator to finalize scenario creation.
         *
         * @param that The object to be combined with.
         * @return Returns the combined object.
         */
        operator fun Any.div(other: Any): Any = this next other

        /**
         * Overloaded division operator to finalize and store the current scenario.
         *
         * This operator finalizes the current scenario, adds it to the scenarioMap, resets the position,
         * and clears the rawScenario for the next scenario creation.
         *
         * @param other The object to be returned after finalizing the scenario.
         * @return Returns the provided object.
         */
        infix fun Any.next(other: Any): Any {
            val scenarioLabel = rawScenario.values.firstOrNull() ?: (scenarioMap.size + 1).toString()

            scenarioMap[scenarioLabel.toString()] = Scenario(rawScenario)

            position = 0
            rawScenario.clear()
            return other
        }
    }


    companion object {
        /**
         * Factory method to create a [SimulationGroup] with the given properties.
         *
         * @param properties Vararg of strings representing the names of properties for scenarios in the group.
         * @return A new instance of [SimulationGroup].
         */
        fun vars(vararg properties: String): SimulationGroup {
            return SimulationGroup(properties)
        }
    }

}