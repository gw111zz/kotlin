/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.backend.konan.InteropFqNames.cstrName
import org.jetbrains.kotlin.backend.konan.InteropFqNames.managedTypeName
import org.jetbrains.kotlin.backend.konan.InteropFqNames.wcstrName
import org.jetbrains.kotlin.builtins.konan.KonanBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.TypeUtils

object InteropFqNames {

    const val cPointerName = "CPointer"
    const val nativePointedName = "NativePointed"

    const val objCObjectBaseName = "ObjCObjectBase"
    const val objCOverrideInitName = "OverrideInit"
    const val objCActionName = "ObjCAction"
    const val objCOutletName = "ObjCOutlet"
    const val objCMethodImpName = "ObjCMethodImp"
    const val exportObjCClassName = "ExportObjCClass"
    const val nativeHeapName = "nativeHeap"

    const val cValueName = "CValue"
    const val cValuesName = "CValues"
    const val cValuesRefName = "CValuesRef"
    const val cEnumName = "CEnum"
    const val cStructVarName = "CStructVar"
    private const val cPointedName = "CPointed"

    const val interopStubsName = "InteropStubs"
    const val managedTypeName = "ManagedType"
    const val memScopeName = "MemScope"
    const val foreignObjCObjectName = "ForeignObjCObject"
    const val cOpaqueName = "COpaque"
    const val objCObjectName = "ObjCObject"
    const val objCObjectBaseMetaName = "ObjCObjectBaseMeta"
    const val objCClassName = "ObjCClass"
    const val objCClassOfName = "ObjCClassOf"
    const val objCProtocolName = "ObjCProtocol"
    const val nativeMemUtilsName = "nativeMemUtils"
    const val cstrName = "cstr"
    const val wcstrName = "wcstr"
    const val cPlusPlusClassName = "CPlusPlusClass"
    const val skiaRefCntName = "SkiaRefCnt"

    const val getObjCClassFunName = "getObjCClass"
    const val objCObjectSuperInitCheckFunName = "superInitCheck"
    const val allocObjCObjectFunName = "allocObjCObject"
    const val typeOfFunName = "typeOf"
    const val objCObjectInitByFunName = "initBy"
    const val objCObjectRawPtrFunName = "objcPtr"
    const val interpretObjCPointerFunName = "interpretObjCPointer"
    const val interpretObjCPointerOrNullFunName = "interpretObjCPointerOrNull"
    const val interpretNullablePointedFunName = "interpretNullablePointed"
    const val interpretCPointerFunName = "interpretCPointer"

    val packageName = FqName("kotlinx.cinterop")

    val cPointer = packageName.child(cPointerName).toUnsafe()
    val nativePointed = packageName.child(nativePointedName).toUnsafe()

    val objCObjectBase = packageName.child(objCObjectBaseName)
    val objCOverrideInit = objCObjectBase.child(objCOverrideInitName)
    val objCAction = packageName.child(objCActionName)
    val objCOutlet = packageName.child(objCOutletName)
    val objCMethodImp = packageName.child(objCMethodImpName)
    val exportObjCClass = packageName.child(exportObjCClassName)

    val cValue = packageName.child(cValueName)
    val cValues = packageName.child(cValuesName)
    val cValuesRef = packageName.child(cValuesRefName)
    val cEnum = packageName.child(cEnumName)
    val cStructVar = packageName.child(cStructVarName)
    val cPointed = packageName.child(cPointedName)

    val interopStubs = packageName.child(interopStubsName)
    val managedType = packageName.child(managedTypeName)
}

private fun FqName.child(nameIdent: String) = child(Name.identifier(nameIdent))

internal class InteropBuiltIns(builtIns: KonanBuiltIns) {

    private val packageScope = builtIns.builtInsModule.getPackage(InteropFqNames.packageName).memberScope

    val nativePointed = packageScope.getContributedClass(InteropFqNames.nativePointedName)

    val cValueWrite = this.packageScope.getContributedFunctions("write")
            .single { it.extensionReceiverParameter?.type?.constructor?.declarationDescriptor?.fqNameSafe == InteropFqNames.cValue }
    val cValueRead = this.packageScope.getContributedFunctions("readValue")
            .single { it.valueParameters.size == 1 }

    val cEnumVar = this.packageScope.getContributedClass("CEnumVar")
    val cStructVar = this.packageScope.getContributedClass("CStructVar")
    val cStructVarType = cStructVar.defaultType.memberScope.getContributedClass("Type")
    private val cPrimitiveVar = this.packageScope.getContributedClass("CPrimitiveVar")
    val cPrimitiveVarType = cPrimitiveVar.defaultType.memberScope.getContributedClass("Type")

