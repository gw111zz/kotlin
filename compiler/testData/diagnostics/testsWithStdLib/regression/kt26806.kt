// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// FIR_IDENTICAL
const val myPi = kotlin.math.PI

annotation class Anno(val d: Double)

@Anno(kotlin.math.PI)
fun f() {}

@Anno(myPi)
fun g() {}