package io.violabs.geordi

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

        assert(mock::class.simpleName?.contains("Subclass") == true) {
            """
                EXPECT: DebugLogging${'$'}Subclass0
                ACTUAL: ${mock::class.simpleName}
            """.trimIndent()
        }
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
    inner class TestSliceTests {
        // normal flow setup && given
        @Nested
        inner class SetupTests : UnitSim() {
            @Test
            fun `setup will process field correctly`() = test {
                setup {
                    this["test"] = true
                }

                true.expect()

                whenever {
                    it["test"]
                }
            }

            @Test
            fun `given will process field correctly`() = test {
                given {
                    this["test"] = true
                }

                true.expect()

                whenever {
                    DelegateTest(it).test
                }
            }

            inner class DelegateTest(dynamicProperties: TestSlice<*>.DynamicProperties<*>) {
                val test by dynamicProperties
            }
        }

        // normal flow expect && from file && null && method based
        @Nested
        inner class ExpectedTests : UnitSim() {
            @Test
            fun `expect will process with provider`() = test {
                given { this["test"] = true }

                expect { it["test"] }

                whenever { true }
            }

            @Test
            fun `expectFromFileContent will process with a file provided full wheneverWithFile`() = test {
                expectFromFileContent("simpleExample/simple_example_scenario.txt") {
                    it.item.uppercase()
                }

                wheneverWithFile("simpleExample/simple_example_expected.md") {
                    it.item.readLines().last()
                }
            }

            @Test
            fun `expectNull `() = test {
                expectNull()

                whenever { null }
            }
        }

        @Nested
        inner class ExpectedProvidedFolderTests : UnitSim("simpleExample") {
            @Test
            fun `expectFromFileContent will process with a file provided full wheneverWithFile`() = test {
                expectFromFileContent("simple_example_scenario.txt") {
                    val (content) = it

                    content.uppercase()
                }

                wheneverWithFile("simple_example_expected.md") {
                    val (file) = it

                    file.readLines().last()
                }
            }

            @Test
            fun `missing throws an expected throws an exception`() {
                assertThrows<Exception> {
                    test {
                        expectFromFileContent("missing.txt") { it.toString() }
                    }
                }
            }
        }

        // with mocks & throws
        @Nested
        inner class MockTests : UnitSim() {
            private val service = mockk<GeordiMockCompositionService>()

            private val target = GeordiMockTest(service)

            @Test
            fun `mock will process with provider`() = test {
                expect { true }

                setupMocks {
                    every { service.supply() } returns "test"
                    every { service.consume("test") }
                    every { service.accept("test") } returns true
                }

                whenever { target.test() }
            }

            @Test
            fun `mock throws`() = test {
                setupMocks {
                    every { service.supply() } throws Exception("test")
                }

                wheneverThrows<Exception> {
                    target.test()
                }
            }
        }

        // whenever with file
        @Nested
        inner class WheneverTests : UnitSim() {
            @Test
            fun `wheneverWithFile throws an exception`() = test {
                wheneverThrows<Exception>({ wheneverWithFile("missing.txt") { } }) {
                    assert(it.item.message == "File not available missing.txt")
                }
            }
        }

        // then default && then && then equals
        @Nested
        inner class ThenTests : UnitSim() {
            @Test
            fun `then processes assertion`() = test {
                expect { "TEST ME" }

                whenever { "test" }

                then { expect: String?, actual: String? ->
                    assert(expect != null)
                    assert(actual != null)
                    assert(expect!!.contains(actual!!.uppercase()))
                }
            }

            @Test
            fun `thenEquals assertion`() = test {
                expect { "TEST" }

                whenever { "test".uppercase() }

                thenEquals("New message") {
                    assert(true)
                }
            }

            @Test
            fun `defaultThenEquals assertion`() = test {
                expect { "TEST" }

                whenever { "test".uppercase() }

                defaultThenEquals()
            }
        }
    }

    @Nested
    inner class StaticSetupTests {

        @Test
        fun `setup with provider will add scenario to the store`() {
            val simulationGroup = SimulationGroup.vars("test")
            val geordiTestClass = GeordiTestClass()

            UnitSim.setup<GeordiTestClass> {
                arrayOf(simulationGroup with ::`here is the method`)
            }

            val actual = WarpDriveEngine.SCENARIO_STORE[geordiTestClass::`here is the method`.name]

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
            val geordiTestClass = GeordiTestClass()

            UnitSim.setup<GeordiTestClass>(
                simulationGroup with { ::`here is a different method` }
            )

            val actual = WarpDriveEngine.SCENARIO_STORE[geordiTestClass::`here is a different method`.name]

            assert(actual == simulationGroup) {
                """
                    EXPECT: $simulationGroup
                    ACTUAL: $actual
                """.trimIndent()
            }
        }
    }
}

const val TEST_1 = "test"
const val TEST_2 = "test 2"

class GeordiTestClass {
    fun `here is the method`(): String = TEST_1
    fun `here is a different method`(): String = TEST_2
}

class GeordiMockTest(private val compositionService: GeordiMockCompositionService) {
    fun test(): Boolean {
        val string = compositionService.supply()
        compositionService.consume(string)
        return compositionService.accept(string)
    }

}

interface GeordiMockCompositionService {
    fun supply(): String

    fun accept(inString: String): Boolean

    fun consume(inString: String)
}
