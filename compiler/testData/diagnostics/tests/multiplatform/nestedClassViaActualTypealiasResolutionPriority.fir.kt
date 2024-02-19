// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect class A {
    class B {}
}

class B {
    class B {
        class B {}
    }
}

// MODULE: jvm()()(common)
// FILE: main.kt
actual typealias <!NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS!>A<!> = B.B

class B {
    class B {}
}
