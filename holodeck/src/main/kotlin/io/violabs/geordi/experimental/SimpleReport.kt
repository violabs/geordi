package io.violabs.geordi.experimental

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class KoverReport(
    @field:JacksonXmlProperty(namespace = "name")
    val name: String? = null,
    @field:JacksonXmlProperty(localName = "package")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val packages: List<Package>,
    @field:JacksonXmlProperty(localName = "counter")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val counters: List<Counter>
) {
    fun verticalDisplay(): String = """
        ;KoverReport(
        ;   name=$name, 
        ;   packages=[
        ;   ¦  ${packages.joinToString(",\n   ¦  ") { it.verticalDisplay() }}
        ;   ],
        ;   counters=[
        ;   ¦  ${counters.joinToString(",\n   ¦  ")}
        ;   ]
        ;)
    """.trimMargin(";")

    data class Package(
        @field:JacksonXmlProperty(namespace = "name")
        val name: String? = null,
        @field:JacksonXmlProperty(localName = "class")
        val clazz: BuildClass? = null
    ) {
        fun verticalDisplay(): String = """
            ;Package(
            ;   ¦  ¦  name=$name, 
            ;   ¦  ¦  clazz=${clazz?.verticalDisplay()}
            ;   ¦  )
        """.trimMargin(";")

        data class BuildClass(
            @field:JacksonXmlProperty(namespace = "name")
            val name: String? = null,
            val method: Method,
            @field:JacksonXmlProperty(localName = "sourcefilename")
            val sourceFilename: String? = null,
            @field:JacksonXmlProperty(localName = "counter")
            @field:JacksonXmlElementWrapper(useWrapping = false)
            val counters: List<Counter>
        ) {
            fun verticalDisplay(): String = """
                ;BuildClass(
                ;   ¦  ¦  ¦  name=$name, 
                ;   ¦  ¦  ¦  method=${method.verticalDisplay()}
                ;   ¦  ¦  ¦  sourceFilename=$sourceFilename, 
                ;   ¦  ¦  ¦  counters=[
                ;   ¦  ¦  ¦  ¦  ${counters.joinToString(",\n   ¦  ¦  ¦  ")} 
                ;   ¦  ¦  ¦  ]
                ;   ¦  ¦  )
            """.trimMargin(";")

            data class Method(
                @field:JacksonXmlProperty(namespace = "name")
                val name: String? = null,
                val desc: String,
                @field:JacksonXmlProperty(localName = "counter")
                @field:JacksonXmlElementWrapper(useWrapping = false)
                val counters: List<Counter>
            ) {
                fun verticalDisplay(): String = """
                    ;Method(
                    ;   ¦  ¦  ¦  name=$name, 
                    ;   ¦  ¦  ¦  desc=$desc, 
                    ;   ¦  ¦  ¦  counters=[
                    ;   ¦  ¦  ¦  ¦  ${counters.joinToString(",\n   ¦  ¦  ¦  ¦  ")} 
                    ;   ¦  ¦  ¦  ]
                    ;   ¦  ¦  ),
                """.trimMargin(";")
            }
        }
    }
}

data class Counter(
    val type: String,
    val missed: Int,
    val covered: Int
)