package es.jvbabi.vplanplus.ui.screens.home.components.home.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LastSyncText(
    lastSync: LocalDateTime?,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (lastSync == null) stringResource(id = R.string.home_lastSyncNever) else stringResource(
            id = R.string.home_lastSync,
            lastSync.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        ), style = MaterialTheme.typography.labelSmall,
        modifier = modifier
    )
}

@Preview
@Composable
private fun NeverSyncedPreview() {
    LastSyncText(null)
}

@Preview
@Composable
private fun SyncedPreview() {
    LastSyncText(LocalDateTime.now())
}