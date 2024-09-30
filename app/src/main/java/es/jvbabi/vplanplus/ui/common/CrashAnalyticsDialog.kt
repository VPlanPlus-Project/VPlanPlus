package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun CrashAnalyticsDialog(
    onAccept: () -> Unit,
    onDeny: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.onboarding_crashAnalyticsDialogTitle)) },
        icon = {
            Icon(
                painter = painterResource(R.drawable.destruction),
                contentDescription = null
            )
        },
        text = {
            Column {
                Text(text = stringResource(id = R.string.onboarding_crashAnalyticsDialogText))
                Spacer8Dp()
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    DialogButton(
                        text = stringResource(id = R.string.onboarding_crashAnalyticsDialogAllow),
                        onClick = onAccept
                    )
                    DialogButton(
                        text = stringResource(id = R.string.onboarding_crashAnalyticsDialogDeny),
                        onClick = onDeny
                    )
                }
            }
        },
        onDismissRequest = {},
        dismissButton = null,
        confirmButton = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CrashAnalyticsDialogPreview() {
    CrashAnalyticsDialog(
        onAccept = {},
        onDeny = {}
    )
}

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.labelMedium
        )
    }
}