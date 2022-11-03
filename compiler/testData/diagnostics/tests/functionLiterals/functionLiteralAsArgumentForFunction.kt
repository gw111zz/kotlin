// FIR_DISABLE_LAZY_RESOLVE_CHECKS
// FIR_IDENTICAL
class Log

data class CalculatedVariable(
    val idString: String,
    val presentableName: String,
    val units: String,
    val function: (Log) -> ((TimeIndex) -> Any?)?,
    val converter: (Any) -> Double
) {
    constructor(idString: String, presentableName: String, units: String, function: (Log) -> ((TimeIndex) -> Double?)?)
            : this(idString, presentableName, units, function, { it as Double })
}

object CalculatedVariables {
    val x = CalculatedVariable(
        "A",
        "B",
        "C",
        fun(log: Log): ((TimeIndex) -> Double?)? {
            return { 0.0 }
        }
    )
}

class TimeIndex
