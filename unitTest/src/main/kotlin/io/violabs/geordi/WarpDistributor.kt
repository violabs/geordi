package io.violabs.geordi

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import kotlin.reflect.KClass

class WarpDistributor(
    private val entry: Map.Entry<String, Scenario>,
    private var methodName: String
) : TestTemplateInvocationContext {
    private var scenarioMessage: String? = methodName

    override fun getDisplayName(invocationIndex: Int): String {
        if (methodName.contains("#scenario")) {
            scenarioMessage = scenarioMessage
                ?.replace("#scenario", entry.key)
                ?: throw Exception("Scenario message is null")
        }

        return scenarioMessage ?: methodName
    }

    override fun getAdditionalExtensions(): MutableList<Extension>? {
        val mainList: MutableList<Extension> = mutableListOf()

        var indexModifier = 1

        val parsedExtensions = entry
            .value
            .content
            .entries
            .mapIndexed { i, entry ->
                val value = entry.value
                val key = entry.key
                val index = i + indexModifier

                if (key == "scenario") {
                    indexModifier = -1
                    return@mapIndexed null
                }

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