// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_EXPRESSION

abstract class Foo<T>

abstract class Bar<T> : Foo<T>(), Comparable<Bar<T>>

object Scope {
    fun <T : Comparable<T>, S : T> greater(x: Bar<in S>, other: Foo<T>) {}

    object Nested {
        fun <T : Comparable<T>, S : T> greater(x: Bar<in S>, t: T) {}

        fun test(b: Bar<Long>) {
            <!DEBUG_INFO_CALL("fqName: Scope.greater; typeCall: function")!>greater(b, b)<!>
        }
    }
}

object OnlyOne {
    fun <T : Comparable<T>, S : T> greater(x: Bar<in S>, t: T) {}

    fun test(b: Bar<Long>) {
        <!DEBUG_INFO_CALL("fqName: fqName is unknown; typeCall: unresolved")!><!INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION!>greater<!>(b, b)<!>
    }
}

object GoodOldCandidate {
    fun <T : Comparable<T>, S : T> greater(x: Bar<in S>, t: T) {}

    object Nested {
        fun <T : Comparable<T>, S : T> greater(x: Bar<in S>, other: Foo<T>) {}

        fun test(b: Bar<Long>) {
            <!DEBUG_INFO_CALL("fqName: GoodOldCandidate.Nested.greater; typeCall: function")!>greater(b, b)<!>
        }
    }
}