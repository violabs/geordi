package io.violabs.geordi

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver

open class WarpCoil<T>(protected val index: Int = -1, protected val input: T) : ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        if (parameterContext == null) return false

        val inputType = input!!::class

        val typeMatches = inputType == parameterContext.parameter.type.kotlin

        return typeMatches && parameterContext.index == index
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): T {
        val parameter = parameterContext?.parameter
        val type = parameter?.type
        val kType = type?.kotlin
        val inKType = input!!::class
        val typesAreSame = kType == inKType

        if (typesAreSame) {
            return input
        }
        throw ParameterResolutionException("No parameter found for input $input")
    }
}