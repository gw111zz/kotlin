// WITH_STDLIB

// FILE: test.kt
val bar2 by <!CANNOT_INFER_PARAMETER_TYPE, CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>bar2<!><!NO_VALUE_FOR_PARAMETER!>()<!>

// FILE: lt/neworld/compiler/Foo.kt
package lt.neworld.compiler

class Foo {
    val bar by <!CANNOT_INFER_PARAMETER_TYPE, CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>bar<!><!NO_VALUE_FOR_PARAMETER!>()<!>
}

// FILE: lt/neworld/compiler/bar/Bar.kt
package lt.neworld.compiler.bar

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T, V> bar() = Bar<T, V>()

class Bar<T, V> : ReadOnlyProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        TODO("Not yet implemented")
    }
}
