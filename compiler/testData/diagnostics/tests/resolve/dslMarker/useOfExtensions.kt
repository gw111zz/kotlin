// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// FIR_IDENTICAL
// !DIAGNOSTICS: -UNUSED_PARAMETER
@DslMarker
annotation class Ann

@Ann
class A

fun A.a() = 1

@Ann
class B

fun B.b() = 2

fun foo(x: A.() -> Unit) {}
fun bar(x: B.() -> Unit) {}

fun test() {
    foo {
        a()
        bar {
            <!DSL_SCOPE_VIOLATION!>a<!>()
            this@foo.a()
            b()
        }
    }
}
