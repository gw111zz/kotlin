/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.diagnostics.js

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.fir.analysis.diagnostics.checkMissingMessages
import org.jetbrains.kotlin.fir.analysis.diagnostics.js.FirJsErrors.JS_BUILTIN_NAME_CLASH
import org.jetbrains.kotlin.fir.analysis.diagnostics.js.FirJsErrors.JS_MODULE_PROHIBITED_ON_VAR
import org.jetbrains.kotlin.fir.analysis.diagnostics.js.FirJsErrors.WRONG_JS_QUALIFIER

@Suppress("unused")
object FirJsErrorsDefaultMessages : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("FIR").also { map ->
        map.put(WRONG_JS_QUALIFIER, "Qualifier contains illegal characters")
        map.put(JS_MODULE_PROHIBITED_ON_VAR, "@JsModule and @JsNonModule annotations prohibited for 'var' declarations. Use 'val' instead.")
        map.put(JS_BUILTIN_NAME_CLASH, "JavaScript name generated for this declaration clashes with built-in declaration {1}", STRING)

        map.checkMissingMessages(FirJsErrors)
    }
}