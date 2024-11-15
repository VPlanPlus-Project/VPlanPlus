package es.jvbabi.vplanplus.util

object MathTools {

    fun cantor(a: Int, b: Int): Int {
        return (a + b) * (a + b + 1) / 2 + a
    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

fun Float.nearest(step: Float): Float {
    if (this % step < step / 2) return this - (this % step)
    return this + (step - (this % step))
}

fun Int.nearest(step: Int): Int {
    if (this % step < step / 2) return this - (this % step)
    return this + (step - (this % step))
}