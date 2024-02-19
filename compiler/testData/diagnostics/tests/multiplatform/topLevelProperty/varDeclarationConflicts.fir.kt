// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect var x1: Int

expect var <!REDECLARATION!>x2<!>: Int
expect var <!REDECLARATION!>x2<!>: Int

expect var x3: Int

// MODULE: jvm()()(common)
// FILE: main.kt
actual val <!ACTUAL_WITHOUT_EXPECT!>x1<!> = 1

<!AMBIGUOUS_EXPECTS!>actual var x2 = 2<!>

actual var <!ACTUAL_WITHOUT_EXPECT!>x3<!>: String = 1
