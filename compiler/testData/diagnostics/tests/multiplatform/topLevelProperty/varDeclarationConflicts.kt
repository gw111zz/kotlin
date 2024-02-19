// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect var <!NO_ACTUAL_FOR_EXPECT!>x1<!>: Int

expect var <!NO_ACTUAL_FOR_EXPECT, REDECLARATION, REDECLARATION{JVM}!>x2<!>: Int
expect var <!NO_ACTUAL_FOR_EXPECT, REDECLARATION, REDECLARATION{JVM}!>x2<!>: Int

expect var <!NO_ACTUAL_FOR_EXPECT!>x3<!>: <!NO_ACTUAL_FOR_EXPECT{JVM}!>Int<!>

// MODULE: jvm()()(common)
// FILE: main.kt
actual <!ACTUAL_WITHOUT_EXPECT!>val<!> x1 = 1

actual var <!AMBIGUOUS_EXPECTS!>x2<!> = 2

actual var x3: <!ACTUAL_WITHOUT_EXPECT!>String<!> = 1
