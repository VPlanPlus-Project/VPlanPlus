package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Composable
fun InfoDialog(
    icon: ImageVector,
    title: String?,
    message: String,
    onOk: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AlertDialog(
            title = { if (title != null) Text(text = title) },
            text = { Text(text = message) },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = { onOk() },
            confirmButton = {
                TextButton(onClick = { onOk() }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
        )
    }
}

@Composable
fun InputDialog(
    icon: ImageVector,
    title: String?,
    message: String? = null,
    placeholder: String? = null,
    value: String? = null,
    onOk: (String?) -> Unit = {},
) {
    var input by remember { mutableStateOf(value?:"") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AlertDialog(
            title = { if (title != null) Text(text = title) },
            text = {
                Column {
                    if (message != null) Text(text = message)
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { if (placeholder != null) Text(text = placeholder) },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = { onOk(null) },
            confirmButton = {
                TextButton(onClick = { onOk(input) }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
        )
    }
}

@Composable
@Preview
fun InfoDialogPreview() {
    InfoDialog(
        icon = Icons.Default.SystemUpdate,
        title = "Update available",
        message = "There is an update available for the app.",
    )
}

@Composable
@Preview
fun InputDialogPreview() {
    InputDialog(
        icon = Icons.Default.Repeat,
        title = "How many times do you want to repeat this?",
        message = "Enter a number between 1 and 10",
        placeholder = "Number"
    )
}