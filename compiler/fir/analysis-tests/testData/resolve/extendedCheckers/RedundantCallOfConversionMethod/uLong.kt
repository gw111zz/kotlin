// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// WITH_STDLIB
fun test(i: ULong) {
    val <!UNUSED_VARIABLE!>foo<!> = i.<!REDUNDANT_CALL_OF_CONVERSION_METHOD!>toULong()<!>
}
