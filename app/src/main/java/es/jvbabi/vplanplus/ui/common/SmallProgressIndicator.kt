package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SmallProgressIndicator(
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(16.dp),
        strokeWidth = 2.dp,
        color = tint ?: MaterialTheme.colorScheme.primary
    )
}