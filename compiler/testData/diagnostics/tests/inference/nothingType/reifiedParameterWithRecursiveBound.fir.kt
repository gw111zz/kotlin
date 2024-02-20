// Issue: KT-37621

class Inv<T>
class In<in I>
class Out<out O>

inline fun <reified TB : Inv<TB>> invBound(): TB = TODO()
inline fun <reified IB : In<IB>> inBound(): IB = TODO()
inline fun <reified OB : Out<OB>> outBound(): OB = TODO()

inline fun <reified T : Inv<T>> testInv(): T {
    return try {
        <!DEBUG_INFO_EXPRESSION_TYPE("T")!>invBound()<!>
    } catch (ex: Exception) {
        throw Exception()
    }
}

inline fun <reified T : Inv<T>> testInvNested(): T {
    return try {
        try {
            <!DEBUG_INFO_EXPRESSION_TYPE("T")!>invBound()<!>
        } catch (ex: Exception) {
            throw Exception()
        }
    } catch (ex: Exception) {
        throw Exception()
    }
}

inline fun <reified T : In<T>> testIn(): T {
    return try {
        <!DEBUG_INFO_EXPRESSION_TYPE("T")!>inBound()<!>
    } catch (ex: Exception) {
        throw Exception()
    }
}

inline fun <reified T : In<T>> testInNested(): T {
    return try {
        try {
            <!DEBUG_INFO_EXPRESSION_TYPE("T")!>inBound()<!>
        } catch (ex: Exception) {
            throw Exception()
        }
    } catch (ex: Exception) {
        throw Exception()
    }
}

inline fun <reified T : Out<T>> testOut(): T {
    return try {
        <!DEBUG_INFO_EXPRESSION_TYPE("T")!>outBound()<!>
    } catch (ex: Exception) {
        throw Exception()
    }
}

inline fun <reified T : Out<T>> testOutNested(): T {
    return try {
        try {
            <!DEBUG_INFO_EXPRESSION_TYPE("T")!>outBound()<!>
        } catch (ex: Exception) {
            throw Exception()
        }
    } catch (ex: Exception) {
        throw Exception()
    }
}
