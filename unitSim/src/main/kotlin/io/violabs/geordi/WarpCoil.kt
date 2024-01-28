package io.violabs.geordi

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * This class is designed for resolving method or constructor parameters based on their type and position.
 *
 * @param T The type of the input that this `WarpCoil` will handle.
 * @property index The position index of the parameter to be resolved, defaulting to -1.
 * @property input The actual value of the parameter to be injected.
 */
open class WarpCoil<T>(open val index: Int = -1, open val input: T) : ParameterResolver {

    /**
     * Determines if the parameter in the given context is supported by this resolver.
     *
     * It checks if the type of the input matches the type of the parameter at the context's index
     * and if the index matches the parameter's index.
     *
     * Currently, this only checks the indices since reflection acts differently between Java and
     * Kotlin code in regard to JUnit
     *
     * @param parameterContext Context for the parameter, providing access to its metadata.
     * @param extensionContext Context for the extension, providing additional information.
     * @return Boolean indicating whether the parameter is supported by this resolver.
     */
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        if (parameterContext == null) return false

        val inputType = input!!::class
        val parameterTypeJavaClass = parameterContext.parameter.type
        val parameterTypeKotlinClass = parameterContext.parameter.type.kotlin
        val typesMatch = inputType == parameterTypeKotlinClass
        val indicesMatch = parameterContext.index == index
        val inputName = inputType.simpleName
        val parameterName = parameterTypeJavaClass.simpleName
        val nameMatches = inputName == parameterName

        if (!typesMatch) {
            println("WARNING! Types do not match - inputType: $inputType, parameterType: $parameterTypeJavaClass")
        }

        if (!nameMatches) {
            println("WARNING! Names do not match - inputName: $inputName, parameterName: $parameterName")
        }

        return indicesMatch
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): T {
        return input
    }
}
