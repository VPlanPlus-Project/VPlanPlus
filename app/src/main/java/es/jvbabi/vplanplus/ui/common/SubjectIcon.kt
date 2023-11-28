package es.jvbabi.vplanplus.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SubjectIcon(subject: String, modifier: Modifier, tint: Color = Color.Unspecified) {
    when (subject.lowercase()) {
        "deutsch", "de", "deu" -> Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = modifier)
        "informatik", "inf", "info" -> Icon(imageVector = Icons.Default.DesktopWindows, contentDescription = null, modifier = modifier, tint = tint)
        "biologie", "bio" -> Icon(imageVector = Icons.Default.Biotech, contentDescription = null, modifier = modifier)
        "mathematik", "mathe", "ma" -> Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = modifier)
        "grw" -> Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = modifier)
        "englisch", "eng", "en" -> Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = modifier)
        "geografie", "geo" -> Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = modifier)
        "chemie", "ch", "cha" -> Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = modifier)
        "physik", "ph", "pha" -> Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = modifier)
        "ethik", "eth" -> Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = modifier)
        "musik", "mu" -> Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = modifier)
        "geschichte", "ge", "ges" -> Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, modifier = modifier)
        "französisch", "fr", "fra" -> Icon(imageVector = Icons.Default.Liquor, contentDescription = null, modifier = modifier)
        "sport", "spo", "sp", "spw", "spm" -> Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = null, modifier = modifier)
        "-" -> Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, modifier = modifier)
        else -> Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = modifier)
    }
}