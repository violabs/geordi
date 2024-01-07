package io.violabs.geordi

inline fun <reified R : Any> Any?.coax(): R? = if (this is R) this else null