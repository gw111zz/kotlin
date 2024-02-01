// TARGET_BACKEND: JVM
package test

class MyMutableEntry<K, V>(
    override val key: K, override var value: V
) : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
        value = newValue
        return value
    }
}

fun entries(map: HashMap<String, Int>) = map.entries
fun keys(map: HashMap<String, Int>) = map.keys
fun value(map: HashMap<String, Int>) = map.values

fun box() {
    val map = HashMap<String, Int>()
    entries(map).add(<!TYPE_MISMATCH, TYPE_MISMATCH!>MyMutableEntry(<!NULL_FOR_NONNULL_TYPE!>null<!>, <!NULL_FOR_NONNULL_TYPE!>null<!>)<!>)
    keys(map).add(<!NULL_FOR_NONNULL_TYPE!>null<!>)
    value(map).add(<!NULL_FOR_NONNULL_TYPE!>null<!>)
}
