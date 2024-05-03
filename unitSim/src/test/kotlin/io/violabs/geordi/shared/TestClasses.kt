package io.violabs.geordi.shared


const val TEST_1 = "test"
const val TEST_2 = "test 2"

class GeordiTestClass {
    fun `here is the method`(): String = TEST_1
    fun `here is a different method`(): String = TEST_2
}

class GeordiMockTest(val compositionService: GeordiMockCompositionService) {
    fun test(): Boolean {
        val string = compositionService.supply()
        compositionService.consume(string)
        return compositionService.accept(string)
    }

}

interface GeordiMockCompositionService {
    fun supply(): String

    fun accept(inString: String): Boolean

    fun consume(inString: String)
}
