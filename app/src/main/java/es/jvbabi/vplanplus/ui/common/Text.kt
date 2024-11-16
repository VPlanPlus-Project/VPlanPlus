package es.jvbabi.vplanplus.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import es.jvbabi.vplanplus.R
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

@Composable
fun String?.orUnknown(capitalize: Boolean = false): String {
    return this ?: stringResource(R.string.unknown).let { unknownString ->
        if (capitalize) unknownString.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } else unknownString
    }
}

operator fun TextUnit.plus(other: TextUnit): TextUnit {
    return TextUnit(value + other.value, type)
}