package io.violabs.geordi

import java.util.*
import kotlin.math.max

interface DebugLogging {
    val debugItems: MutableMap<String, Any?>
    fun <T> addDebugItem(key: String = UUID.randomUUID().toString(), value: T): T

    fun logDebugItems()

    fun logDebugMocks(callFn: MetricsReader.() -> Unit)

    companion object {
        fun default(): DebugLogging = DefaultDebugLogging()
    }

    interface MetricsReader {
        fun List<Any>.logThrownCount()
        fun List<Any>.logCalledCount()
        fun List<Any>.logReturnedCount()
        fun List<Any>.logNullCount()
    }
}

internal const val DEBUG_ITEMS_TITLE = "DEBUG ITEMS"
internal const val DEBUG_MOCKS_TITLE = "MOCK METRICS"
internal const val DEBUG_DEFAULT_WIDTH = 26

internal class DefaultDebugLogging : DebugLogging {
    override val debugItems = mutableMapOf<String, Any?>()
    override fun <T> addDebugItem(key: String, value: T): T {
        debugItems[key] = value

        return value
    }

    override fun logDebugItems() {
        if (debugItems.isEmpty()) {
            println("No debug items found")
            println()
            return
        }

        val debugLines = debugItems.toString().asLines()

        val max = stringMax(26, *debugLines.toTypedArray())

        val (topBorder, middleBar, bottomBorder) = Border(max)

        val debugTitleLine = DEBUG_ITEMS_TITLE.debugTitleLineFormat(max)
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

    override fun logDebugMocks(callFn: DebugLogging.MetricsReader.() -> Unit) {
        val (topBorder, middleBar, bottomBorder) = Border(DEBUG_DEFAULT_WIDTH)

        val debugTitleSpaces = " ".repeat((DEBUG_DEFAULT_WIDTH - DEBUG_MOCKS_TITLE.length) / 2)
        val debugTitleLine = "║ $debugTitleSpaces$DEBUG_MOCKS_TITLE$debugTitleSpaces ║"

        println(
            """
                |$topBorder
                |$debugTitleLine
                |$middleBar
            """.trimMargin()
        )

        callFn(DefaultMetricReader)

        println(bottomBorder)
        println()
    }

    class Border(
        val size: Int,
        val gap: String,
        val top: String    = "╔$gap╗",
        val middle: String = "╠$gap╣",
        val bottom: String = "╚$gap╝"
    ) {
        operator fun component1() = top
        operator fun component2() = middle
        operator fun component3() = bottom

        constructor(size: Int) : this(size, "═".repeat(size + 2))
        companion object Defaults {
            const val TOP_LEFT = "╔"
            const val TOP_RIGHT = "╗"
            const val MIDDLE_LEFT = "╠"
            const val MIDDLE_RIGHT = "╣"
            const val BOTTOM_LEFT = "╚"
            const val BOTTOM_RIGHT = "╝"
        }
    }

    object DefaultMetricReader : DebugLogging.MetricsReader {
        override fun List<Any>.logThrownCount() = logCount("THROWN")
        override fun List<Any>.logCalledCount() = logCount("CALLED")
        override fun List<Any>.logReturnedCount() = logCount("RETURNED")
        override fun List<Any>.logNullCount() = logCount("NULL")

        private fun List<Any>.logCount(label: String) {
            this.count().also {
                val message = "# $label: $it"
                val spaces = " ".repeat(DEBUG_DEFAULT_WIDTH - 1 - message.length)
                println("║ $message $spaces ║")
            }
        }
    }

    private fun stringMax(number: Int, vararg strings: String): Int {
        return max(strings.maxOfOrNull(String::length) ?: 0, number)
    }

    private fun buildContent(max: Int, debugLines: List<String>): String {
        return debugLines.joinToString("\n") { "║ $it${" ".repeat(max - it.length)} ║" }
    }

    private fun String.debugTitleLineFormat(max: Int): String {
        val debugTitleSpaces = " ".repeat((max - this.length) / 2)
        val offset = if (max % 2 == 0) " " else ""
        return "║ $debugTitleSpaces$this$debugTitleSpaces $offset║"
    }
}