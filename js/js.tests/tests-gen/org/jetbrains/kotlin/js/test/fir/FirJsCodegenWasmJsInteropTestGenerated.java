/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.fir;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateJsTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/codegen/boxWasmJsInterop")
@TestDataPath("$PROJECT_ROOT")
public class FirJsCodegenWasmJsInteropTestGenerated extends AbstractFirJsCodegenWasmJsInteropTest {
  @Test
  public void testAllFilesPresentInBoxWasmJsInterop() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/testData/codegen/boxWasmJsInterop"), Pattern.compile("^(.+)\\.kt$"), null, TargetBackend.JS_IR, true);
  }

  @Test
  @TestMetadata("defaultValues.kt")
  public void testDefaultValues() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/defaultValues.kt");
  }

  @Test
  @TestMetadata("externalTypeOperators.kt")
  public void testExternalTypeOperators() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/externalTypeOperators.kt");
  }

  @Test
  @TestMetadata("externals.kt")
  public void testExternals() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/externals.kt");
  }

  @Test
  @TestMetadata("functionTypes.kt")
  public void testFunctionTypes() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/functionTypes.kt");
  }

  @Test
  @TestMetadata("jsCode.kt")
  public void testJsCode() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/jsCode.kt");
  }

  @Test
  @TestMetadata("jsModule.kt")
  public void testJsModule() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/jsModule.kt");
  }

  @Test
  @TestMetadata("jsModuleWithQualifier.kt")
  public void testJsModuleWithQualifier() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/jsModuleWithQualifier.kt");
  }

  @Test
  @TestMetadata("jsQualifier.kt")
  public void testJsQualifier() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/jsQualifier.kt");
  }

  @Test
  @TestMetadata("jsToKotlinAdapters.kt")
  public void testJsToKotlinAdapters() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/jsToKotlinAdapters.kt");
  }

  @Test
  @TestMetadata("kotlinToJsAdapters.kt")
  public void testKotlinToJsAdapters() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/kotlinToJsAdapters.kt");
  }

  @Test
  @TestMetadata("longStrings.kt")
  public void testLongStrings() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/longStrings.kt");
  }

  @Test
  @TestMetadata("nameClash.kt")
  public void testNameClash() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/nameClash.kt");
  }

  @Test
  @TestMetadata("types.kt")
  public void testTypes() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/types.kt");
  }

  @Test
  @TestMetadata("vararg.kt")
  public void testVararg() {
    runTest("compiler/testData/codegen/boxWasmJsInterop/vararg.kt");
  }
}
