package io.violabs.geordi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestTemplate
import kotlin.reflect.KClass

private val TYPE_SCENARIOS: List<Pair<String, Any?>> = listOf(
    "string" to "string",
    "int" to Int.MAX_VALUE,
    "boolean" to true,
    "list" to listOf(1, 2, 3),
    "map" to mapOf("key" to "value"),
    "long" to Long.MAX_VALUE,
    "double" to Double.MAX_VALUE,
    "float" to Float.MAX_VALUE,
    "short" to Short.MAX_VALUE,
    "byte" to Byte.MAX_VALUE,
    "char" to 'c',
    "byteArray" to byteArrayOf(1, 2, 3),
    "charArray" to charArrayOf('a', 'b', 'c'),
    "intArray" to intArrayOf(1, 2, 3),
    "longArray" to longArrayOf(1, 2, 3),
    "shortArray" to shortArrayOf(1, 2, 3),
    "floatArray" to floatArrayOf(1.1f, 2.2f, 3.3f),
    "doubleArray" to doubleArrayOf(1.1, 2.2, 3.3),
    "booleanArray" to booleanArrayOf(true, false, true),
    "stringArray" to arrayOf("a", "b", "c"),
    "genericArray" to arrayOf(1, "a", true),
    "throwable" to Throwable("message"),
    "null" to null,
    "class" to String::class.java,
    "klass" to String::class,
    "enum" to TestEnum.VALUE,
    "any" to Any(),
    "customClass" to CustomClass("value")
)

data class CustomClass(val value: String)

enum class TestEnum {
    VALUE
}

private val SCENARIO_MAP = TYPE_SCENARIOS.associateBy(Pair<String, Any?>::first) {
    SimulationGroup.vars("scenario", "value").with(it.first, it.second)
}


/**
 * Special test for the occasion of testing how junit works with the
 * custom class [WarpDistributor].
 */
