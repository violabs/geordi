package io.violabs.geordi

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import kotlin.reflect.KClass

/**
 * A class representing a test template invocation context in JUnit 5, specifically designed to handle scenarios.
 *
 * This class is responsible for providing a display name for test invocations and managing additional extensions
 * based on the provided scenario.
 *
 * @property entry A map entry where the key is a string representing the scenario identifier, and the value is the
 * [Scenario] object.
 * @property methodName The name of the method in which this context is being used, which may include special tokens.
 */
class WarpDistributor(
    private val entry: Map.Entry<String, Scenario>,
    private var methodName: String
) : TestTemplateInvocationContext {

    // A message describing the scenario, initially set to the method name
    // but can be modified based on the scenario.
    private var scenarioMessage: String = methodName

    /**
     * Generates a display name for the test invocation.
     *
     * This method takes the scenario identifier and replaces any "#scenario" token in the `methodName` with it.
     * If `scenarioMessage` is null, it throws an exception.
     *
     * @param invocationIndex The index of the invocation.
     * @return The display name for the test invocation.
     * @throws Exception if `scenarioMessage` is null when it is required.
     */
    override fun getDisplayName(invocationIndex: Int): String {
        if (methodName.contains("#scenario")) {
            scenarioMessage = scenarioMessage.replace("#scenario", entry.key)
        }

        return scenarioMessage
    }

    /**
     * Provides a list of additional extensions needed for the test invocation context.
     *
     * These extensions are derived from the scenario content and are used to inject parameters into the test method.
     * It handles various types such as maps, lists, and classes, and wraps them in `PositionCoil` or `WarpCoil`
     * extensions.
     *
     * @return A mutable list of [Extension] objects required for the test context.
     */
    override fun getAdditionalExtensions(): MutableList<Extension> {
        val hasScenario = HasScenario(entry.key == "scenario")

        return entry
            .value
            .content
            .entries
            .mapIndexed { i, entry -> determineCoilForParameter(i, entry, hasScenario) }
            .filterNotNull()
            .toMutableList()
    }

    /**
     * A utility class for tracking the presence of a 'scenario' key in a map.
     *
     * @property hasScenario A boolean flag indicating the presence of a 'scenario' key.
     */
    private class HasScenario(private var hasScenario: Boolean)  {
        val indexModifier: Int by lazy {if (hasScenario) -1 else 0 }

        /**
         * Sets the flag indicating the presence of a 'scenario' key to true.
         */
        fun yes() {
            this.hasScenario = true
        }
    }

    /**
     * Determines the appropriate `ParameterResolver` for a given parameter
     * based on its type and index.
     *
     * This function checks the type of the value associated with a map entry and
     * returns a suitable [ParameterResolver].
     * It adjusts the index based on the presence of a 'scenario' key in the map,
     * which is tracked using [HasScenario].
     *
     * @param i The original index of the parameter in the map.
     * @param entry A map entry consisting of a key and a value representing a parameter.
     * @param hasScenario An instance of `HasScenario` used to track the presence of a 'scenario' key
     *                    and adjust the index.
     * @return A `ParameterResolver` appropriate for the type of the value, or null if the key is 'scenario'.
     */
    private fun determineCoilForParameter(
        i: Int,
        entry: Map.Entry<String, Any?>,
        hasScenario: HasScenario
    ): ParameterResolver? {
        // Check if the current entry is the 'scenario' key and update hasScenario accordingly.
        if (entry.key == "scenario") {
            hasScenario.yes()
            return null
        }

        // Retrieve the value and calculate the adjusted index.
        val value = entry.value
        val index = i + hasScenario.indexModifier

        // Determine and return the appropriate ParameterResolver based on the type of the value.
        return when (value) {
            null -> PositionCoil(index, null)
            is Map<*, *> -> PositionCoil(index, value)
            is List<*> -> PositionCoil(index, value)
            is KClass<*> -> PositionCoil(index, value)
            else -> WarpCoil(index, value)
        }
    }

}
