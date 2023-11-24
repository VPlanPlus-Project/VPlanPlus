package es.jvbabi.vplanplus.util

object MathTools {

    fun cantor(a: Int, b: Int): Int {
        return (a + b) * (a + b + 1) / 2 + a
    }
}