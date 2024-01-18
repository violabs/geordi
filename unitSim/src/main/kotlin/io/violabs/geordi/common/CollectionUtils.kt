package io.violabs.geordi.common

infix fun <A, B> A.with(b: B) = Pair(this, b)