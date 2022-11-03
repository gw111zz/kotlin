// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !OPT_IN: kotlin.contracts.ExperimentalContracts

// FILE: contracts.kt

package contracts

import kotlin.contracts.*

// TESTCASE NUMBER: 1
fun <T : Any?> T?.case_1() {
    contract { returns() implies (this@case_1 is Number || this@case_1 is Int) }
    if (!(this@case_1 is Number || this@case_1 is Int)) throw Exception()
}

// TESTCASE NUMBER: 2
inline fun <reified T : Any?> T?.case_2(value_2: Number, value_3: Any?, value_4: String?) {
    contract { returns() implies ((this@case_2 is Number || this@case_2 is Int) && value_2 is Int && value_3 != null && value_3 is Number && value_4 != null) }
    if (!((this is Number || this is Int) && value_2 is Int && value_3 != null && value_3 is Number && value_4 != null)) throw Exception()
}

// FILE: main.kt

import contracts.*

// TESTCASE NUMBER: 1
fun case_1(value_1: Any?) {
    value_1.case_1()
    println(value_1.toByte())
}

// TESTCASE NUMBER: 2
fun case_2(value_1: Any?, value_2: Number, value_3: Any?, value_4: String?) {
    value_1.case_2(value_2, value_3, value_4)
    println(value_1.toByte())
}
