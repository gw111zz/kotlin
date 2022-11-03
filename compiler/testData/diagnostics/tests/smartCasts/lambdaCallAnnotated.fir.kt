// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// See KT-9134: smart cast is not provided inside lambda call

@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class My

fun bar(): Int = @My {
    var i: Int?
    i = 42
    i
}()