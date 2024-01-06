package io.violabs.geordi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.confirmVerified
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.util.UUID
import kotlin.math.max
import kotlin.reflect.KFunction
import kotlin.test.assertFailsWith
import io.mockk.every as mockkEvery

private val DEFAULT_OBJECT_MAPPER = ObjectMapper()
    .registerKotlinModule()
    .registerModule(JavaTimeModule())

private fun String.asLines(): List<String> = this.split("\n")

@ExtendWith(WarpDriveEngine::class)
abstract class UnitSim(
    protected val testResourceFolder: String = "",
    protected val objectMapper: ObjectMapper = DEFAULT_OBJECT_MAPPER
) {
    private val mocks: MutableList<Any> = mutableListOf()
    private val mockCalls = mutableListOf<MockTask<*>>()
    private val debugItems = mutableMapOf<String, Any?>()

    fun <T> T.debug(key: String = UUID.randomUUID().toString()): T {
        debugItems[key] = this

        return this
    }

    inline fun <reified T : Any> mock(): T = mockkClass(type = T::class)

    class MockTask<T : Any>(
        val mockCall: () -> T?,
        var returnedItem: T? = null,
        var throwable: Throwable? = null
    ) {

        infix fun returns(returnItem: T) {
            returnedItem = returnItem
        }

        infix fun throws(throwable: Throwable) {
            this.throwable = throwable
        }
    }

    fun <T : Any> every(mockCall: () -> T?): MockTask<T> {
        val task = MockTask(mockCall)
        mockCalls.add(task)
        return task
    }

    fun <T> test(horizontalLogs: Boolean = false, runnable: TestSlice<T>.() -> Unit) {
        val spec = TestSlice<T>(horizontalLogs)

        runnable(spec)

        spec.setupCall()
        spec.expectCall()
        spec.mockSetupCall()
        spec.wheneverCall()
        spec.thenCall()
        spec.tearDownCall()

        handleDebug()

        mockCalls.forEach {
            verify { it.mockCall() }
        }

        if (mocks.isEmpty()) return

        confirmVerified(*mocks.toTypedArray())
        cleanup()
    }

    private fun stringMax(number: Int, vararg strings: String): Int {
        return max(strings.maxOfOrNull(String::length) ?: 0, number)
    }

    private fun buildContent(max: Int, debugLines: List<String>): String {
        return debugLines.joinToString("\n") { "║ $it${" ".repeat(max - it.length)} ║" }
    }

    private fun handleDebug() {
        if (debugItems.isEmpty()) {
            println("No debug items found")
            println()
        } else {
            val debugTitle = "DEBUG ITEMS"

            val debugLines = debugItems.toString().asLines()

            val max = stringMax(26, *debugLines.toTypedArray())

            val border = "═".repeat(max + 2)
            val topBorder    = "╔$border╗"
            val middleBar = "╠$border╣"
            val bottomBorder = "╚$border╝"

            val debugTitleLine = debugTitle.debugTitleLineFormat(max)
            val content = buildContent(max, debugLines)

            println(
                """
                |$topBorder
                |$debugTitleLine
                |$middleBar
                |$content
                |$bottomBorder
            """.trimMargin()
            )
            println()
        }
    }

    fun String.debugTitleLineFormat(max: Int): String {
        val debugTitleSpaces = " ".repeat((max - this.length) / 2)
        val offset = if (max % 2 == 0) " " else ""
        return "║ $debugTitleSpaces$this$debugTitleSpaces $offset║"
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
        private var expectedExists = false

        fun setup(setupFn: () -> Unit) {
            this.setupCall = setupFn
        }

        fun expect(givenFn: () -> T?) {
            if (expectedExists) throw Exception("Can only have expect or given and not both!!")
            expectedExists = true
            expectCall = { expected = givenFn() }
        }

        fun expectFromFile(filename: String, givenFn: (fileContent: String) -> T?) {
            val fullFilename = filename.takeIf { testResourceFolder.isEmpty() } ?: "$testResourceFolder/$filename"

            val uri =
                this::class
                    .java
                    .classLoader
                    .getResource(fullFilename)
                    ?.toURI() ?: throw Exception("File not available $filename")

            val content =
                File(uri)
                    .readText()

            this.expect { givenFn(content) }
        }

        fun expectNull() = expect { null }

        fun given(givenFn: () -> T?) {
            if (expectedExists) throw Exception("Can only have expect or given and not both!!")
            expectedExists = true
            expectCall = { expected = givenFn() }
        }

        fun setupMocks(mockSetupFn: () -> Unit) {
            mockSetupCall = {
                mockSetupFn()
                processMocks()
            }
        }

        fun whenever(whenFn: () -> T?) {
            wheneverCall = { actual = whenFn() }
        }

        fun <T> T.debug(key: String = debugItems.size.toString()): T {
            debugItems[key] = this

            return this
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

        private fun processMocks() {

            val (throwables, runnables) = mockCalls.partition { it.throwable != null }

            val debugTitle = "MOCK METRICS"

            val max = 26

            val border = "═".repeat(max + 2)
            val topBorder    = "╔$border╗"
            val middleBar    = "╠$border╣"
            val bottomBorder = "╚$border╝"

            val debugTitleSpaces = " ".repeat((max - debugTitle.length) / 2)
            val debugTitleLine = "║ $debugTitleSpaces$debugTitle$debugTitleSpaces ║"

            println(
                """
                |$topBorder
                |$debugTitleLine
                |$middleBar
            """.trimMargin()
            )

            throwables.onEach { mockkEvery { it.mockCall.invoke() } throws it.throwable!! }.count().also {
                val message = "# THROWN: $it"
                val spaces = " ".repeat(max - message.length - 1)
                println("║ $message $spaces ║")
            }

            val (callOnly, returnable) = runnables.partition { it.returnedItem == null }

            callOnly.onEach { mockkEvery { it.mockCall.invoke() } }.count().also {
                val message = "# NULL: $it"
                val spaces = " ".repeat(max - message.length - 1)
                println("║ $message $spaces ║")
            }

            returnable.onEach { mockkEvery { it.mockCall.invoke() } returns it.returnedItem!! }.count().also {
                val message = "# RETURNABLE: $it"
                val spaces = " ".repeat(max - message.length - 1)
                println("║ $message $spaces ║")
            }
            println(bottomBorder)
            println()
        }
    }

    companion object {
        inline fun <reified T> setup(provider: (T) -> Array<Pair<KFunction<*>, SimulationGroup>>) {
            val refInstance: T = T::class.java.getDeclaredConstructor().newInstance()

            provider(refInstance).forEach { (ref, scenarios) ->
                val methodName = ref.name

                WarpDriveEngine.SCENARIO_STORE[methodName] = scenarios
            }
        }

        inline fun <reified T> setup(vararg methodPairs: Pair<SimulationGroup, (T) -> KFunction<*>>) {
            val refInstance: T = T::class.java.getDeclaredConstructor().newInstance()

            methodPairs.forEach { (scenarios, nameFetcher) ->
                val methodName = nameFetcher(refInstance).name

                WarpDriveEngine.SCENARIO_STORE[methodName] = scenarios
            }
        }
    }
}