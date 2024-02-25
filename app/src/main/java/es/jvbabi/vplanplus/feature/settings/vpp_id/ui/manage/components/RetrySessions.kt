package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.manage.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun RetrySessions(
    onRetry: () -> Unit
) {
    Row {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .size(16.dp)
        )
        Column {
            Text(stringResource(id = R.string.vppIdSettingsManagement_sessionsError))
            OutlinedButton(
                onClick = onRetry
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RetrySessionsPreview() {
    RetrySessions(onRetry = {})
}