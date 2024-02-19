// LANGUAGE: +MultiPlatformProjects
// IGNORE_DIAGNOSTIC_API
// IGNORE_REVERSED_RESOLVE

// MODULE: common
// TARGET_PLATFORM: Common
expect val <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{COMMON}!>x1<!>: Int

expect val x2: Int

expect val <!AMBIGUOUS_ACTUALS{JVM}, EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE{COMMON}!>x3<!>: Int

expect val <!NO_ACTUAL_FOR_EXPECT{JVM}!>x4<!>: Int

// MODULE: intermediate()()(common)
// TARGET_PLATFORM: Common
actual val <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE!>x1<!> = 0

actual val <!EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE, REDECLARATION{JVM}!>x3<!> = 0

// MODULE: main()()(intermediate)
actual val x2 = 1

actual val <!REDECLARATION!>x3<!> = 1

fun foo() {
    <!VAL_REASSIGNMENT!>x4<!> = 1
}
