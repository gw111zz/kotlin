package org.jetbrains.kotlin.objcexport

import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.symbols.KtFunctionSymbol

/**
 * Very basic implementation
 * - [org.jetbrains.kotlin.backend.konan.objcexport.ObjCExportMapperKt.getBaseMethods]
 * - [org.jetbrains.kotlin.backend.konan.objcexport.ObjCExportMapperKt.isBaseMethod]
 */
context(KtAnalysisSession)
internal val KtFunctionSymbol.baseMethod: KtFunctionSymbol
    get() {
        val overriddenSymbols = getAllOverriddenSymbols()
        return if (overriddenSymbols.isEmpty()) this
        else overriddenSymbols.last() as KtFunctionSymbol
    }