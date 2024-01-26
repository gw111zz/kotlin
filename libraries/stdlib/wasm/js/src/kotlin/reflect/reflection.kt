/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// a package is omitted to get declarations directly under the module
package kotlin.wasm.internal

import kotlin.reflect.*
import kotlin.reflect.wasm.internal.*

@Suppress("UNUSED_PARAMETER")
private fun getJsClassName(jsKlass: JsAny): String? =
    js("jsKlass.name")

@Suppress("UNUSED_PARAMETER")
private fun instanceOf(ref: JsAny, jsKlass: JsAny): Boolean =
    js("ref instanceof jsKlass")

@Suppress("UNUSED_PARAMETER")
private fun getConstructor(obj: JsAny): JsAny? =
    js("obj.constructor")

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> getKClass(typeInfoData: Any?): KClass<T> = when (typeInfoData) {
    is TypeInfoData -> KClassImpl(typeInfoData)
    is JsTypeInfoData -> KExternalClassImpl(typeInfoData)
    null -> ErrorKClass as KClass<T>
    else -> error("Unknown KClass constructor ${typeInfoData::class.simpleName}")
}

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any> getKClassForObject(obj: Any): KClass<T> {
    if (obj !is JsExternalBox) return KClassImpl(getTypeInfoTypeDataByPtr(obj.typeInfo))
    val jsConstructor = getConstructor(obj.ref) ?: return ErrorKClass as KClass<T>
    return KExternalClassImpl(JsTypeInfoData(jsConstructor))
}

internal class KExternalClassImpl<T : Any>(private val jsTypeInfoData: JsTypeInfoData) : KClass<T> {
    override val simpleName: String? get() = getJsClassName(jsTypeInfoData.jsConstructor)
    override val qualifiedName: String? = null

    override fun isInstance(value: Any?): Boolean =
        value is JsExternalBox && instanceOf(value.ref, jsTypeInfoData.jsConstructor)

    override fun equals(other: Any?): Boolean =
        other is KExternalClassImpl<*> && jsTypeInfoData.jsConstructor == other.jsTypeInfoData.jsConstructor

    override fun hashCode(): Int = simpleName.hashCode()

    override fun toString(): String = "class $simpleName"
}