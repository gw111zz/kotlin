// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// FIR_IDENTICAL
class A : Function0<Int> {
    override fun invoke(): Int = 1
}

fun main() {
    A()()
}