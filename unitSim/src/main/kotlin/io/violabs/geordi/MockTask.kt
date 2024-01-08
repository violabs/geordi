package io.violabs.geordi

/**
 * A class representing a task for setting up mock behavior in unit tests.
 *
 * This class is used to configure the return value or an exception to be thrown when a mock method is called.
 * It supports fluent configuration through infix functions.
 *
 * @param T The type of the return value for the mock call.
 * @param returnedItem The item to be returned when the mock call is executed. Default is null.
 * @param throwable The throwable to be thrown when the mock call is executed. Default is null.
 * @param mockCall A lambda representing the mock call.
 */
@ExcludeFromJacocoGeneratedReport
class MockTask<T : Any>(
    var returnedItem: T? = null,
    var throwable: Throwable? = null,
    val mockCall: () -> T?
) {

    /**
     * Sets the item to be returned when the mock call is executed.
     *
     * @param returnItem The item to return.
     */
    infix fun returns(returnItem: T) {
        returnedItem = returnItem
    }

    /**
     * Sets the throwable to be thrown when the mock call is executed.
     *
     * @param throwable The throwable to throw.
     */
    infix fun throws(throwable: Throwable) {
        this.throwable = throwable
    }
}
