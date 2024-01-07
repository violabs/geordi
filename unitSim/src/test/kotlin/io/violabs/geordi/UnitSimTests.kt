package io.violabs.geordi

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnitSimTests {
    val debugLogging = mockk<DebugLogging>()

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
}