package io.violabs.geordi.debug

object DiffChecker {
    fun findDifferences(first: String, second: String): DifferenceGroup {
        val paddedFirst = first.pad(second)
        val paddedSecond = second.pad(first)

        val firstCheck = paddedFirst.parseDiff(paddedSecond)
        val secondCheck = paddedSecond.parseDiff(paddedFirst)

        return DifferenceGroup(first, second, firstCheck, secondCheck)
    }

    private fun String.parseDiff(other: String): String =
        this.mapIndexed { i, c -> if (c == other[i]) '_' else c }.joinToString("")

    private fun String.pad(other: String, repeatable: String = " "): String =
        if (this.length < other.length) {
            "$this${repeatable.repeat(other.length - this.length)}"
        } else {
            this
        }
}
