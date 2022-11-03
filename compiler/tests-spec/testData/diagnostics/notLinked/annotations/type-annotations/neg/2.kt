// FIR_DISABLE_LAZY_RESOLVE_CHECKS
/*
 * KOTLIN DIAGNOSTICS NOT LINKED SPEC TEST (NEGATIVE)
 *
 * SECTIONS: annotations, type-annotations
 * NUMBER: 2
 * DESCRIPTION: Type annotations on supertypes with unresolved reference in parameters.
 * ISSUES: KT-28424
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 */
@Target(AnnotationTarget.TYPE)
annotation class Ann(val x: Int)

abstract class Foo : @Ann(<!UNRESOLVED_REFERENCE!>unresolved_reference<!>) Any()
