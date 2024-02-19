// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect enum class <!NO_ACTUAL_FOR_EXPECT!>Direction<!> {
    NORTH, SOUTH, WEST, EAST
}

// MODULE: jvm()()(common)
// FILE: jvm.kt
actual enum class Direction {
    SOUTH, WEST, EAST, NORTH
}

// MODULE: js()()(common)
// FILE: js.kt
actual enum class Direction {
    SOUTH, WEST, E, EAST, NORTH
}
