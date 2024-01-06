package io.violabs.geordi

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