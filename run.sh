export JDK_17_0=`/usr/libexec/java_home -v 17`
export JDK_21_0=`/usr/libexec/java_home -v 21`

./gradlew :compiler:tests-common-new:test --tests "org.jetbrains.kotlin.test.runners.ir.ClassicJvmIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.ir.FirPsiJvmIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.ir.FirPsiJvmIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true


./gradlew :js:js.tests:test --tests "org.jetbrains.kotlin.js.test.ir.ClassicJsIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :js:js.tests:test --tests "org.jetbrains.kotlin.js.test.fir.FirPsiJsIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :js:js.tests:test --tests "org.jetbrains.kotlin.js.test.fir.FirPsiJsIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true

./gradlew :native:native.tests:test --tests "org.jetbrains.kotlin.konan.test.irtext.ClassicNativeIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :native:native.tests:test --tests "org.jetbrains.kotlin.konan.test.irtext.FirLightTreeNativeIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :native:native.tests:test --tests "org.jetbrains.kotlin.konan.test.irtext.FirLightTreeNativeIrTextTestGenerated" --continue -Pkotlin.test.update.test.data=true

./gradlew :compiler:tests-common-new:test --tests "org.jetbrains.kotlin.test.runners.codegen.IrBlackBoxCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirLightTreeBlackBoxCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirLightTreeBlackBoxCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true

./gradlew :plugins:fir-plugin-prototype:test --tests "org.jetbrains.kotlin.fir.plugin.runners.FirLoadK2CompiledWithPluginJvmKotlinTestGenerated" --continue -Pkotlin.test.update.test.data=true


./gradlew :compiler:tests-common-new:test --tests "org.jetbrains.kotlin.test.runners.codegen.BlackBoxModernJdkCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirLightTreeBlackBoxModernJdkCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirLightTreeBlackBoxModernJdkCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true

./gradlew :compiler:test --tests "org.jetbrains.kotlin.codegen.ScriptCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirScriptCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true
./gradlew :compiler:fir:fir2ir:test --tests "org.jetbrains.kotlin.test.runners.codegen.FirScriptCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true

./gradlew :plugins:fir-plugin-prototype:test --tests "org.jetbrains.kotlin.fir.plugin.runners.FirLightTreePluginBlackBoxCodegenTestGenerated" --continue -Pkotlin.test.update.test.data=true