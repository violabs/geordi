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
    @Suppress("ForbiddenComment")
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

    /**
     * Extracts and returns scenarios that are marked as isolated.
     *
     * This function filters the simulation group's content and returns only those scenarios
     * where the 'isolated' flag is set to true.
     *
     * @return A map containing isolated scenarios, keyed by their labels.
     */
    fun extractIsolated(): Map<String, Scenario> = content.filter { it.value.isolated }

    /**
     * Modifies the isolation state of scenarios in the simulation group.
     *
     * This method ensures that all but the last scenario are isolated if none are currently isolated.
     * It then sets the isolation of the last scenario to false. This approach is particularly useful
     * for controlling test execution order or for scenarios where only the last one should be non-isolated.
     *
     * @return The current instance of [SimulationGroup] for method chaining.
     */
    fun ignore(): SimulationGroup = apply {
        // Check if any scenario is already isolated.
        val anyIsolated = content.values.any { it.isolated }

        if (!anyIsolated) {
            // If no scenarios are isolated, isolate all but the last one.
            content
                .values
                .filterIndexed { i, _ -> content.values.indices.last != i }
                .forEach { it.isolated = true }
        }

        // Set the isolation of the last scenario to false.
        content.values.last().isolated = false
    }


    override fun toString(): String {
        return content.toString()
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
