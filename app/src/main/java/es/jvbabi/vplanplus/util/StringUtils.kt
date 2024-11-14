package es.jvbabi.vplanplus.util

fun String.maxLength(length: Int, overflow: String = "…") = if (length < this.length) this.substring(0, length).removeSuffix(" ") + overflow else this
fun String.removeAllSurrounding(toRemove: Char) = dropWhile { it == toRemove }.dropLastWhile { it == toRemove }
