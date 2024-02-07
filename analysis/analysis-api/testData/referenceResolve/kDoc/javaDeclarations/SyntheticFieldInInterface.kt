// FILE: main.kt
/**
 * [Storage.<caret>value]
 */
fun usage() {

}

// FILE: Storage.java
@interface Storage {
    String value() default "";
}
