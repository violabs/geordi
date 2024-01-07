package io.violabs.geordi

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext

class WarpDriveEngineTests {
    private val warpDriveEngine = WarpDriveEngine()

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if context is null`() {
        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(null)
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if requiredTestMethod is null`() {
        val context = mockk<ExtensionContext>()

        every { context.requiredTestMethod } returns null

        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(context)
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if name is null`() {
        val context = mockk<ExtensionContext>()

        every { context.requiredTestMethod } returns mockk {
            every { name } returns null
        }

        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(context)
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if no scenarios found`() {
        val context = mockk<ExtensionContext>()
        val methodName = "testMethod"

        every { context.requiredTestMethod } returns mockk {
            every { name } returns methodName
        }

        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(context)
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will process default filter`() {
        val context = mockk<ExtensionContext>()
        val methodName = "testMethod"

        every { context.requiredTestMethod } returns mockk {
            every { name } returns methodName
        }

        val simulationGroup = SimulationGroup.vars("scenario", "b", "c")
            .with("first", 2, 3)
            .with("second", 5, 6)
            .with(null, 5, 6)

        WarpDriveEngine.SCENARIO_STORE[methodName] = simulationGroup

        val actual = warpDriveEngine.provideTestTemplateInvocationContexts(context)

        assertEquals(actual.count(), 3)
    }

    @Test
    fun `provideTestTemplateInvocationContexts will process isolation filter`() {
        val context = mockk<ExtensionContext>()
        val methodName = "testMethod"

        every { context.requiredTestMethod } returns mockk {
            every { name } returns methodName
        }

        val simulationGroup = SimulationGroup.vars("scenario", "b", "c")
            .with("first", 2, 3)
            .with("second", 5, 6).isolate()
            .with(null, 5, 6)

        WarpDriveEngine.SCENARIO_STORE[methodName] = simulationGroup

        val actual = warpDriveEngine.provideTestTemplateInvocationContexts(context)

        assertEquals(actual.count(), 1)
    }

    @Test
    fun `isolation filter static test`() {
        val scenario1 = Scenario(mapOf("a" to 1))
        val scenario2 = Scenario(mapOf("a" to 2))

        scenario1.isolated = true

        val isScenario1Isolated = WarpDriveEngine.ISOLATION_FILTER(mapOf("first" to scenario1).entries.first())
        val isScenario2Isolated = WarpDriveEngine.ISOLATION_FILTER(mapOf("second" to scenario2).entries.first())

        assert(isScenario1Isolated && !isScenario2Isolated) {
            """
                EXPECT: true && false
                ACTUAL: $isScenario1Isolated && $isScenario2Isolated
            """.trimIndent()
        }
    }

    @Test
    fun `default filter static test`() {
        val scenario1 = Scenario(mapOf("a" to 1))
        val scenario2 = Scenario(mapOf("a" to 2))

        scenario1.isolated = true

        val isScenario1Isolated = WarpDriveEngine.DEFAULT_FILTER(mapOf("first" to scenario1).entries.first())
        val isScenario2Isolated = WarpDriveEngine.DEFAULT_FILTER(mapOf("second" to scenario2).entries.first())

        assert(isScenario1Isolated && isScenario2Isolated) {
            """
                EXPECT: true && true
                ACTUAL: $isScenario1Isolated && $isScenario2Isolated
            """.trimIndent()
        }
    }
}