/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// a package is omitted to get declarations directly under the module
package kotlin.wasm.internal

import kotlin.reflect.*
import kotlin.reflect.wasm.internal.*

internal fun <T : Any> getKClass(typeInfoData: Any?): KClass<T> = when (typeInfoData) {
    is TypeInfoData -> KClassImpl(typeInfoData)
    null -> error("KClass constructor cannot be null")
    else -> error("Unknown KClass constructor ${typeInfoData::class.simpleName}")
}

internal actual fun <T : Any> getKClassForObject(obj: Any): KClass<T> =
    KClassImpl(getTypeInfoTypeDataByPtr(obj.typeInfo))