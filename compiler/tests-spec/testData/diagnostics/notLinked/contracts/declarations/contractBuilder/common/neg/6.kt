// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// !LANGUAGE: +AllowContractsForNonOverridableMembers +AllowReifiedGenericsInContracts
// !DIAGNOSTICS: -UNUSED_VARIABLE -UNUSED_PARAMETER -UNREACHABLE_CODE -UNUSED_EXPRESSION
// !OPT_IN: kotlin.contracts.ExperimentalContracts

/*
 * KOTLIN DIAGNOSTICS NOT LINKED SPEC TEST (NEGATIVE)
 *
 * SECTIONS: contracts, declarations, contractBuilder, common
 * NUMBER: 6
 * DESCRIPTION: contracts with not function parameters in implies.
 * HELPERS: typesProvider
 */

import kotlin.contracts.*

// TESTCASE NUMBER: 1
object case_1 {
    val value_1 = getBoolean()
    const val value_2 = true
    private const val value_3 = false

    fun case_1_1(): Boolean? {
        contract { returnsNotNull() implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_1<!>) }
        return if (value_1) true else null
    }
    fun case_1_2(): Boolean? {
        contract { returns(null) implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_2<!>) }
        return if (value_2) null else true
    }

    fun case_1_3(): Boolean {
        contract { returns(true) implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_3<!>) }
        return value_3
    }
}

// TESTCASE NUMBER: 2
class case_2(value_5: Boolean, val value_1: Boolean) {
    val value_2 = getBoolean()

    companion object {
        const val value_3 = false
        private const val value_4 = true
    }

    init {
        fun case_2_1(): Boolean {
            <!CONTRACT_NOT_ALLOWED!>contract<!> { returns(false) implies (value_5) }
            return !(value_5)
        }
    }

    fun case_2_2(): Boolean? {
        contract { returns(null) implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_1<!>) }
        return if (value_1) null else true
    }

    fun case_2_3(): Boolean {
        contract { returns(true) implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_2<!>) }
        return value_2
    }

    fun case_2_4(): Boolean {
        contract { returns(false) implies (<!ERROR_IN_CONTRACT_DESCRIPTION!>value_3<!>) }
        return !(value_3)
    }

    inline fun <reified K : Number> K.case_2_5(): Boolean? {
        contract { returnsNotNull() implies (<!ERROR_IN_CONTRACT_DESCRIPTION, NON_PUBLIC_CALL_FROM_PUBLIC_INLINE!>value_4<!>) }
        return if (<!NON_PUBLIC_CALL_FROM_PUBLIC_INLINE!>value_4<!>) true else null
    }
}
