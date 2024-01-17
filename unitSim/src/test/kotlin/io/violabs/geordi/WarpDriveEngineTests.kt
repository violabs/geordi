package io.violabs.geordi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExecutableInvoker
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstances
import org.junit.jupiter.api.parallel.ExecutionMode
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*
import java.util.function.Function
import kotlin.reflect.KClass

class WarpDriveEngineTests {
    private val warpDriveEngine = WarpDriveEngine()

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if context is null`() {
        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(null)
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will throw exception if no scenarios found`() {
        assertThrows<Exception> {
            warpDriveEngine.provideTestTemplateInvocationContexts(TestExtensionContext())
        }
    }

    @Test
    fun `provideTestTemplateInvocationContexts will process default filter`() {
        val methodName = "testMethod"
        val context = TestExtensionContext()

        val simulationGroup = SimulationGroup.vars("scenario", "b", "c")
            .with("first", 2, 3)
            .with("second", 5, 6)
            .with(null, 5, 6)

        WarpDriveEngine.SCENARIO_STORE[methodName] = simulationGroup

        val actual = warpDriveEngine.provideTestTemplateInvocationContexts(context)

        assertEquals(actual.count(), 3)
    }

    @Test
    fun `provideTestTemplateInvocationContexts will process isolation filter`() {
        val methodName = "testMethod"
        val context = TestExtensionContext()

        val simulationGroup = SimulationGroup.vars("scenario", "b", "c")
            .with("first", 2, 3)
            .with("second", 5, 6).isolate()
            .with(null, 5, 6)

        WarpDriveEngine.SCENARIO_STORE[methodName] = simulationGroup

        val actual = warpDriveEngine.provideTestTemplateInvocationContexts(context)

        assertEquals(actual.count(), 1)
    }

    @Test
    fun `isolation filter static test`() {
        val scenario1 = Scenario(mapOf("a" to 1))
        val scenario2 = Scenario(mapOf("a" to 2))

        scenario1.isolated = true

        val isScenario1Isolated = WarpDriveEngine.ISOLATION_FILTER(mapOf("first" to scenario1).entries.first())
        val isScenario2Isolated = WarpDriveEngine.ISOLATION_FILTER(mapOf("second" to scenario2).entries.first())

        assert(isScenario1Isolated && !isScenario2Isolated) {
            """
                EXPECT: true && false
                ACTUAL: $isScenario1Isolated && $isScenario2Isolated
            """.trimIndent()
        }
    }

    @Test
    fun `default filter static test`() {
        val scenario1 = Scenario(mapOf("a" to 1))
        val scenario2 = Scenario(mapOf("a" to 2))

        scenario1.isolated = true

        val isScenario1Isolated = WarpDriveEngine.DEFAULT_FILTER(mapOf("first" to scenario1).entries.first())
        val isScenario2Isolated = WarpDriveEngine.DEFAULT_FILTER(mapOf("second" to scenario2).entries.first())

        assert(isScenario1Isolated && isScenario2Isolated) {
            """
                EXPECT: true && true
                ACTUAL: $isScenario1Isolated && $isScenario2Isolated
            """.trimIndent()
        }
    }
}

class TestExtensionContext(private val klass: KClass<*> = Example::class) : ExtensionContext {
    override fun getRequiredTestMethod(): Method {
        @Suppress("TooGenericExceptionThrown")
        return klass.java.methods.firstOrNull() ?: throw Exception("No test method found")
    }

    override fun getParent(): Optional<ExtensionContext> {
        TODO("GET PARENT Not yet implemented")
    }

    override fun getRoot(): ExtensionContext {
        TODO("GET ROOT Not yet implemented")
    }

    override fun getUniqueId(): String {
        TODO("GET UNIQUE ID Not yet implemented")
    }

    override fun getDisplayName(): String {
        TODO("GET DISPLAY NAME Not yet implemented")
    }

    override fun getTags(): MutableSet<String> {
        TODO("GET TAGS Not yet implemented")
    }

    override fun getElement(): Optional<AnnotatedElement> {
        TODO("GET ELEMENT Not yet implemented")
    }

    override fun getTestClass(): Optional<Class<*>> {
        TODO("GET TEST CLASS Not yet implemented")
    }

    override fun getTestInstanceLifecycle(): Optional<TestInstance.Lifecycle> {
        TODO("GET TEST INSTANCE LIFECYCLE Not yet implemented")
    }

    override fun getTestInstance(): Optional<Any> {
        TODO("GET TEST INSTANCE Not yet implemented")
    }

    override fun getTestInstances(): Optional<TestInstances> {
        TODO("GET TEST INSTANCES Not yet implemented")
    }

    override fun getTestMethod(): Optional<Method> {
        return klass.java.methods.firstOrNull()?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun getExecutionException(): Optional<Throwable> {
        TODO("GET EXECUTION EXCEPTION Not yet implemented")
    }

    override fun getConfigurationParameter(p0: String?): Optional<String> {
        TODO("GET CONFIGURATION PARAMETER Not yet implemented")
    }

    override fun <T : Any?> getConfigurationParameter(p0: String?, p1: Function<String, T>?): Optional<T> {
        TODO("GET CONFIGURATION PARAMETER Not yet implemented")
    }

    override fun publishReportEntry(p0: MutableMap<String, String>?) {
        TODO("PUBLISH REPORT ENTRY Not yet implemented")
    }

    override fun getStore(p0: ExtensionContext.Namespace?): ExtensionContext.Store {
        TODO("GET STORE Not yet implemented")
    }

    override fun getExecutionMode(): ExecutionMode {
        TODO("GET EXECUTION MODENot yet implemented")
    }

    override fun getExecutableInvoker(): ExecutableInvoker {
        TODO("GET EXECUTABLE INVOKER Not yet implemented")
    }

    class Example {
        @Suppress("EmptyFunctionBlock")
        fun testMethod() {
        }
    }
}
