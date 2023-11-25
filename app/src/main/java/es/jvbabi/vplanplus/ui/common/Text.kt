package es.jvbabi.vplanplus.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SmallText(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelSmall)
}