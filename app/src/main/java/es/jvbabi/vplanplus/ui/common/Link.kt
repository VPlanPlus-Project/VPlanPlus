package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun Link(
    url: String,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val context = LocalContext.current
    Text(
        text = text,
        modifier = Modifier.clickable { openLink(context, url) },
        style = style.copy(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)
    )
}