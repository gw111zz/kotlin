// FIR_DISABLE_LAZY_RESOLVE_CHECKS
<!CONFLICTING_OVERLOADS!>fun bar(x: String): Int<!> = 1
<!CONFLICTING_OVERLOADS!>fun bar(x: String): Double<!> = <!RETURN_TYPE_MISMATCH!>1<!>

fun baz(x: String): Int = 1
fun <T, R> foobaz(x: T): R = TODO()

fun foo() {
    val x: (String) -> Int = ::bar
    val y = ::bar
    val z = ::baz
    val w: (String) -> Int = ::foobaz

    ::baz
}
