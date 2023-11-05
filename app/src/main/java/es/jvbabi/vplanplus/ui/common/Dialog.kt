package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R

/**
 * A dialog with a title, message, and two buttons: Yes and No.
 * Use on root of composable as it will fill the whole screen.
 * @param icon The icon to show in the dialog.
 * @param title The title of the dialog.
 * @param message The message of the dialog.
 * @param onYes The action to perform when the user presses the Yes button.
 * @param onNo The action to perform when the user presses the No button or taps outside the dialog.
 */
@Composable
fun YesNoDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onYes: () -> Unit = {},
    onNo: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
    ) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = message) },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = { onNo() },
            confirmButton = {
                TextButton(onClick = { onYes() }) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { onNo() }) {
                    Text(text = stringResource(id = R.string.no))
                }
            }
        )
    }
}

@Composable
@Preview
fun YesNoDialogPreview() {
    YesNoDialog(
        icon = Icons.Default.Delete,
        title = "Deleting stuff",
        message = "Are you sure you want to delete this stuff?",
    )
}