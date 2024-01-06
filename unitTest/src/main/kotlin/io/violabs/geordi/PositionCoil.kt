package io.violabs.geordi

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * This class is designed to resolve parameters based on their position in a method or constructor.
 * It is generic and can work with any type that extends `Any`.
 *
 * @param T The type of the input that this `PositionCoil` will handle.
 * @property index The position index of the parameter to be resolved.
 * @property input The actual value of the parameter to be injected, which can be null.
 */
class PositionCoil<T : Any>(private val index: Int, private val input: T?) : ParameterResolver {

    /**
     * Determines if the parameter at the given context position can be supported by this resolver.
     *
     * @param parameterContext Context for the parameter, providing access to its metadata.
     * @param extensionContext Context for the extension, providing additional information.
     * @return Boolean indicating whether the parameter at the specified index is supported.
     */
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return parameterContext?.index == index
    }

    /**
     * Resolves the parameter for the given context.
     *
     * It provides the actual parameter value to be used in the method or constructor where it's needed.
     * This implementation simply returns the `input` provided at the specific `index`.
     *
     * @param parameterContext Context for the parameter, providing access to its metadata.
     * @param extensionContext Context for the extension, providing additional information.
     * @return The value of the input parameter to be used, or null if not applicable.
     */
    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any? {
        return input
    }
}
