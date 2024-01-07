package io.violabs.geordi

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
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
     * @param parameterContext Context for the parameter, providing access to its metadata.
     * @param extensionContext Context for the extension, providing additional information.
     * @return Boolean indicating whether the parameter is supported by this resolver.
     */
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        if (parameterContext == null) return false

        val inputType = input!!::class
        val typeMatches = inputType == parameterContext.parameter.type.kotlin

        return typeMatches && parameterContext.index == index
    }

    /**
     * Resolves the parameter for the given context.
     *
     * It provides the actual parameter value to be used in the method or constructor where it's needed.
     * If the type of the input does not match the type of the parameter, it throws a `ParameterResolutionException`.
     *
     * @param parameterContext Context for the parameter, providing access to its metadata.
     * @param extensionContext Context for the extension, providing additional information.
     * @return The value of the input parameter to be used.
     * @throws ParameterResolutionException if no parameter is found for the input.
     */
    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): T {

        val inKlass = input!!::class
        val inJava = inKlass.java
        val paramClass = parameterContext?.parameter?.type
        val paramKlass = paramClass?.kotlin
        if (paramKlass != inKlass && paramClass != inJava) {
            throw ParameterResolutionException("No parameter found for input $input")
        }

        return input
    }
}
