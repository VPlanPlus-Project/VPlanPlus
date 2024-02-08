package es.jvbabi.vplanplus.ui.common

import java.util.Locale

const val DOT = "•"

fun Int.toLocalizedString(): String {
    return when (Locale.getDefault().language) {
        "de" -> "$this."
        else -> when (this.toString().last()) {
            '1' -> "${this}st"
            '2' -> "${this}nd"
            '3' -> "${this}rd"
            else -> "${this}th"
        }
    }
}