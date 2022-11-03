// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER -UNUSED_EXPRESSION
// SKIP_TXT


// FILE: TestCase1.kt
// TESTCASE NUMBER: 1
package testPackCase1
private fun Int.boo(x: Int, a: Any = ""): String = TODO() //(1.1)
private fun Int.boo(x: Int, a: Any = "", b: Any = 1): Unit = TODO() //(1.1)
private fun Int.boo(x: Long, a: Any = ""): Unit = TODO() //(1.2)
private fun Int.boo(x: Long, a: Any = "", b: Any = 1): Unit = TODO() //(1.2)
private fun Int.boo(x: Short, a: Any = ""): Unit = TODO() //(1.3)
private fun Int.boo(x: Short, a: Any = "", b: Any = 1): Unit = TODO() //(1.3)
private fun Int.boo(x: Byte, a: Any = ""): Unit = TODO() //(1.4)
private fun Int.boo(x: Byte, a: Any = "", b: Any = 1): Unit = TODO() //(1.4)

fun case1() {
    1.apply {
        //to (1.1)
        <!DEBUG_INFO_CALL("fqName: testPackCase1.boo; typeCall: extension function")!>boo(1)<!>
        //(1.1) return type is String
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>boo(1)<!>
        //to (1.1)
        <!DEBUG_INFO_CALL("fqName: testPackCase1.boo; typeCall: extension function")!>boo(x = 1)<!>
        //(1.1) return type is String
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>boo(x = 1)<!>
    }
}


// FILE: TestCase2.kt
// TESTCASE NUMBER: 2
package testPackCase2

class Case2 {
    private fun Int.boo(x: Int, y: Int, a: Any = ""): String = TODO() //(1.1)
    private fun Int.boo(x: Int, y: Int, a: Any = "", b: Any = 1): Unit = TODO() //(1.1)
    private fun Short.boo(x: Int, y: Int, a: Any = ""): Unit = TODO() //(1.2)
    private fun Short.boo(x: Int, y: Int, a: Any = "", b: Any = 1): Unit = TODO() //(1.2)
    private fun Long.boo(x: Int, y: Int, a: Any = ""): Unit = TODO() //(1.3)
    private fun Long.boo(x: Int, y: Int, a: Any = "", b: Any = 1): Unit = TODO() //(1.3)
    private fun Byte.boo(x: Int, y: Int, a: Any = ""): Unit = TODO() //(1.4)
    private fun Byte.boo(x: Int, y: Int, a: Any = "", b: Any = 1): Unit = TODO() //(1.4)

    fun case2() {
        1.apply {
            //to (1.1)
            <!DEBUG_INFO_CALL("fqName: testPackCase2.Case2.boo; typeCall: extension function")!>boo(1, 2)<!>
            //(1.1) return type is String
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>boo(1, 2)<!>
            //to (1.1)
            <!DEBUG_INFO_CALL("fqName: testPackCase2.Case2.boo; typeCall: extension function")!>boo(x = 1, y = 2)<!>
            //(1.1) return type is String
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>boo(x = 1, y = 2)<!>
        }
    }
}




// FILE: TestCase4.kt
// TESTCASE NUMBER: 4
package testPackCase4

private operator fun Int.get(x: String, a: Any = ""): String = TODO() //(1.1)
private operator fun Int.get(x: String, a: Any = "", b: Any = 1): Unit = TODO() //(1.1)
private operator fun Short.get(x: String, a: Any = ""): Unit = TODO() //(1.2)
private operator fun Short.get(x: String, a: Any = "", b: Any = 1): Unit = TODO() //(1.2)
private operator fun Byte.get(x: String, a: Any = ""): Unit = TODO() //(1.3)
private operator fun Byte.get(x: String, a: Any = "", b: Any = 1): Unit = TODO() //(1.3)
private operator fun Long.get(x: String, a: Any = ""): Unit = TODO() //(1.4)
private operator fun Long.get(x: String, a: Any = "", b: Any = 1): Unit = TODO() //(1.4)

fun case4() {
    1.apply {
        //to (1.1)
        <!DEBUG_INFO_CALL("fqName: testPackCase4.get; typeCall: operator extension function")!>get("1")<!>
        //(1.1) return type is String
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>get("1")<!>
        //to (1.1)
        this["1"]
        //(1.1) return type is String
        this["1"]
    }
}
