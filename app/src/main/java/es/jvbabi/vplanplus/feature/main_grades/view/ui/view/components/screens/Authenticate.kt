package es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun Authenticate(
    onAuthenticate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Fingerprint,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.authenticate),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = onAuthenticate) {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = stringResource(id = R.string.unlock))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthenticatePreview() {
    Authenticate {}
}