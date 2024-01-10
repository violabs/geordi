package io.violabs.geordi.experimental

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test

class XmlParsingTest {

    @Test
    fun `tester`() {
        val s = this.javaClass.classLoader.getResource("report.xml")!!.readText()

        val su = XmlMapper()
            .registerKotlinModule()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(s, KoverReport::class.java)

        println(su.verticalDisplay())
    }
}