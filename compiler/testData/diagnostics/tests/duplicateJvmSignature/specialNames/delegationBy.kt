// FIR_DISABLE_LAZY_RESOLVE_CHECKS
interface Foo

class <!CONFLICTING_JVM_DECLARATIONS!>Bar(f: Foo)<!> : Foo by f {
    <!CONFLICTING_JVM_DECLARATIONS!>val `$$delegate_0`: Foo?<!> = null
}