// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !DIAGNOSTICS: -UNUSED_PARAMETER

fun bar(y: (Int) -> Int) = 1
fun foo(x: Float) = 10f
fun foo(x: String) = ""

fun main() {
    bar(::<!CALLABLE_REFERENCE_RESOLUTION_AMBIGUITY!>foo<!>) // no report about unresolved callable reference for `foo`
}
