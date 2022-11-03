// FIR_DISABLE_LAZY_RESOLVE_CHECKS
/*
 * KOTLIN DIAGNOSTICS NOT LINKED SPEC TEST (NEGATIVE)
 *
 * SECTIONS: objects, inheritance
 * NUMBER: 2
 * DESCRIPTION: Access to class members in the super constructor call of an object.
 * ISSUES: KT-25289
 */

// TESTCASE NUMBER: 2
open class Foo(val prop: Int) {
    companion object MyCompanion : Foo(Foo.prop)
}

fun box(): String? {
    if (<!SENSELESS_COMPARISON!>Foo(42) == null<!>) return null

    return "OK"
}
