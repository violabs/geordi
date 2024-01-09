package io.violabs.geordi

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.lang.reflect.Parameter

class WarpCoilTests {
    @Test
    fun `instantiation default`() {
        val default = WarpCoil(input = true)

        assert(default.input)
        assert(default.index == -1)
    }

    @Test
    fun `supportsParameter returns false if context is null`() {
        val coil = WarpCoil(1, input = true)

        assertFalse(coil.supportsParameter(null, null))
    }

    @Test
    fun `supportsParameter returns false type is not the same`() {
        val coil = WarpCoil(1, input = true)

        val context = mockk<ParameterContext>()
        val parameter = mockk<Parameter>()

        every { context.parameter } returns parameter
        every { parameter.type } returns Int::class.java

        assertFalse(coil.supportsParameter(context, null))
    }

    @Test
    fun `supportsParameter returns false index is not the same`() {
        val coil = WarpCoil(1, input = true)

        val context = mockk<ParameterContext>()
        val parameter = mockk<Parameter>()

        every { context.parameter } returns parameter
        every { parameter.type } returns Boolean::class.java
        every { context.index } returns 0

        assertFalse(coil.supportsParameter(context, null))
    }

    @Nested
    inner class ResolveParameterTests {
        @Test
        fun `resolveParameter throws exception if context is null`() {
            val coil = WarpCoil(1, input = true)

            assertThrows<ParameterResolutionException> {
                coil.resolveParameter(null, null)
            }
        }

        @Test
        fun `resolveParameter throws exception if parameter is null`() {
            val coil = WarpCoil(1, input = true)

            val context = mockk<ParameterContext>()

            every { context.parameter } returns null

            assertThrows<ParameterResolutionException> {
                coil.resolveParameter(context, null)
            }
        }

        @Test
        fun `resolveParameter throws exception if type is null`() {
            val coil = WarpCoil(1, input = true)

            val context = mockk<ParameterContext>()
            val parameter = mockk<Parameter>()

            every { context.parameter } returns parameter
            every { parameter.type } returns null

            assertThrows<ParameterResolutionException> {
                coil.resolveParameter(context, null)
            }
        }

        @Test
        fun `resolveParameter throws exception if does not match`() {
            val coil = WarpCoil(1, input = true)

            val context = mockk<ParameterContext>()
            val parameter = mockk<Parameter>()

            every { context.parameter } returns parameter
            every { parameter.type } returns String::class.java

            assertThrows<ParameterResolutionException> {
                coil.resolveParameter(context, null)
            }
        }

        @Test
        fun `resolveParameter successfully returns `() {
            val coil = WarpCoil(1, input = "test")

            val context = mockk<ParameterContext>()
            val parameter = mockk<Parameter>()

            every { context.parameter } returns parameter
            every { parameter.type } returns String::class.java

            assert(coil.resolveParameter(context, null) == "test")
        }
    }
}
