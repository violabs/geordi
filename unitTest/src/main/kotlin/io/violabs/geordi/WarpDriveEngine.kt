package io.violabs.geordi

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import java.util.stream.Stream
import kotlin.streams.asStream

class WarpDriveEngine : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext?): Boolean {
        return true
    }

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext?): Stream<TestTemplateInvocationContext> {
        val methodName: String? = context?.requiredTestMethod?.name

        val objScenarios = SCENARIO_STORE[methodName]

        if (objScenarios !is SimulationGroup) throw Exception("No scenarios found for method $methodName")

        val anyIsolated = objScenarios.content.any { it.value.isolated }

        val filterFn = if (anyIsolated) ISOLATION_FILTER else DEFAULT_FILTER

        return objScenarios
            .content
            .asSequence()
            .filter(filterFn)
            .map { WarpDistributor(it, methodName ?: "") }
            .asStream()
    }

    companion object {
        val SCENARIO_STORE: MutableMap<String, SimulationGroup> = mutableMapOf()
        val ISOLATION_FILTER: (Map.Entry<String, Scenario>) -> Boolean = { it.value.isolated }
        val DEFAULT_FILTER: (Map.Entry<String, Scenario>) -> Boolean = { true }
    }
}