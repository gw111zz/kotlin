/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.sir.passes.builder

import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.symbols.KtFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbol
import org.jetbrains.kotlin.analysis.api.types.KtType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.sir.*
import org.jetbrains.kotlin.sir.builder.buildFunction
import org.jetbrains.kotlin.sir.builder.buildGetter
import org.jetbrains.kotlin.sir.builder.buildSetter
import org.jetbrains.kotlin.sir.builder.buildVariable
import org.jetbrains.kotlin.sir.util.SirSwiftModule


public fun KtAnalysisSession.buildSirDeclarationList(from: KtElement): List<SirDeclaration> {
    val res = mutableListOf<SirDeclaration>()
    from.acceptChildren(
        PsiToSirTranslationCollector(
            res,
            this,
            PsiToSirTranslatableChecker(this),
            PsiToSirElementTranslation(this)
        )
    )
    return res.toList()
}

private abstract class PsiToSirTranslation<T>(
    val analysisSession: KtAnalysisSession,
) : KtVisitor<T, Unit?>() {
    abstract override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): T
    abstract override fun visitProperty(property: KtProperty, data: Unit?): T
}

private class PsiToSirTranslatableChecker(
    analysisSession: KtAnalysisSession,
) : PsiToSirTranslation<Boolean>(analysisSession) {

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): Boolean = with(analysisSession) {
        val functionIsPublicAndTopLevel = function.isPublic
                && function.isTopLevel
                && !function.isAnonymous
        val functionSymbolIsTranslatable = (function.getFunctionLikeSymbol() as? KtFunctionSymbol)
            ?.let { symbol ->
                !symbol.isSuspend
                        && !symbol.isInline
                        && !symbol.isExtension
                        && !symbol.isOperator
            }
            ?: true
        return functionIsPublicAndTopLevel && functionSymbolIsTranslatable
    }

    override fun visitProperty(property: KtProperty, data: Unit?): Boolean {
        // we are able to consume any public property?
        return property.isPublic
                && property.isTopLevel
    }
}

private class PsiToSirTranslationCollector(
    private val res: MutableList<SirDeclaration>,
    analysisSession: KtAnalysisSession,
    private val checker: PsiToSirTranslation<Boolean>,
    private val translator: PsiToSirTranslation<SirDeclaration>, // List<SirDeclaration>?
) : PsiToSirTranslation<Unit>(analysisSession) {

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?) {
        function
            .takeIf {
                it.accept(checker, data)
            }
            ?.let {
                res.add(it.accept(translator, data))
            }
    }

    override fun visitProperty(property: KtProperty, data: Unit?) {
        property
            .takeIf {
                it.accept(checker, data)
            }
            ?.let {
                res.add(it.accept(translator, data))
            }
    }
}

private class PsiToSirElementTranslation(
    analysisSession: KtAnalysisSession,
) : PsiToSirTranslation<SirDeclaration>(analysisSession) {
    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): SirDeclaration = with(analysisSession) {
        buildSirFunctionFromPsi(function)
    }

    override fun visitProperty(property: KtProperty, data: Unit?): SirDeclaration = with(analysisSession) {
        buildSirVariableFromPsi(property)
    }
}

context(KtAnalysisSession)
internal fun buildSirFunctionFromPsi(function: KtNamedFunction): SirFunction = buildFunction {
    val symbol = function.getFunctionLikeSymbol()
    val callableId = symbol.callableIdIfNonLocal
    origin = KotlinSource(symbol)

    // this check is not ideal. We assume that there will be no further moves of the declaration,
    // especially that there will be no changes in "static" quality.
    // That is not fully true. For example, during KT-65127
    // we may end up removing the whole enum structure at top of the declaration, and that will
    // lift the need to keep it static.
    // this problem will be addressed somewhere in the future
    val isRootPackage = callableId?.packageName?.isRoot
    isStatic = if (isRootPackage == true) false else function.isTopLevel

    name = callableId?.callableName?.asString() ?: "UNKNOWN_FUNCTION_NAME"

    symbol.valueParameters.mapTo(parameters) {
        SirParameter(
            argumentName = it.name.asString(),
            type = buildSirNominalType(it.returnType)
        )
    }
    returnType = buildSirNominalType(symbol.returnType)
    documentation = function.docComment?.text
}

context(KtAnalysisSession)
internal fun buildSirVariableFromPsi(variable: KtProperty): SirVariable = buildVariable {
    val symbol = variable.getVariableSymbol()
    val callableId = symbol.callableIdIfNonLocal
    origin = KotlinSource(symbol)

    // this check is not ideal. We assume that there will be no further moves of the declaration,
    // especially that there will be no changes in "static" quality.
    // That is not fully true. For example, during KT-65127
    // we may end up removing the whole enum structure at top of the declaration, and that will
    // lift the need to keep it static.
    // this problem will be addressed somewhere in the future
    val isRootPackage = callableId?.packageName?.isRoot
    isStatic = if (isRootPackage == true) false else variable.isTopLevel

    name = callableId?.callableName?.asString() ?: "UNKNOWN_VARIABLE_NAME"

    type = buildSirNominalType(symbol.returnType)

    getter = buildGetter {}
    setter = if (variable.isVar) buildSetter {} else null
}.also {
    it.getter.parent = it
    it.setter?.parent = it
}

public data class KotlinSource(
    val symbol: KtSymbol,
) : SirOrigin.Foreign.SourceCode


context(KtAnalysisSession)
private fun buildSirNominalType(it: KtType): SirNominalType = SirNominalType(
    when {
        it.isUnit -> SirSwiftModule.void

        it.isByte -> SirSwiftModule.int8
        it.isShort -> SirSwiftModule.int16
        it.isInt -> SirSwiftModule.int32
        it.isLong -> SirSwiftModule.int64

        it.isUByte -> SirSwiftModule.uint8
        it.isUShort -> SirSwiftModule.uint16
        it.isUInt -> SirSwiftModule.uint32
        it.isULong -> SirSwiftModule.uint64

        it.isBoolean -> SirSwiftModule.bool

        it.isDouble -> SirSwiftModule.double
        it.isFloat -> SirSwiftModule.float
        else ->
            throw IllegalArgumentException("Swift Export does not support argument type: ${it.asStringForDebugging()}")
    }
)
