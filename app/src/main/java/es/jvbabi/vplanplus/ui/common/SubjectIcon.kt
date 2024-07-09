package es.jvbabi.vplanplus.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson

@Composable
fun SubjectIcon(subject: String?, modifier: Modifier, tint: Color = Color.Unspecified) {
    Icon(imageVector = subject.getSubjectIcon(), contentDescription = null, modifier = modifier, tint = tint)
}

@Composable
fun DefaultLesson?.getSubjectIcon() = this?.subject?.lowercase().getSubjectIcon()

@Composable
fun String?.getSubjectIcon(): ImageVector {
    return when(this?.lowercase()) {
        "deutsch", "de", "deu" -> Icons.Default.Book
        "informatik", "inf", "info" -> Icons.Default.Code
        "biologie", "bio" -> Icons.Default.Biotech
        "mathematik", "mathe", "ma" -> Icons.Default.Calculate
        "grw" -> Icons.Default.Payment
        "englisch", "eng", "en" -> ImageVector.vectorResource(id = R.drawable.subject_english)
        "geografie", "geo" -> Icons.Default.Place
        "chemie", "ch", "cha" -> Icons.Default.Science
        "physik", "ph", "pha" -> Icons.Default.Bolt
        "ethik", "eth" -> Icons.Default.Psychology
        "musik", "mu" -> Icons.Default.MusicNote
        "geschichte", "ge", "ges" -> Icons.Default.HistoryEdu
        "französisch", "fr", "fra" -> ImageVector.vectorResource(id = R.drawable.subject_french)
        "sport", "spo", "sp", "spw", "spm" -> Icons.Default.SportsSoccer
        "latein", "la", "lat" -> ImageVector.vectorResource(id = R.drawable.subject_latin)
        "kunst", "ku" -> Icons.Default.Brush
        "astronomie", "ast" -> Icons.Default.SatelliteAlt
        "-" -> Icons.Default.EventBusy
        else -> Icons.Default.School
    }
}