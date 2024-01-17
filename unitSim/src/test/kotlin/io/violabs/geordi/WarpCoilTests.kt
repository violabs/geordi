package io.violabs.geordi

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.lang.reflect.Parameter
import java.util.*
import kotlin.reflect.KClass

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

        val context = TestParameterContext(IntTestClass::class)

        assertFalse(coil.supportsParameter(context, null))
    }

    @Test
    fun `supportsParameter returns false index is not the same`() {
        val coil = WarpCoil(1, input = true)

        val context = TestParameterContext(BooleanTestClass::class)

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
        fun `resolveParameter throws exception if does not match`() {
            val coil = WarpCoil(1, input = true)

            val context = TestParameterContext(StringTestClass::class)

            assertThrows<ParameterResolutionException> {
                coil.resolveParameter(context, null)
            }
        }

        @Test
        fun `resolveParameter successfully returns`() {
            val coil = WarpCoil(1, input = "test")

            val context = TestParameterContext(MultiStringTestClass::class, 1)

            assert(coil.resolveParameter(context, null) == "test")
        }
    }
}

interface TestClass<T>

class IntTestClass : TestClass<Int> {
    fun testMethod(param: Int) {
        println("testMethod $param")
    }
}

class BooleanTestClass : TestClass<Boolean> {
    fun testMethod(param: Boolean) {
        println("testMethod $param")
    }
}

class StringTestClass : TestClass<String> {
    fun testMethod(param: String) {
        println("testMethod $param")
    }
}

class MultiStringTestClass : TestClass<String> {
    fun testMethod(param1: Boolean, param2: String) {
        println("testMethod $param1 and $param2")
    }
}

class TestParameterContext<T, U : TestClass<T>>(
    val klass: KClass<U>, val testIndex: Int = 0
) : ParameterContext {
    override fun getParameter(): Parameter {
        @Suppress("TooGenericExceptionThrown") return klass.java.declaredMethods.firstOrNull()?.parameters?.getOrNull(
                testIndex
            ) ?: throw Exception("No parameters found for ${klass.simpleName}")
    }

    override fun getIndex(): Int = testIndex

    override fun getTarget(): Optional<Any> {
        TODO("GET TARGET Not yet implemented")
    }

    override fun isAnnotated(p0: Class<out Annotation>?): Boolean {
        TODO("IS ANNOTATED Not yet implemented")
    }

    override fun <A : Annotation?> findAnnotation(p0: Class<A>?): Optional<A> {
        TODO("FIND ANNOTATION Not yet implemented")
    }

    override fun <A : Annotation?> findRepeatableAnnotations(p0: Class<A>?): MutableList<A> {
        TODO("FIND REPEATABLE ANNOTATIONS Not yet implemented")
    }
}
