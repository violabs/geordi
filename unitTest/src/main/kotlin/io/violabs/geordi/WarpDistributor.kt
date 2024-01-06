package io.violabs.geordi

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import kotlin.reflect.KClass

/**
 * A class representing a test template invocation context in JUnit 5, specifically designed to handle scenarios.
 *
 * This class is responsible for providing a display name for test invocations and managing additional extensions
 * based on the provided scenario.
 *
 * @property entry A map entry where the key is a string representing the scenario identifier, and the value is the [Scenario] object.
 * @property methodName The name of the method in which this context is being used, which may include special tokens.
 */
class WarpDistributor(
    private val entry: Map.Entry<String, Scenario>,
    private var methodName: String
) : TestTemplateInvocationContext {

    // A message describing the scenario, initially set to the method name
    // but can be modified based on the scenario.
    private var scenarioMessage: String? = methodName

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
            scenarioMessage = scenarioMessage
                ?.replace("#scenario", entry.key)
                ?: throw Exception("Scenario message is null")
        }

        return scenarioMessage ?: methodName
    }

    /**
     * Provides a list of additional extensions needed for the test invocation context.
     *
     * These extensions are derived from the scenario content and are used to inject parameters into the test method.
     * It handles various types such as maps, lists, and classes, and wraps them in `PositionCoil` or `WarpCoil` extensions.
     *
     * @return A mutable list of [Extension] objects required for the test context.
     */
    override fun getAdditionalExtensions(): MutableList<Extension> {
        val mainList: MutableList<Extension> = mutableListOf()

        var indexModifier = 1

        val parsedExtensions = entry
            .value
            .content
            .entries
            .mapIndexed { i, entry ->
                if (entry.key == "scenario") {
                    indexModifier = -1
                    return@mapIndexed null
                }

                val value = entry.value
                val index = i + indexModifier

                if (value == null) {
                    return@mapIndexed PositionCoil(index, null)
                }

                when(value) {
                    is Map<*, *> -> PositionCoil(index, value)
                    is List<*> -> PositionCoil(index, value)
                    is KClass<*> -> PositionCoil(index, value)
                    else -> WarpCoil(index, value)
                }
            }
            .filterNotNull()

        return (mainList + parsedExtensions).toMutableList()
    }
}
