package es.jvbabi.vplanplus.ui.screens.id_link.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun LinkNoProfiles(modifier: Modifier = Modifier, className: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.onErrorContainer,
            imageVector = Icons.Default.Warning,
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.vppIdLink_noSuitableProfiles, className),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
@Preview
private fun LinkNoProfilesPreview() {
    LinkNoProfiles(className = "7a")
}