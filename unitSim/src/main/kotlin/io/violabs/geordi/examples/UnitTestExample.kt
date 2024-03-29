package io.violabs.geordi.examples

import io.violabs.geordi.SimulationGroup
import io.violabs.geordi.UnitSim
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestTemplate
import java.time.LocalDate


/**
 * Defines file-based scenarios for testing.
 * Each scenario includes a name, the path to a scenario file, and the expected output file.
 */
private val FILE_BASED_SCENARIOS = SimulationGroup
    .vars("scenario", "scenarioFile",         "expectedFile")
    .with("full",     "full_scenario.txt",    "full_expected.json")
    .with("partial",  "partial_scenario.txt", "partial_expected.json")

/**
 * Defines parameter-based scenarios for testing.
 * Each scenario includes a name, a person's name, their date of joining, and their years of service.
 */
@Suppress("MagicNumber")
private val PARAMETER_BASED_SCENARIOS = SimulationGroup
    .vars("scenario", "name",            "date joined",                               "years of service")
    .with("geordi",   "Geordi LaForge",  LocalDate.now(),                             4)
    .with("beverly",  "Beverly Crusher", LocalDate.now().minusYears(1), 5)


/**
 * A test class that extends from UnitSim to utilize its testing capabilities.
 * This class uses custom scenarios for testing.
 */
class UnitTestExample : UnitSim(testResourceFolder = "unitTestExample") {

    /**
     * Companion object for setting up the test scenarios.
     * Links the scenarios to their respective test template methods.
     */
    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() = setup<UnitTestExample>(
            FILE_BASED_SCENARIOS to { ::`show file based test` },
            PARAMETER_BASED_SCENARIOS to { ::`show parameter based test` }
        )
    }

    /**
     * Test template for file-based scenarios.
     * @param scenarioFile The file containing the test scenario.
     * @param expectedFile The file containing the expected results.
     */
    @TestTemplate
    @Suppress("FunctionNaming")
    fun `show file based test`(scenarioFile: String, expectedFile: String) = test {
        expectFromFileContent(expectedFile) {
            // transform the content here
            it.item
        }

        wheneverWithFile(scenarioFile) {
            // do something with the file here
            it.item
        }
    }

    /**
     * Test template for parameter-based scenarios.
     * @param name The name of the person.
     * @param dateJoined The date when the person joined.
     * @param yearsOfService The number of years the person has served.
     */
    @TestTemplate
    @Suppress("FunctionNaming")
    fun `show parameter based test`(name: String, dateJoined: LocalDate, yearsOfService: Int) = test {
        expect { true }

        whenever {
            CrewMember(name, dateJoined, yearsOfService).isSenior
        }
    }

    @TestTemplate
    @Suppress("FunctionNaming")
    fun `show sim ex test`(first: String, second: String, third: String) = test {
        expect { third }

        whenever {
            first + second
        }
    }

    /**
     * A nested class representing a crew member.
     * @param name The name of the crew member.
     * @param dateJoined The date when the crew member joined.
     * @param yearsOfService The number of years the crew member has served.
     */
    class CrewMember(private val name: String, private val dateJoined: LocalDate, private val yearsOfService: Int) {
        /**
         * Property to check if the crew member is considered senior.
         * This example considers a crew member senior if they have 1 year of service.
         */
        @Suppress("MagicNumber")
        val isSenior: Boolean
            get() = yearsOfService > 3 // low bar
    }
}
