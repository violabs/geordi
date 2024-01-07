package io.violabs.geordi

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnitSimTests {
    val debugLogging = mockk<DebugLogging>()

    @AfterEach
    fun teardown() {
        // confirm no additional mocks are called
        confirmVerified(debugLogging)
    }

    @Test
    fun `mock will create mock of specified type`() {
        val unitSim = object : UnitSim() {
            fun testMock(): DebugLogging = mock()
        }

        val mock = unitSim.testMock()

        assert("DebugLogging\$Subclass0" == mock::class.simpleName)
    }

    @Test
    fun `every will create mock task`() {
        val unitSim = object : UnitSim() {
            fun testEvery(): MockTask<String> = every { "test" }
        }

        val mockTask = unitSim.testEvery()

        assert(mockTask.mockCall.invoke() == "test")
    }

    @Nested
    inner class DebugItemAddTests {
        @Test
        fun `debug will add debug item with default key`() {
            val debugItem = "test"
            val indexKey = "0"

            val unitSim = object : UnitSim(debugLogging = debugLogging) {
                fun testDebug(): String = debugItem.debug()
            }

            every { debugLogging.addDebugItem(indexKey, debugItem) } returns debugItem

            unitSim.testDebug()

            verify { debugLogging.addDebugItem(indexKey, debugItem) }
            confirmVerified(debugLogging)
        }

        @Test
        fun `debug will add debug item with specified key`() {
            val debugItem = "test"
            val providedKey = "testKey"

            val unitSim = object : UnitSim(debugLogging = debugLogging) {
                fun testDebug(): String = debugItem.debug(providedKey)
            }

            every { debugLogging.addDebugItem(providedKey, debugItem) } returns debugItem

            unitSim.testDebug()

            verify { debugLogging.addDebugItem(providedKey, debugItem) }
            confirmVerified(debugLogging)
        }
    }

    @Nested
    inner class StaticSetupTests {

        @Test
        fun `setup with provider will add scenario to the store`() {
            val simulationGroup = SimulationGroup.vars("test")
            val testClass = TestClass()

            UnitSim.setup<TestClass> {
                arrayOf(simulationGroup with ::`here is the method`)
            }

            val actual = WarpDriveEngine.SCENARIO_STORE[testClass::`here is the method`.name]

            assert(actual == simulationGroup) {
                """
                    EXPECT: $simulationGroup
                    ACTUAL: $actual
                """.trimIndent()
            }
        }

        @Test
        fun `setup with method pairs will add scenario to the store`() {
            val simulationGroup = SimulationGroup.vars("test")
            val testClass = TestClass()

            UnitSim.setup<TestClass>(
                simulationGroup with { ::`here is a different method` }
            )

            val actual = WarpDriveEngine.SCENARIO_STORE[testClass::`here is a different method`.name]

            assert(actual == simulationGroup) {
                """
                    EXPECT: $simulationGroup
                    ACTUAL: $actual
                """.trimIndent()
            }
        }
    }
}

class TestClass {
    fun `here is the method`(): String = "test"
    fun `here is a different method`(): String = "test 2"
}