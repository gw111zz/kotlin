// TARGET_BACKEND: NATIVE
// FILECHECK_STAGE: CStubs

value class Foo(val value: Int)
// CHECK-LABEL: define %struct.ObjHeader* @"kfun:Foo#$<bridge-NUN>toString(){}kotlin.String(){}kotlin.String
// CHECK-DEBUG-NOT: call zeroext i1 @IsSubtype
// CHECK-OPT-NOT: call zeroext i1 @IsSubclassFast
// CHECK-LABEL: call %struct.ObjHeader* @"kfun:Foo#toString(){}kotlin.String
// CHECK-LABEL: epilogue:

// CHECK-LABEL: define %struct.ObjHeader* @"kfun:#box(){}kotlin.String"
fun box(): String {
    println(Foo(42))
    return "OK"
}