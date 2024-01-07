package io.violabs.geordi

import org.junit.jupiter.api.Test

class SimpleKotlinTests : UnitSim() {

    @Test
    fun `bare minimum test`() = test {
        expect { 15 }

        whenever { "Hello Universe!".length }
    }

    @Test
    fun `bare minimum with setup`() = test {
        given { this["greeting"] = "Hello Universe!" }

        expect { "Hello Universe!" }

        whenever { Greeting(it).greeting.coax() }
    }

    @Test
    fun `bare minimum using all parts`() = test {
        setup { this["greeting"] = "Hello Universe!" }

        expect { "Hello Universe!" }

        whenever { Greeting(it).greeting.coax() }
    }
}

class Greeting(properties: UnitSim.TestSlice<String>.DynamicProperties) {
    val greeting by properties
}