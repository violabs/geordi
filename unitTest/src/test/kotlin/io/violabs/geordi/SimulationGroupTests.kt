package io.violabs.geordi

import org.junit.jupiter.api.Test

class SimulationGroupTests {

    @Test
    fun `vars will build correctly`() {
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
}