class WarpDistributorIntegrationSim : UnitSim() {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            setup<WarpDistributorIntegrationSim>(
                SCENARIO_MAP["string"]!! to { it::`getAdditionalExtensions will provide string variable` },
                SCENARIO_MAP["boolean"]!! to { it::`getAdditionalExtensions will provide boolean variable` },
                SCENARIO_MAP["byte"]!! to { it::`getAdditionalExtensions will provide byte variable` },
                SCENARIO_MAP["short"]!! to { it::`getAdditionalExtensions will provide short variable` },
                SCENARIO_MAP["int"]!! to { it::`getAdditionalExtensions will provide int variable` },
                SCENARIO_MAP["long"]!! to { it::`getAdditionalExtensions will provide long variable` },
                SCENARIO_MAP["float"]!! to { it::`getAdditionalExtensions will provide float variable` },
                SCENARIO_MAP["double"]!! to { it::`getAdditionalExtensions will provide double variable` },
                SCENARIO_MAP["char"]!! to { it::`getAdditionalExtensions will provide char variable` },
                SCENARIO_MAP["byteArray"]!! to { it::`getAdditionalExtensions will provide byteArray variable` },
                SCENARIO_MAP["charArray"]!! to { it::`getAdditionalExtensions will provide charArray variable` },
                SCENARIO_MAP["intArray"]!! to { it::`getAdditionalExtensions will provide intArray variable` },
                SCENARIO_MAP["longArray"]!! to { it::`getAdditionalExtensions will provide longArray variable` },
                SCENARIO_MAP["shortArray"]!! to { it::`getAdditionalExtensions will provide shortArray variable` },
                SCENARIO_MAP["floatArray"]!! to { it::`getAdditionalExtensions will provide floatArray variable` },
                SCENARIO_MAP["doubleArray"]!! to { it::`getAdditionalExtensions will provide doubleArray variable` },
                SCENARIO_MAP["booleanArray"]!! to { it::`getAdditionalExtensions will provide booleanArray variable` },
                SCENARIO_MAP["stringArray"]!! to { it::`getAdditionalExtensions will provide stringArray variable` },
                SCENARIO_MAP["genericArray"]!! to { it::`getAdditionalExtensions will provide genericArray variable` },
                SCENARIO_MAP["throwable"]!! to { it::`getAdditionalExtensions will provide throwable variable` },
                SCENARIO_MAP["null"]!! to { it::`getAdditionalExtensions will provide null variable` },
                SCENARIO_MAP["class"]!! to { it::`getAdditionalExtensions will provide class variable` },
                SCENARIO_MAP["klass"]!! to { it::`getAdditionalExtensions will provide klass variable` },
                SCENARIO_MAP["enum"]!! to { it::`getAdditionalExtensions will provide enum variable` },
                SCENARIO_MAP["any"]!! to { it::`getAdditionalExtensions will provide any variable` },
                SCENARIO_MAP["list"]!! to { it::`getAdditionalExtensions will provide list variable` },
                SCENARIO_MAP["map"]!! to { it::`getAdditionalExtensions will provide map variable` },
                SCENARIO_MAP["customClass"]!! to { it::`getAdditionalExtensions will provide customClass variable` }
            )
        }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide string variable`(value: String) = test {
        expect { "string" }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide boolean variable`(value: Boolean) = test {
        expect { true }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide byte variable`(value: Byte) = test {
        expect { Byte.MAX_VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide short variable`(value: Short) = test {
        expect {
            Short.MAX_VALUE
        }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide int variable`(value: Int) = test {
        expect { Int.MAX_VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide long variable`(value: Long) = test {
        expect { Long.MAX_VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide float variable`(value: Float) = test {
        expect { Float.MAX_VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide double variable`(value: Double) = test {
        expect { Double.MAX_VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide char variable`(value: Char) = test {
        expect { 'c' }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide byteArray variable`(value: ByteArray) = test {
        expect { byteArrayOf(1, 2, 3).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide charArray variable`(value: CharArray) = test {
        expect { charArrayOf('a', 'b', 'c').joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide intArray variable`(value: IntArray) = test {
        expect { intArrayOf(1, 2, 3).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide longArray variable`(value: LongArray) = test {
        expect { longArrayOf(1, 2, 3).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide shortArray variable`(value: ShortArray) = test {
        expect { shortArrayOf(1, 2, 3).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide floatArray variable`(value: FloatArray) = test {
        expect { floatArrayOf(1.1f, 2.2f, 3.3f).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide doubleArray variable`(value: DoubleArray) = test {
        expect { doubleArrayOf(1.1, 2.2, 3.3).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide booleanArray variable`(value: BooleanArray) = test {
        expect { booleanArrayOf(true, false, true).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide stringArray variable`(value: Array<String>) = test {
        expect { arrayOf("a", "b", "c").joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide genericArray variable`(value: Array<Any>) = test {
        expect { arrayOf(1, "a", true).joinToString(",") }

        whenever { value.joinToString(",").debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide throwable variable`(value: Throwable) = test {
        expect { "message" }

        whenever { value.message.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide null variable`(value: Any?) = test {
        expect { null }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide class variable`(value: Class<*>) = test {
        expect { String::class.java }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide klass variable`(value: KClass<*>) = test {
        expect { String::class }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide enum variable`(value: TestEnum) = test {
        expect { TestEnum.VALUE }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide any variable`(value: Any?) = test {
        expect { true }

        whenever { value != null }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide list variable`(value: List<*>) = test {
        expect { listOf(1, 2, 3) }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide map variable`(value: Map<*, *>) = test {
        expect { mapOf("key" to "value") }

        whenever { value.debug() }
    }

    @TestTemplate
    fun `getAdditionalExtensions will provide customClass variable`(value: CustomClass) = test {
        expect { CustomClass("value") }

        whenever { value.debug() }
    }
}