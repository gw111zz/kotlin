// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// +JDK

typealias Exn = java.lang.Exception

fun test() {
    throw <!NO_COMPANION_OBJECT!>Exn<!>
}