// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

// TESTCASE NUMBER: 1
fun case_1() {
    var x: Int? = 11
    x!!
    try {x = null;} finally { <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!> <!UNSAFE_OPERATOR_CALL!>+=<!> 10; }
}

// TESTCASE NUMBER: 2
fun case_2() {
    var x: Boolean? = true
    if (x != null) {
        try {
            throw Exception()
        } catch (e: Exception) {
            x = null
        }
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>not()
    }
}

// TESTCASE NUMBER: 3
fun case_3() {
    var x: Boolean? = true
    if (x is Boolean) {
        try {
            throw Exception()
        } catch (e: Exception) {
            x = null
        }
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>not()
    }
}

// TESTCASE NUMBER: 4
fun case_4() {
    var x: Boolean? = true
    x as Boolean
    try {
        x = null
    } finally { }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!><!UNSAFE_CALL!>.<!>not()
}
