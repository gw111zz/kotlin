// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !LANGUAGE: -ApproximateIntegerLiteralTypesInReceiverPosition
fun fooInt(p: Int) = p
fun fooLong(p: Long) = p
fun fooByte(p: Byte) = p
fun fooShort(p: Short) = p

fun test() {
    fooInt(1 + 1)
    fooByte(<!ARGUMENT_TYPE_MISMATCH!>1 + 1<!>)
    fooLong(1 + 1)
    fooShort(<!ARGUMENT_TYPE_MISMATCH!>1 + 1<!>)

    fooInt(1 * 1)
    fooByte(<!ARGUMENT_TYPE_MISMATCH!>1 * 1<!>)
    fooLong(1 * 1)
    fooShort(<!ARGUMENT_TYPE_MISMATCH!>1 * 1<!>)

    fooInt(1 / 1)
    fooByte(<!ARGUMENT_TYPE_MISMATCH!>1 / 1<!>)
    fooLong(1 / 1)
    fooShort(<!ARGUMENT_TYPE_MISMATCH!>1 / 1<!>)

    fooInt(1 % 1)
    fooByte(<!ARGUMENT_TYPE_MISMATCH!>1 % 1<!>)
    fooLong(1 % 1)
    fooShort(<!ARGUMENT_TYPE_MISMATCH!>1 % 1<!>)
}
