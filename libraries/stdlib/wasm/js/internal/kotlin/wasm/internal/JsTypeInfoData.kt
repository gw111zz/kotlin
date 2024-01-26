/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")  // Used by compiler

package kotlin.wasm.internal

internal class JsTypeInfoData(val jsConstructor: JsAny)

internal fun wasmGetJsTypeInfoData(jsConstructor: JsAny?): JsTypeInfoData? =
    jsConstructor?.let(::JsTypeInfoData)