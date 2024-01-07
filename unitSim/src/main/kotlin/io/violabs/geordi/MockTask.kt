package io.violabs.geordi

@ExcludeFromJacocoGeneratedReport
class MockTask<T : Any>(
    var returnedItem: T? = null,
    var throwable: Throwable? = null,
    val mockCall: () -> T?
) {

    infix fun returns(returnItem: T) {
        returnedItem = returnItem
    }

    infix fun throws(throwable: Throwable) {
        this.throwable = throwable
    }
}