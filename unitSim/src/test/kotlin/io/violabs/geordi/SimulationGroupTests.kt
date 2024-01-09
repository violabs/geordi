package io.violabs.geordi

import org.junit.jupiter.api.Test

class SimulationGroupTests {

    @Test
    fun `vars and with will build correctly for method version`() {
        val simulationGroup = SimulationGroup
            .vars("scenario", "b", "c"  )
            .with("first"    ,  2 ,  3   )
            .with("second"  ,  5 ,  6   )
            .with(null      ,  5 ,  6   )

        val expected = mapOf(
            "first" to Scenario(mapOf(
                "scenario" to "first",
                "b" to 2,
                "c" to 3
            )),
            "second" to Scenario(mapOf(
                "scenario" to "second",
                "b" to 5,
                "c" to 6
            )),
            "3" to Scenario(mapOf(
                "scenario" to null,
                "b" to 5,
                "c" to 6
            ))
        )

        assert(simulationGroup.content == expected) {
            """
                
                EXPECT: $expected
                ACTUAL: ${simulationGroup.content}
            """.trimIndent()
        }
    }

    @Test
    fun `isolate will flag specific scenarios`() {
        val simulationGroup = SimulationGroup
            .vars("scenario", "b", "c"  )
            .with("first"    ,  2 ,  3   ).isolate()
            .with("second"  ,  5 ,  6   )
            .with(null      ,  5 ,  6   ).isolate()

        val firstScenario = Scenario(mapOf(
            "scenario" to "first",
            "b" to 2,
            "c" to 3
        ))

        val secondScenario = Scenario(mapOf(
            "scenario" to null,
            "b" to 5,
            "c" to 6
        ))

        val expected = mapOf("first" to firstScenario, "3" to secondScenario)

        val actual = simulationGroup.extractIsolated()

        assert(actual == expected) {
            """
                
                EXPECT: $expected
                ACTUAL: $actual
            """.trimIndent()
        }
    }

    @Test
    fun `ignore will turn on non-ignored`() {
        val simulationGroup = SimulationGroup
            .vars("scenario", "b", "c"  )
            .with("first"    ,  2 ,  3   )
            .with("second"  ,  5 ,  6   )
            .with(null      ,  5 ,  6   ).ignore()
            .with("fourth"  ,  8 ,  9   ).ignore()

        val firstScenario = Scenario(mapOf(
            "scenario" to "first",
            "b" to 2,
            "c" to 3
        ))

        val secondScenario = Scenario(mapOf(
            "scenario" to "second",
            "b" to 5,
            "c" to 6
        ))

        val expected = mapOf("first" to firstScenario, "second" to secondScenario)

        val actual = simulationGroup.extractIsolated()

        assert(actual == expected) {
            """
                
                EXPECT: $expected
                ACTUAL: $actual
            """.trimIndent()
        }
    }

    @Test
    fun `ignore and isolate in the same will turn on isolated`() {
        val simulationGroup = SimulationGroup
            .vars("scenario", "b", "c"  )
            .with("first"    ,  2 ,  3   ).isolate()
            .with("second"  ,  5 ,  6   )
            .with(null      ,  5 ,  6   ).ignore()

        val firstScenario = Scenario(mapOf(
            "scenario" to "first",
            "b" to 2,
            "c" to 3
        ))

        val expected = mapOf("first" to firstScenario)

        val actual = simulationGroup.extractIsolated()

        assert(actual == expected) {
            """
                
                EXPECT: $expected
                ACTUAL: $actual
            """.trimIndent()
        }
    }

    @Test
    fun `toString returns correct string`() {
        val simulationGroup = SimulationGroup
            .vars("scenario", "b", "c"  )
            .with("first"    ,  2 ,  3   ).isolate()

        val string = simulationGroup.toString()

        val expected = "{first=Scenario(content={scenario=first, b=2, c=3})}"

        assert(string == expected) {
            """
                |
                |EXPECT: $expected
                |ACTUAL: $string
            """.trimMargin()
        }
    }
}
