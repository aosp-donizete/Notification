package com.android.notification

import kotlin.reflect.KProperty

class PseudoRandom<T>(
    private var value: T,
    private val transformer: (T) -> T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = transformer(value).also {
        value = it
    }
}
