package io.violabs.geordi

inline fun <reified R : Any> Any?.coax(): R? = if (this is R) this else null
inline fun <reified R : Any> Any?.force(): R {
    if (this !is R) throw Exception("Could not coerce $this into a ${R::class.simpleName}")

    return this
}