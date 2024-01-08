package io.violabs.geordi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestTemplate

private val SIMULATION = SimulationGroup
    .vars("scenario", "x", "y", "sum")
    .with("2 + 3 = 5", 2,   3,   5)
    .with("2 + 3 = 5", 5,   6,   11)
    .with("2 + 3 = 5", 5,   6,   11)

class SimpleScenarioKotlinTests : UnitSim() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() = setup<SimpleScenarioKotlinTests>(
            SIMULATION with { ::`sum scenarios - #scenario` }
        )
    }

    @TestTemplate
    fun `sum scenarios - #scenario`(x: Int, y: Int, sum: Int) = test {
        expect { sum }

        whenever {
            x + y
        }
    }
}