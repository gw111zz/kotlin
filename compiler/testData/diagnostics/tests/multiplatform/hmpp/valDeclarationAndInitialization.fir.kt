// LANGUAGE: +MultiPlatformProjects
// IGNORE_DIAGNOSTIC_API
// IGNORE_REVERSED_RESOLVE

// MODULE: common
// TARGET_PLATFORM: Common
expect val x1: Int

expect val x2: Int

<!AMBIGUOUS_ACTUALS{JVM}, NO_ACTUAL_FOR_EXPECT{JVM}!>expect val x3: Int<!>

<!NO_ACTUAL_FOR_EXPECT{JVM}!>expect val x4: Int<!>

// MODULE: intermediate()()(common)
// TARGET_PLATFORM: Common
actual val x1 = 0

actual val x3 = 0

// MODULE: main()()(intermediate)
actual val x2 = 1

actual val x3 = 1

fun foo() {
    <!VAL_REASSIGNMENT!>x4<!> = 1
}
