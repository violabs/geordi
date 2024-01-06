package io.violabs.geordi

import org.junit.jupiter.api.Test

//import org.junit.jupiter.api.Test

class SimpleTests : UnitSim() {

//    @Test
//    fun `test`() = test {
//        setup {
//            this["test"] = true
//        }
//
//        expect { true }
//
//        whenever {
//            val test by it
//
//            test
//        }
//    }

    @Test
    fun `test`() = test {
        setup {
            this["test"] = true
        }

        expect { true }

        whenever {
            val s = Se(it)

            s.test.debug()
        }
    }

    class Se(map: TestSlice<*>.DynamicProperties) {
        val test by map
    }

    @Test
    fun `testing quick`() {
        assert(true)
    }
}