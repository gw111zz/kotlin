/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.components

import org.jetbrains.kotlin.analysis.api.components.KtCompletionCandidateChecker
import org.jetbrains.kotlin.analysis.api.components.KtExtensionApplicabilityResult
import org.jetbrains.kotlin.analysis.api.fir.KtFirAnalysisSession
import org.jetbrains.kotlin.analysis.api.fir.symbols.KtFirSymbol
import org.jetbrains.kotlin.analysis.api.lifetime.KtLifetimeToken
import org.jetbrains.kotlin.analysis.api.symbols.KtCallableSymbol
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.getOrBuildFirFile
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.getOrBuildFirOfType
import org.jetbrains.kotlin.analysis.low.level.api.fir.resolver.ResolutionParameters
import org.jetbrains.kotlin.analysis.low.level.api.fir.resolver.SingleCandidateResolutionMode
import org.jetbrains.kotlin.analysis.low.level.api.fir.resolver.SingleCandidateResolver
import org.jetbrains.kotlin.analysis.low.level.api.fir.sessions.llFirSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.ContextCollector
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirSafeCallExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildExpressionStub
import org.jetbrains.kotlin.fir.resolve.DoubleColonLHS
import org.jetbrains.kotlin.fir.resolve.SessionHolderImpl
import org.jetbrains.kotlin.fir.resolve.calls.FirErrorReferenceWithCandidate
import org.jetbrains.kotlin.fir.resolve.calls.ImplicitReceiverValue
import org.jetbrains.kotlin.fir.symbols.lazyResolveToPhase
import org.jetbrains.kotlin.fir.types.receiverType
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStatementExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.utils.exceptions.errorWithAttachment
import org.jetbrains.kotlin.utils.exceptions.withPsiEntry

