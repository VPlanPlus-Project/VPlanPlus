package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    onNo: () -> Unit = {},
    onDismiss: (() -> Unit) = onNo,
    yes: String? = null,
    no: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = message) },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = { onYes() }) {
                    Text(text = yes ?: stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onNo()
                }) {
                    Text(text = no ?: stringResource(id = R.string.no))
                }
            }
        )
    }
}

@Composable
@Preview
private fun YesNoDialogPreview() {
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
    message: AnnotatedString,
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
fun ComposableDialog(
    icon: ImageVector,
    title: String?,
    content: @Composable () -> Unit,
    onOk: (() -> Unit)? = {},
    okEnabled: Boolean = true,
    onDismiss: (() -> Unit)? = {},
    onCancel: (() -> Unit)? = null,
    cancelString: String = stringResource(id = android.R.string.cancel)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AlertDialog(
            title = { if (title != null) Text(text = title) },
            text = { content() },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = { if (onDismiss == null && onOk != null) onOk() else onDismiss?.invoke() },
            confirmButton = {
                if (onOk == null) return@AlertDialog
                TextButton(onClick = { onOk() }, enabled = okEnabled) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                if (onCancel != null) TextButton(onClick = { onCancel() }) {
                    Text(text = cancelString)
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
    postfixText: String? = null,
    selectContent: Boolean = false,
    onOk: (String?) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var text by remember {
            val textRange = if (selectContent) TextRange(0, value?.length ?: 0) else TextRange(value?.length ?: 0)
            mutableStateOf(TextFieldValue(text = value ?: "", selection = textRange))
        }
        val focusRequester = remember { FocusRequester() }
        AlertDialog(
            title = { if (title != null) Text(text = title) },
            text = {
                Column {
                    if (message != null) Text(text = message)
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { if (placeholder != null) Text(text = placeholder) },
                        trailingIcon = {
                            RowVerticalCenter {
                                if (postfixText != null) Text(text = postfixText, color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .focusRequester(focusRequester)
                    )
                }
            },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            onDismissRequest = { onOk(null) },
            confirmButton = {
                TextButton(onClick = { onOk(text.text) }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { onOk(null) }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
        )
        LaunchedEffect(key1 = Unit) { focusRequester.requestFocus() }
    }
}

@Composable
fun <T : Comparable<T>> SelectDialog(
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String?,
    message: String? = null,
    value: T? = null,
    itemToComposable: @Composable ColumnScope.(T) -> Unit = { Text(text = it.toString()) },
    onOk: (T?) -> Unit = {},
    items: List<T>,
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var selected by remember { mutableStateOf(value) }
        AlertDialog(
            icon = {
                if (icon != null) Icon(imageVector = icon, contentDescription = null)
                else if (painter != null) Icon(painter = painter, contentDescription = null)
            },
            title = { if (title != null) Text(text = title) },
            text = {
                Column {
                    if (message != null) Text(text = message)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selected = item },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected == item,
                                    onClick = { selected = item })
                                Column {
                                    itemToComposable(item)
                                }
                            }
                        }
                    }
                }

            },
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onOk(selected) }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
        )
    }
}

@Composable
@Preview
fun SelectDialogPreview() {
    SelectDialog(
        icon = Icons.Default.SystemUpdate,
        title = "Update available",
        message = "There is an update available for the app.",
        items = listOf("1", "2", "3")
    )
}

@Composable
@Preview
private fun InfoDialogPreview() {
    InfoDialog(
        icon = Icons.Default.SystemUpdate,
        title = "Update available",
        message = "There is an update available for the app.",
    )
}

@Composable
@Preview
private fun InputDialogPreview() {
    InputDialog(
        icon = Icons.Default.Repeat,
        title = "How many times do you want to repeat this?",
        message = "Enter a number between 1 and 10",
        placeholder = "Number"
    )
}
