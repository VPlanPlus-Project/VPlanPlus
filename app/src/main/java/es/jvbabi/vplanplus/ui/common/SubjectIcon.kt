package es.jvbabi.vplanplus.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HistoryEdu
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
import androidx.compose.ui.res.painterResource
import es.jvbabi.vplanplus.R

@Composable
fun SubjectIcon(subject: String, modifier: Modifier, tint: Color = Color.Unspecified) {
    when (subject.lowercase()) {
        "deutsch", "de", "deu" -> Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = modifier, tint = tint)
        "informatik", "inf", "info" -> Icon(imageVector = Icons.Default.DesktopWindows, contentDescription = null, modifier = modifier, tint = tint)
        "biologie", "bio" -> Icon(imageVector = Icons.Default.Biotech, contentDescription = null, modifier = modifier, tint = tint)
        "mathematik", "mathe", "ma" -> Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = modifier, tint = tint)
        "grw" -> Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = modifier, tint = tint)
        "englisch", "eng", "en" -> Icon(painter = painterResource(id = R.drawable.subject_english), contentDescription = null, modifier = modifier, tint = tint)
        "geografie", "geo" -> Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = modifier, tint = tint)
        "chemie", "ch", "cha" -> Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = modifier, tint = tint)
        "physik", "ph", "pha" -> Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = modifier, tint = tint)
        "ethik", "eth" -> Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = modifier, tint = tint)
        "musik", "mu" -> Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = modifier, tint = tint)
        "geschichte", "ge", "ges" -> Icon(imageVector = Icons.Default.HistoryEdu, contentDescription = null, modifier = modifier, tint = tint)
        "französisch", "fr", "fra" -> Icon(painter = painterResource(id = R.drawable.subject_french), contentDescription = null, modifier = modifier, tint = tint)
        "sport", "spo", "sp", "spw", "spm" -> Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = null, modifier = modifier, tint = tint)
        "latein", "la", "lat" -> Icon(painterResource(id = R.drawable.subject_latin), contentDescription = null, modifier = modifier, tint = tint)
        "kunst", "ku" -> Icon(imageVector = Icons.Default.Brush, contentDescription = null, modifier = modifier, tint = tint)
        "-" -> Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, modifier = modifier, tint = tint)
        else -> Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = modifier, tint = tint)
    }
}