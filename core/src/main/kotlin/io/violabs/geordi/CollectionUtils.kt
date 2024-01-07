package io.violabs.geordi

infix fun <A, B> A.with(b: B) = Pair(this, b)