internal class KtFirCompletionCandidateChecker(
    override val analysisSession: KtFirAnalysisSession,
    override val token: KtLifetimeToken,
) : KtCompletionCandidateChecker(), KtFirAnalysisSessionComponent {
    override fun checkExtensionFitsCandidate(
        firSymbolForCandidate: KtCallableSymbol,
        originalFile: KtFile,
        nameExpression: KtSimpleNameExpression,
        possibleExplicitReceiver: KtExpression?,
    ): KtExtensionApplicabilityResult {
        require(firSymbolForCandidate is KtFirSymbol<*>)
        firSymbolForCandidate.firSymbol.lazyResolveToPhase(FirResolvePhase.STATUS)
        val declaration = firSymbolForCandidate.firSymbol.fir as FirCallableDeclaration
        return checkExtension(declaration, originalFile, nameExpression, possibleExplicitReceiver)
    }

    private fun checkExtension(
        candidateSymbol: FirCallableDeclaration,
        originalFile: KtFile,
        nameExpression: KtSimpleNameExpression,
        possibleExplicitReceiver: KtExpression?,
    ): KtExtensionApplicabilityResult {
        val file = originalFile.getOrBuildFirFile(firResolveSession)
        val resolver = SingleCandidateResolver(firResolveSession.useSiteFirSession, file)
        val explicitReceiverExpression = possibleExplicitReceiver?.getMatchingFirExpressionForCallReceiver(resolver)
        val implicitReceivers = getImplicitReceivers(nameExpression)
        for (implicitReceiverValue in implicitReceivers) {
            val resolutionParameters = ResolutionParameters(
                singleCandidateResolutionMode = SingleCandidateResolutionMode.CHECK_EXTENSION_FOR_COMPLETION,
                callableSymbol = candidateSymbol.symbol,
                implicitReceiver = implicitReceiverValue,
                explicitReceiver = explicitReceiverExpression,
                allowUnsafeCall = true,
                allowUnstableSmartCast = true,
            )
            resolver.resolveSingleCandidate(resolutionParameters)?.let { call ->
                val substitutor = call.createSubstitutorFromTypeArguments() ?: return@let null
                val receiverCastRequired = call.calleeReference is FirErrorReferenceWithCandidate

                return when {
                    candidateSymbol is FirVariable && candidateSymbol.symbol.resolvedReturnType.receiverType(rootModuleSession) != null -> {
                        KtExtensionApplicabilityResult.ApplicableAsFunctionalVariableCall(substitutor, receiverCastRequired, token)
                    }
                    else -> {
                        KtExtensionApplicabilityResult.ApplicableAsExtensionCallable(substitutor, receiverCastRequired, token)
                    }
                }
            }
        }
        return KtExtensionApplicabilityResult.NonApplicable(token)
    }

    private fun getImplicitReceivers(fakeNameExpression: KtSimpleNameExpression): Sequence<ImplicitReceiverValue<*>?> {
        val fakeFile = fakeNameExpression.containingKtFile
        val fakeFirFile = fakeFile.getOrBuildFirFile(firResolveSession)

        val sessionHolder = run {
            val firSession = fakeFirFile.llFirSession
            val scopeSession = firResolveSession.getScopeSessionFor(firSession)
            SessionHolderImpl(firSession, scopeSession)
        }

        val elementContext = ContextCollector.process(fakeFirFile, sessionHolder, fakeNameExpression, bodyElement = null)

        val towerDataContext = elementContext?.towerDataContext
            ?: errorWithAttachment("Cannot find enclosing declaration for ${fakeNameExpression::class}") {
                withPsiEntry("fakeNameExpression", fakeNameExpression)
            }

        return sequence {
            yield(null) // otherwise explicit receiver won't be checked when there are no implicit receivers in completion position
            yieldAll(towerDataContext.implicitReceiverStack)
            for (towerDataElement in towerDataContext.towerDataElements) {
                yieldAll(towerDataElement.contextReceiverGroup.orEmpty())
            }
        }
    }

    /**
     * It is not enough to just call the `getOrBuildFirOfType` on [this] receiver expression, because for calls
     * like `foo?.bar()` the receiver is additionally wrapped into `FirCheckedSafeCallSubject`, which is important
     * for type-checks during resolve.
     *
     * @receiver PSI receiver expression in some qualified expression (e.g. `foo` in `foo?.bar()`, `a` in `a.b`)
     * @return A FIR expression which most precisely represents the receiver for the corresponding FIR call.
     */
    private fun KtExpression.getMatchingFirExpressionForCallReceiver(resolver: SingleCandidateResolver): FirExpression? {
        // FIR for KtStatementExpression is not FirExpression
        if (this is KtStatementExpression) {
            return null
        }

        val parent = parent
        if (parent is KtCallableReferenceExpression) {
            return getMatchingFirExpressionForCallableReferenceReceiver(parent, resolver)
        }

        val psiWholeCall = this.getQualifiedExpressionForReceiver()
        if (psiWholeCall !is KtSafeQualifiedExpression) return this.getOrBuildFirOfType<FirExpression>(firResolveSession)

        val firSafeCall = psiWholeCall.getOrBuildFirOfType<FirSafeCallExpression>(firResolveSession)
        return firSafeCall.checkedSubjectRef.value
    }

    private fun KtExpression.getMatchingFirExpressionForCallableReferenceReceiver(
        callableReferenceExpression: KtCallableReferenceExpression,
        resolver: SingleCandidateResolver,
    ): FirExpression? {
        val explicitReceiverExpression = this.getOrBuildFirOfType<FirExpression>(firResolveSession)
        val callableReferenceAccess = callableReferenceExpression.getOrBuildFirOfType<FirCallableReferenceAccess>(firResolveSession)
        val lhs = resolver.resolveDoubleColonLHS(callableReferenceAccess)
        if (lhs is DoubleColonLHS.Type) {
            return buildExpressionStub {
                source = explicitReceiverExpression.source
                coneTypeOrNull = lhs.type
            }
        }
        return explicitReceiverExpression
    }
}
