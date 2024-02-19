// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect class <!NO_ACTUAL_FOR_EXPECT!>A<!> {
    class B {}
}

class <!PACKAGE_OR_CLASSIFIER_REDECLARATION{JVM}!>B<!> {
    class B {
        class B {}
    }
}

// MODULE: jvm()()(common)
// FILE: main.kt
actual typealias <!NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS!>A<!> = B.B

class <!PACKAGE_OR_CLASSIFIER_REDECLARATION!>B<!> {
    class B {}
}
