package io.violabs.geordi

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MockTaskTests {

    @Test
    fun `default filled out`() {
        val task = MockTask { true }

        assertNull(task.returnedItem)
        assertNull(task.throwable)
        assert(task.mockCall.invoke()!!)
    }

    @Test
    fun `fully filled out`() {
        val task = MockTask(false, Exception("test")) { true }

        assertFalse(task.returnedItem!!)
        assertNotNull(task.throwable)
        assert(task.mockCall.invoke()!!)
    }
}
