// ISSUE: KT-65881, KT-65760

object list {
    object iterator {
        operator fun invoke(): Iterator<Int> = null!!
    }

    object plus {
        operator fun invoke(arg: Int) = arg
    }
}

class list2 {
    companion object iterator {
        operator fun invoke(): Iterator<Int> = null!!
    }
}

class A {
    operator fun invoke(): Iterator<Int> = null!!
}

class I {
    val component1 = { "UwU" }
}

operator fun I.component1(): String = "Not UwU"

fun test(a: A) {
    for (x in <!ITERATOR_MISSING!>list<!>) {}
    for (x in <!ITERATOR_MISSING!>list2<!>) {}
    list <!UNRESOLVED_REFERENCE!>+<!> 3
    val (uwu) = I()
}
