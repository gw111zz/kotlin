/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.scripting.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("plugins/scripting/scripting-tests/testData/diagnostics/testScripts")
@TestDataPath("$PROJECT_ROOT")
public class ScriptWithCustomDefDiagnosticsTestBaseGenerated extends AbstractScriptWithCustomDefDiagnosticsTestBase {
  @Test
  public void testAllFilesPresentInTestScripts() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("plugins/scripting/scripting-tests/testData/diagnostics/testScripts"), Pattern.compile("^(.+)\\.kts$"), null, true);
  }

  @Test
  @TestMetadata("providedProperties.test.kts")
  public void testProvidedProperties_test() {
    runTest("plugins/scripting/scripting-tests/testData/diagnostics/testScripts/providedProperties.test.kts");
  }

  @Test
  @TestMetadata("simple.test.kts")
  public void testSimple_test() {
    runTest("plugins/scripting/scripting-tests/testData/diagnostics/testScripts/simple.test.kts");
  }
}
