// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// JAVAC_EXPECTED_FILE
// WITH_EXTENDED_CHECKERS
interface MyTrait: <!INTERFACE_WITH_SUPERCLASS, PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Object<!> {
    override fun toString(): String
    public override fun finalize()
    <!REDUNDANT_VISIBILITY_MODIFIER!>public<!> <!OVERRIDING_FINAL_MEMBER!>override<!> fun wait()
}
