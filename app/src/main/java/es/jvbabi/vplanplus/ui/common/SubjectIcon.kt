package es.jvbabi.vplanplus.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SubjectIcon(subject: String, modifier: Modifier, tint: Color = Color.Unspecified) {
    when (subject.lowercase()) {
        "deutsch", "de", "deu" -> Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = modifier)
        "informatik", "inf", "info" -> Icon(imageVector = Icons.Default.DesktopWindows, contentDescription = null, modifier = modifier, tint = tint)
        "biologie", "bio" -> Icon(imageVector = Icons.Default.Forest, contentDescription = null, modifier = modifier)
        "mathematik", "mathe", "ma" -> Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = modifier)
        "grw" -> Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, modifier = modifier)
        "englisch", "eng", "en" -> Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = modifier)
        "geografie", "geo" -> Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = modifier)
        else -> Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = modifier)
    }
}