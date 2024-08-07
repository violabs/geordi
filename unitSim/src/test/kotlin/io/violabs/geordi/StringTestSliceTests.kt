package io.violabs.geordi

import io.violabs.geordi.exceptions.NotFoundException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StringTestSliceTests {
    @Nested
    @Suppress("SwallowedException")
    inner class ExpectJsonExceptionBlockingTest : UnitSim() {
        @Test
        fun `expectJson will throw an exception if JSON mapper is missing`() {
            var exceptionCalled = false

            try {
                test {
                    expectJson { "true" }

                    whenever { "true" }
                }
            } catch (nfex: NotFoundException.JsonMapper) {
                exceptionCalled = true
            }

            assert(exceptionCalled) { "NotFoundException not thrown!" }
        }
    }

    @Nested
    inner class ExpectJsonBlockingTest : UnitSim(json = Json) {
        @Test
        fun `expectJson will return a string in a compressed format`() = test {
            expectJson {
                """
                    {
                        "firstItem": "firstValue",
                        "nestedObject": {
                            "nestedItem": "nestedValue"
                        },
                        "array": [
                            "item1",
                            "item2"
                        ]
                    }
                """.trimIndent()
            }

            whenever {
                """{"firstItem":"firstValue","nestedObject":{"nestedItem":"nestedValue"},"array":["item1","item2"]}"""
            }
        }
    }

    @Nested
    @Suppress("SwallowedException")
    inner class WheneverJsonExceptionBlockingTest : UnitSim() {
        @Test
        fun `wheneverJson will throw an exception if JSON mapper is missing`() {
            var exceptionCalled = false

            try {
                test {
                    expect { "true" }

                    wheneverJson { "true" }
                }
            } catch (nfex: NotFoundException.JsonMapper) {
                exceptionCalled = true
            }

            assert(exceptionCalled) { "NotFoundException not thrown!" }
        }
    }

    @Nested
    inner class WheneverJsonBlockingTest : UnitSim(json = Json) {
        @Test
        fun `wheneverJson will return a string in a compressed format`() = test {
            expect {
                """{"firstItem":"firstValue","nestedObject":{"nestedItem":"nestedValue"},"array":["item1","item2"]}"""
            }

            wheneverJson {
                """
                    {
                        "firstItem": "firstValue",
                        "nestedObject": {
                            "nestedItem": "nestedValue"
                        },
                        "array": [
                            "item1",
                            "item2"
                        ]
                    }
                """.trimIndent()
            }
        }
    }

    @Nested
    @Suppress("SwallowedException")
    inner class CoExpectJsonExceptionReactiveTest : CoUnitSim() {
        @Test
        fun `coExpectJson will throw an exception if JSON mapper is missing`() {
            var exceptionCalled = false

            try {
                testBlocking {
                    coExpectJson { "true" }

                    whenever { "true" }
                }
            } catch (nfex: NotFoundException.JsonMapper) {
                exceptionCalled = true
            }

            assert(exceptionCalled) { "NotFoundException not thrown!" }
        }
    }

    @Nested
    inner class CoExpectJsonReactiveTest : CoUnitSim(json = Json) {
        @Test
        fun `coExpectJson will return a string in a compressed format`() = testBlocking {
            coExpectJson {
                """
                    {
                        "firstItem": "firstValue",
                        "nestedObject": {
                            "nestedItem": "nestedValue"
                        },
                        "array": [
                            "item1",
                            "item2"
                        ]
                    }
                """.trimIndent()
            }

            whenever {
                """{"firstItem":"firstValue","nestedObject":{"nestedItem":"nestedValue"},"array":["item1","item2"]}"""
            }
        }
    }

    @Nested
    @Suppress("SwallowedException")
    inner class CoWheneverJsonExceptionReactiveTest : CoUnitSim() {
        @Test
        fun `coWheneverJson will throw an exception if JSON mapper is missing`() {
            var exceptionCalled = false

            try {
                testBlocking {
                    expect { "true" }

                    coWheneverJson { "true" }
                }
            } catch (nfex: NotFoundException.JsonMapper) {
                exceptionCalled = true
            }

            assert(exceptionCalled) { "NotFoundException not thrown!" }
        }
    }

    @Nested
    inner class CoWheneverJsonReactiveTest : CoUnitSim(json = Json) {
        @Test
        fun `coWheneverJson will return a string in a compressed format`() = testBlocking {
            expect {
                """{"firstItem":"firstValue","nestedObject":{"nestedItem":"nestedValue"},"array":["item1","item2"]}"""
            }

            coWheneverJson {
                """
                    {
                        "firstItem": "firstValue",
                        "nestedObject": {
                            "nestedItem": "nestedValue"
                        },
                        "array": [
                            "item1",
                            "item2"
                        ]
                    }
                """.trimIndent()
            }
        }
    }
}
