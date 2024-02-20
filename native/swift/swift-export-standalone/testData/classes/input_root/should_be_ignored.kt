public annotation class OptIn

enum ENUM {
    class INSIDE_ENUM
}

interface OUTSIDE_PROTO {
    class INSIDE_PROTO
}

class INHERITANCE_COUPLE : OUTSIDE_PROTO.INSIDE_PROTO, OUTSIDE_PROTO
class INHERITANCE_SINGLE : OUTSIDE_PROTO.INSIDE_PROTO

object OBJECT

data class DATA_CLASS(val a: Int)

inline class INLINE_CLASS(val a: Int)

abstract class ABSTRACT_CLASS

sealed class SEALED {
    object O : SEALED()
}