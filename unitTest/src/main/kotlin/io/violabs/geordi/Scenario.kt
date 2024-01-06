package io.violabs.geordi

/**
 * This will be used by [SimulationGroup] to generate a test for each scenario using the same test function.
 * [WarpDriveEngine] contains a map of these scenarios, which it uses to generate the tests. Data is processed
 * from this in the [WarpDistributor] class to determine the resolver for each value in the map. Each value
 * can associate to a parameter of a test function.
 *
 * @property content A map containing the data of the scenario. Keys are strings and values can be any type, including null.
 */
data class Scenario(val content: Map<String, Any?>) {
    /**
     * A flag indicating whether the scenario is put in the isolation group.
     * More than 1 scenario can be isolated. Useful for debugging multi-scenario tests.
     */
    var isolated = false
}
