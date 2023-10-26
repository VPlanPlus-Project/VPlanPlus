package es.jvbabi.vplanplus.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SubjectIcon(subject: String, modifier: Modifier, tint: Color = Color.Unspecified) {
    when (subject.lowercase()) {
        "deutsch" -> Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = modifier)
        "informatik" -> Icon(imageVector = Icons.Default.DesktopWindows, contentDescription = null, modifier = modifier, tint = tint)
        else -> Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = modifier)
    }
}