    val allocType = this.packageScope.getContributedFunctions("alloc")
            .single { it.extensionReceiverParameter != null
                    && it.valueParameters.singleOrNull()?.name?.toString() == "type" }

    private val cPointer = this.packageScope.getContributedClass(InteropFqNames.cPointerName)

    val cPointerRawValue = cPointer.unsubstitutedMemberScope.getContributedVariables("rawValue").single()

    val cPointerGetRawValue = packageScope.getContributedFunctions("getRawValue").single {
        val extensionReceiverParameter = it.extensionReceiverParameter
        extensionReceiverParameter != null &&
                TypeUtils.getClassDescriptor(extensionReceiverParameter.type)?.fqNameUnsafe == InteropFqNames.cPointer
    }

    val cstr = packageScope.getContributedVariables("cstr").single()
    val wcstr = packageScope.getContributedVariables("wcstr").single()

    val nativePointedRawPtrGetter =
            nativePointed.unsubstitutedMemberScope.getContributedVariables("rawPtr").single().getter!!

    val nativePointedGetRawPointer = packageScope.getContributedFunctions("getRawPointer").single {
        val extensionReceiverParameter = it.extensionReceiverParameter
        extensionReceiverParameter != null &&
                TypeUtils.getClassDescriptor(extensionReceiverParameter.type) == nativePointed
    }

    val managedType = packageScope.getContributedClass(managedTypeName)  // used in CStructVarClassGenerator.kt

    val interopGetPtr = packageScope.getContributedVariables("ptr").single {
        val singleTypeParameter = it.typeParameters.singleOrNull()
        val singleTypeParameterUpperBound = singleTypeParameter?.upperBounds?.singleOrNull()
        val extensionReceiverParameter = it.extensionReceiverParameter

        singleTypeParameterUpperBound != null &&
        extensionReceiverParameter != null &&
        TypeUtils.getClassDescriptor(singleTypeParameterUpperBound)?.fqNameSafe == InteropFqNames.cPointed &&
        extensionReceiverParameter.type == singleTypeParameter.defaultType
    }.getter!!

    val interopManagedGetPtr = packageScope.getContributedVariables("ptr").single {
        val singleTypeParameter = it.typeParameters.singleOrNull()
        val singleTypeParameterUpperBound = singleTypeParameter?.upperBounds?.singleOrNull()
        val extensionReceiverParameter = it.extensionReceiverParameter

        singleTypeParameterUpperBound != null &&
        extensionReceiverParameter != null &&
        TypeUtils.getClassDescriptor(singleTypeParameterUpperBound)?.fqNameSafe == InteropFqNames.cStructVar &&
        TypeUtils.getClassDescriptor(extensionReceiverParameter.type)?.fqNameSafe == InteropFqNames.managedType
    }.getter!!
}

private fun MemberScope.getContributedVariables(name: String) =
        this.getContributedVariables(Name.identifier(name), NoLookupLocation.FROM_BUILTINS)

private fun MemberScope.getContributedClass(name: String): ClassDescriptor =
        this.getContributedClassifier(Name.identifier(name), NoLookupLocation.FROM_BUILTINS) as ClassDescriptor

private fun MemberScope.getContributedFunctions(name: String) =
        this.getContributedFunctions(Name.identifier(name), NoLookupLocation.FROM_BUILTINS)

internal val cKeywords = setOf(
        // Actual C keywords.
        "auto", "break", "case",
        "char", "const", "continue",
        "default", "do", "double",
        "else", "enum", "extern",
        "float", "for", "goto",
        "if", "int", "long",
        "register", "return",
        "short", "signed", "sizeof", "static", "struct", "switch",
        "typedef", "union", "unsigned",
        "void", "volatile", "while",
        // C99-specific.
        "_Bool", "_Complex", "_Imaginary", "inline", "restrict",
        // C11-specific.
        "_Alignas", "_Alignof", "_Atomic", "_Generic", "_Noreturn", "_Static_assert", "_Thread_local",
        // Not exactly keywords, but reserved or standard-defined.
        "and", "not", "or", "xor",
        "bool", "complex", "imaginary",

        // C++ keywords not listed above.
        "alignas", "alignof", "and_eq", "asm",
        "bitand", "bitor", "bool",
        "catch", "char16_t", "char32_t", "class", "compl", "constexpr", "const_cast",
        "decltype", "delete", "dynamic_cast",
        "explicit", "export",
        "false", "friend",
        "inline",
        "mutable",
        "namespace", "new", "noexcept", "not_eq", "nullptr",
        "operator", "or_eq",
        "private", "protected", "public",
        "reinterpret_cast",
        "static_assert",
        "template", "this", "thread_local", "throw", "true", "try", "typeid", "typename",
        "using",
        "virtual",
        "wchar_t",
        "xor_eq"
)
