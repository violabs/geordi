package io.violabs.geordi

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ParameterContext

class PositionCoilTests {
    private val positionCoil = PositionCoil(1, "test")

    @Test
    fun `supportsParameter does not`() {
        val context: ParameterContext = mockk()

        every { context.index } returns 0

        val isFalse = !positionCoil.supportsParameter(context, null)

        assert(isFalse)
    }

    @Test
    fun `supportsParameter does`() {
        val context: ParameterContext = mockk()

        every { context.index } returns 1

        val isTrue = positionCoil.supportsParameter(context, null)

        assert(isTrue)
    }
}