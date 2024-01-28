package io.violabs.geordi.debug

data class DifferenceGroup(
    val first: Difference,
    val second: Difference,
) {
    constructor(
        first: String,
        second: String,
        firstDifference: String,
        secondDifference: String
    ) : this(
        Difference(first, firstDifference),
        Difference(second, secondDifference)
    )

    override fun toString(): String = """
        |ORIGINAL FIRST
        |${first.original}
        |COMPARED
        |----------------------------------------------------------------
        |${first.differences}
        |${second.differences}
        |----------------------------------------------------------------
        |ORIGINAL SECOND
        |${second.original}
    """.trimMargin()

    data class Difference(val original: String, val differences: String)
}