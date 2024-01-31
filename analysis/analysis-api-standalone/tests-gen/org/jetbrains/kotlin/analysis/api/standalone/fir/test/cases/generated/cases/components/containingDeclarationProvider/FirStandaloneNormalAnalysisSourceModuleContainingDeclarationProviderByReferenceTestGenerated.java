/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.standalone.fir.test.cases.generated.cases.components.containingDeclarationProvider;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.standalone.fir.test.configurators.AnalysisApiFirStandaloneModeTestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.containingDeclarationProvider.AbstractContainingDeclarationProviderByReferenceTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference")
@TestDataPath("$PROJECT_ROOT")
public class FirStandaloneNormalAnalysisSourceModuleContainingDeclarationProviderByReferenceTestGenerated extends AbstractContainingDeclarationProviderByReferenceTest {
    @NotNull
    @Override
    public AnalysisApiTestConfigurator getConfigurator() {
        return AnalysisApiFirStandaloneModeTestConfiguratorFactory.INSTANCE.createConfigurator(
            new AnalysisApiTestConfiguratorFactoryData(
                FrontendKind.Fir,
                TestModuleKind.Source,
                AnalysisSessionMode.Normal,
                AnalysisApiMode.Standalone
            )
        );
    }

    @Test
    public void testAllFilesPresentInContainingDeclarationByReference() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @Test
    @TestMetadata("localFunction.kt")
    public void testLocalFunction() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/localFunction.kt");
    }

    @Test
    @TestMetadata("localFunctionFromInside.kt")
    public void testLocalFunctionFromInside() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/localFunctionFromInside.kt");
    }

    @Test
    @TestMetadata("nestedClass.kt")
    public void testNestedClass() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/nestedClass.kt");
    }

    @Test
    @TestMetadata("nestedClassFromInside.kt")
    public void testNestedClassFromInside() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/nestedClassFromInside.kt");
    }

    @Test
    @TestMetadata("propertyAccessor.kt")
    public void testPropertyAccessor() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/propertyAccessor.kt");
    }

    @Test
    @TestMetadata("propertyAccessorFromInside.kt")
    public void testPropertyAccessorFromInside() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/propertyAccessorFromInside.kt");
    }

    @Test
    @TestMetadata("propertyField.kt")
    public void testPropertyField() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/propertyField.kt");
    }

    @Test
    @TestMetadata("simple.kt")
    public void testSimple() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/simple.kt");
    }

    @Test
    @TestMetadata("topLevelFunctionFromInside.kt")
    public void testTopLevelFunctionFromInside() throws Exception {
        runTest("analysis/analysis-api/testData/components/containingDeclarationProvider/containingDeclarationByReference/topLevelFunctionFromInside.kt");
    }
}
