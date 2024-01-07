package io.violabs.geordi

import io.mockk.*
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.assertFailsWith
import io.mockk.every as mockkEvery

@ExtendWith(WarpDriveEngine::class)
abstract class UnitSim(
    protected val testResourceFolder: String = "",
    private val debugLogging: DebugLogging = DebugLogging.default(),
    private val debugEnabled: Boolean = true,
) {
    private val mocks: MutableList<Any> = mutableListOf()
    private val mockCalls = mutableListOf<MockTask<*>>()
    private val debugItems = mutableMapOf<String, Any?>()

    fun <T> T.debug(key: String = debugItems.size.toString()): T = debugLogging.addDebugItem(key, this)

    inline fun <reified T : Any> mock(): T = mockkClass(type = T::class)

    fun <T : Any> every(mockCall: () -> T?): MockTask<T> {
        val task = MockTask(mockCall = mockCall)
        mockCalls.add(task)
        return task
    }

    fun <T> test(horizontalLogs: Boolean = false, runnable: TestSlice<T>.() -> Unit) {
        val spec = TestSlice<T>(horizontalLogs)

        runnable(spec)

        processTestFlow(spec)
        if (debugEnabled) debugLogging.logDebugItems()
        finalizeMocks()
        cleanup()
    }

    private fun processTestFlow(spec: TestSlice<*>) {
        spec.setupCall()
        spec.expectCall()
        spec.mockSetupCall()
        spec.wheneverCall()
        spec.thenCall()
        spec.tearDownCall()
    }

    private fun finalizeMocks() {
        mockCalls.forEach {
            verify { it.mockCall() }
        }

        if (mocks.isEmpty()) return

        confirmVerified(*mocks.toTypedArray())
    }

    private fun cleanup() {
        mockCalls.clear()
        debugItems.clear()
    }

    inner class TestSlice<T>(
        private val useHorizontalLogs: Boolean = false
    ) {
        private var expected: T? = null
        private var actual: T? = null
        internal var setupCall: () -> Unit = {}
        internal var expectCall: () -> Unit = {}
        internal var mockSetupCall: () -> Unit = this::processMocks
        var wheneverCall: () -> Unit = {}
        internal var thenCall: () -> Unit = this::defaultThenEquals
        internal var tearDownCall: () -> Unit = {}

        private val objectProvider: DynamicProperties = DynamicProperties()

        fun setup(setupFn: MutableMap<String, Any?>.() -> Unit) {
            this.setupCall = { setupFn(objectProvider.assign()) }
        }

        fun given(setupFn: MutableMap<String, Any?>.() -> Unit) {
            this.setupCall = { setupFn(objectProvider.assign()) }
        }

        fun expect(givenFn: (DynamicProperties) -> T?) {
            expectCall = { expected = givenFn(objectProvider) }
        }

        fun expectFromFileContent(filename: String, givenFn: (fileContent: String) -> T?) {
            val fullFilename = filename.takeIf { testResourceFolder.isEmpty() } ?: "$testResourceFolder/$filename"

            val uri =
                this::class
                    .java
                    .classLoader
                    .getResource(fullFilename)
                    ?.toURI() ?: throw Exception("File not available $filename")

            val content = File(uri).readText()

            this.expect { givenFn(content) }
        }

        fun expectNull() = expect { null }

        fun T.expect() = expect { this }

        fun setupMocks(mockSetupFn: () -> Unit) {
            mockSetupCall = {
                mockSetupFn()
                processMocks()
            }
        }

        fun whenever(whenFn: (DynamicProperties) -> T?) {
            wheneverCall = { actual = whenFn(objectProvider) }
        }

        fun wheneverWithFile(filename: String, whenFn: (file: File) -> T?) {
            val fullFilename = filename.takeIf { testResourceFolder.isEmpty() } ?: "$testResourceFolder/$filename"

            val uri = this::class
                .java
                .classLoader
                .getResource(fullFilename)
                ?.toURI() ?: throw Exception("File not available $filename")

            val content = File(uri)

            this.whenever { whenFn(content) }
        }

        inline fun <reified U : Throwable> wheneverThrows(crossinline whenFn: () -> T) {
            wheneverCall = { assertFailsWith<U> { whenFn() } }
        }

        inline fun <reified U : Throwable> wheneverThrows(
            crossinline whenFn: () -> T,
            crossinline and: (U) -> Unit
        ) {
            wheneverCall = {
                val item: U = assertFailsWith<U> { whenFn() }
                and(item)
            }
        }

        fun then(thenFn: (T?, T?) -> Unit) {
            thenCall = {
                thenFn(expected, actual)
            }
        }

        fun thenEquals(message: String, runnable: (() -> Unit)? = null) {
            thenCall = {
                runnable?.invoke()

                assert(expected == actual) {
                    println("FAILED $message")
                    if (useHorizontalLogs) {
                        println(makeHorizontalLogs())
                    } else {
                        println("EXPECT: $expected")
                        println("ACTUAL: $actual")
                    }
                }
            }
        }

        fun defaultThenEquals() {
            assert(expected == actual) {
                if (useHorizontalLogs) {
                    println(makeHorizontalLogs())
                } else {
                    println("EXPECT: $expected")
                    println("ACTUAL: $actual")
                }
            }
        }

        private fun List<String>.zipWithNulls(other: List<String>) = this.mapIndexed { index, e ->
            val actual = other.getOrNull(index) ?: ""

            e to actual
        }

        private fun List<Pair<String, String>>.alignContent(max: Int) = this.joinToString("\n") { (e, a) ->
            val numberOfSpaces = max - e.length

            val g = " ".repeat(numberOfSpaces + 4)

            "$e$g$a"
        }

        private fun makeHorizontalLogs(): String {
            val expectedLines = expected.toString().asLines()
            val actualLines = actual.toString().asLines()

            val zippedWithNulls = expectedLines.zipWithNulls(actualLines)

            val max = expectedLines.maxOfOrNull(String::length) ?: 0

            val based = zippedWithNulls.alignContent(max)

            val expect = "EXPECT"
            val actual = "ACTUAL"

            val numberOfSpaces = max - expect.length + 4

            val titleGap = " ".repeat(numberOfSpaces)

            return """
                |$expect$titleGap$actual
                |$based
            """.trimMargin()
        }

        fun teardown(tearDownFn: () -> Unit) {
            this.tearDownCall = tearDownFn
        }

        private fun processMocks() = debugLogging.logDebugMocks {
            val (throwables, runnables) = mockCalls.partition { it.throwable != null }

            throwables.onEach { mockkEvery { it.mockCall.invoke() } throws it.throwable!! }.logThrownCount()

            val (callOnly, returnable) = runnables.partition { it.returnedItem == null }

            runnables.logCalledCount()

            callOnly.onEach { mockkEvery { it.mockCall.invoke() } returns Unit }.logNullCount()

            returnable.onEach { mockkEvery { it.mockCall.invoke() } returns it.returnedItem!! }.logReturnedCount()
        }

        inner class DynamicProperties {
            private val properties = mutableMapOf<String, Any?>()

            operator fun getValue(thisRef: Any?, property: KProperty<*>): Any? = properties[property.name]

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Any?) {
                properties[property.name] = value
            }

            internal fun assign(): MutableMap<String, Any?> = properties
            operator fun get(key: String): Any? = properties[key]
        }
    }


    companion object {


        inline fun <reified T> setup(provider: T.() -> Array<Pair<SimulationGroup, KFunction<*>>>) {
            val refInstance: T = T::class.java.getDeclaredConstructor().newInstance()

            provider(refInstance).forEach { (scenarios, ref) ->
                val methodName = ref.name

                WarpDriveEngine.SCENARIO_STORE[methodName] = scenarios
            }
        }

        inline fun <reified T> setup(vararg methodPairs: Pair<SimulationGroup, T.() -> KFunction<*>>) {
            val refInstance: T = T::class.java.getDeclaredConstructor().newInstance()

            methodPairs.forEach { (scenarios, nameFetcher) ->
                val methodName = nameFetcher(refInstance).name

                WarpDriveEngine.SCENARIO_STORE[methodName] = scenarios
            }
        }
    }
}