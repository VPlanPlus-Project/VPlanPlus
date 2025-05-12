package es.jvbabi.vplanplus.feature.migration.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun NewAppCard(onClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClicked() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
            Icon(
                imageVector = Icons.Default.Upgrade,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "\uD83D\uDE80 Die neue VPlanPlus-App ist da!",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Wechsle jetzt zur neuen Generation von VPlanPlus. Schneller, einfacher und zuverlässiger. Tippe hier für mehr Informationen.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}