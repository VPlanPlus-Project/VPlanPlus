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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BetaTestAdvert(onClicked: () -> Unit, onCloseClicked: () -> Unit, canClose: Boolean = false) {
    Column (
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClicked() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        text = "\uD83D\uDE80 Dürfen wir vorstellen?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Wir arbeiten an einem Nachfolger für die VPlanPlus-App - schneller, einfacher und funktionsreicher. Tritt jetzt dem Beta-Test bei und erlebe die neue App als Erster!",
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
        if (canClose) {
            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = onCloseClicked,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) { Text("Nicht mehr anzeigen") }
        }
    }
}