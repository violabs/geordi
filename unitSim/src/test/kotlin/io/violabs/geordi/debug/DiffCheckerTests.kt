package io.violabs.geordi.debug

import io.violabs.geordi.SimulationGroup
import io.violabs.geordi.UnitSim
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestTemplate

val SCENARIOS = SimulationGroup
    .vars("scenario", "first", "second", "firstDiff", "secondDiff")
    .with("correct comparison", "this is a test", "this is a test", "______________", "______________")
    .with("incorrect comparison", "this as I test", "thou is a test!", "__is_a__I_____ ", "__ou_i__a_____!")

class DiffCheckerTests : UnitSim() {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() = setup<DiffCheckerTests> { arrayOf(
            SCENARIOS to ::`findDifferences should return a DifferenceGroup containing - #scenario`
        ) }
    }

    @TestTemplate
    fun `findDifferences should return a DifferenceGroup containing - #scenario`(
        first: String,
        second: String,
        firstDiff: String,
        secondDiff: String
    ) = test(horizontalLogs = true) {
        expect {
            DifferenceGroup(first, second, firstDiff, secondDiff)
        }

        whenever {
            DiffChecker.findDifferences(first, second)
        }
    }
}