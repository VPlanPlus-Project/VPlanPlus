package es.jvbabi.vplanplus.util

import kotlin.math.floor
import kotlin.random.Random

fun <E> Collection<E>.randomSubList(size: Int = 5, forceSize: Boolean = true): MutableList<E> {
    val result = mutableListOf<E>()
    var rSize = floor(if (forceSize) size.toDouble() else size * Random.nextDouble(0.0, 1.0))
    if (rSize == 0.0) rSize = 1.0
    var i = 0
    while (i < rSize) {
        val r = this.random()
        if (result.any { r == it }) continue
        result.add(r)
        i += 1
    }
    return result
}
