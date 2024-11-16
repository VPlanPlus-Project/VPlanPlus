package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import es.jvbabi.vplanplus.R

@Composable
fun SettingsCategory(
    overrideStartPadding: Dp? = null,
    title: String, content: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = overrideStartPadding ?: 16.dp, bottom = 4.dp, top = 24.dp)
        )
        content()
    }
}

@Composable
fun SettingsSetting(
    painter: Painter? = null,
    iconTint: Color? = MaterialTheme.colorScheme.onSurface,
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit,
    enabled: Boolean = true,
    clickable: Boolean = true,
    isLoading: Boolean = false,
    titleOverflow: TextOverflow = TextOverflow.Visible,
    subtitleOverflow: TextOverflow = TextOverflow.Visible,
    customContent: @Composable () -> Unit = {},
) {
    Settings(
        title = title,
        subtitle = subtitle,
        type = type,
        checked = checked,
        doAction = doAction,
        enabled = enabled,
        clickable = clickable,
        isLoading = isLoading,
        titleOverflow = titleOverflow,
        subtitleOverflow = subtitleOverflow,
        customContent = customContent,
        imageDrawer = {
            if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = iconTint ?: MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 12.dp, end = 16.dp)
                )
            } else {
                Box(modifier = Modifier.size(56.dp))
            }
        }
    )
}

@Composable
private fun Settings(
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit,
    enabled: Boolean = true,
    clickable: Boolean = true,
    isLoading: Boolean = false,
    titleOverflow: TextOverflow = TextOverflow.Visible,
    subtitleOverflow: TextOverflow = TextOverflow.Visible,
    customContent: @Composable () -> Unit = {},
    imageDrawer: @Composable () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled && clickable) { if (enabled) doAction() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f, false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp)
                        )
                    } else imageDrawer()
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        maxLines = if (titleOverflow != TextOverflow.Visible) 1 else Int.MAX_VALUE,
                        overflow = titleOverflow
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray,
                            maxLines = if (subtitleOverflow != TextOverflow.Visible) 1 else Int.MAX_VALUE,
                            overflow = subtitleOverflow
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                when (type) {
                    SettingsType.TOGGLE -> {
                        Switch(
                            checked = checked ?: false,
                            onCheckedChange = { doAction() },
                            enabled = enabled
                        )
                    }

                    SettingsType.NUMERIC_INPUT -> {}
                    SettingsType.SELECT -> {}
                    SettingsType.DISPLAY -> {}

                    SettingsType.CHECKBOX -> {
                        Checkbox(
                            checked = checked ?: false,
                            onCheckedChange = { doAction() },
                            enabled = enabled
                        )
                    }

                    SettingsType.FUNCTION -> {
                        // TODO
                    }
                }
            }
        }
        customContent()
    }
}

@Composable
fun SettingsSetting(
    icon: ImageVector?,
    iconTint: Color? = null,
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit,
    enabled: Boolean = true,
    clickable: Boolean = true,
    isLoading: Boolean = false,
    titleOverflow: TextOverflow = TextOverflow.Visible,
    subtitleOverflow: TextOverflow = TextOverflow.Visible,
    customContent: @Composable () -> Unit = {},
) {
    Settings(
        title = title,
        subtitle = subtitle,
        type = type,
        checked = checked,
        doAction = doAction,
        enabled = enabled,
        clickable = clickable,
        isLoading = isLoading,
        titleOverflow = titleOverflow,
        subtitleOverflow = subtitleOverflow,
        customContent = customContent,
        imageDrawer = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                        ?: if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 12.dp, end = 16.dp)
                )
            } else {
                Box(modifier = Modifier.size(56.dp))
            }
        }
    )
}

enum class SettingsType {
    TOGGLE,
    CHECKBOX,
    FUNCTION,
    NUMERIC_INPUT,
    SELECT,
    DISPLAY
}

@Composable
@Preview(showBackground = true)
private fun SettingsOptionPreview() {
    SettingsSetting(
        icon = Icons.Default.ManageAccounts,
        title = "Test",
        subtitle = "Test",
        type = SettingsType.NUMERIC_INPUT,
        checked = true,
        doAction = {},
        enabled = true
    )
}

@Composable
@Preview(showBackground = true)
fun SettingsOptionNoIconPreview() {
    SettingsSetting(
        icon = null,
        isLoading = false,
        title = "Test",
        subtitle = "Test",
        type = SettingsType.NUMERIC_INPUT,
        checked = true,
        doAction = {},
        enabled = true
    ) {
        Text(text = "Custom content")
    }
}

@Composable
fun Setting(
    state: SettingsState,
) {
    when (state) {
        is IconSettingsState -> {
            SettingsSetting(
                icon = state.imageVector,
                iconTint = state.tint,
                title = state.title,
                subtitle = state.subtitle,
                type = state.type,
                checked = state.checked,
                doAction = { state.doAction(null) },
                enabled = state.enabled,
                clickable = state.clickable,
                isLoading = state.isLoading,
                titleOverflow = state.titleOverflow,
                subtitleOverflow = state.subtitleOverflow,
                customContent = state.customContent
            )
        }
        is PainterSettingsState -> {
            SettingsSetting(
                painter = state.painter,
                title = state.title,
                subtitle = state.subtitle,
                type = state.type,
                checked = state.checked,
                doAction = { state.doAction(null) },
                enabled = state.enabled,
                clickable = state.clickable,
                isLoading = state.isLoading,
                titleOverflow = state.titleOverflow,
                subtitleOverflow = state.subtitleOverflow,
                customContent = state.customContent
            )
        }
        else -> {
            SettingsSetting(
                title = state.title,
                subtitle = state.subtitle,
                type = state.type,
                checked = state.checked,
                doAction = { state.doAction(null) },
                enabled = state.enabled,
                clickable = state.clickable,
                isLoading = state.isLoading,
                titleOverflow = state.titleOverflow,
                subtitleOverflow = state.subtitleOverflow,
                customContent = state.customContent
            )
        }
    }
}

open class SettingsState(
    open val title: String,
    open val subtitle: String? = null,
    open val type: SettingsType,
    open val checked: Boolean? = null,
    open val doAction: (Any?) -> Unit,
    open val enabled: Boolean = true,
    open val clickable: Boolean = true,
    open val isLoading: Boolean = false,
    open val titleOverflow: TextOverflow = TextOverflow.Visible,
    open val subtitleOverflow: TextOverflow = TextOverflow.Visible,
    open val customContent: @Composable () -> Unit = {},
)

data class IconSettingsState(
    val imageVector: ImageVector? = null,
    val tint: Color? = null,
    override val title: String = "",
    override val subtitle: String? = null,
    override val type: SettingsType = SettingsType.DISPLAY,
    override val checked: Boolean? = null,
    override val doAction: (Any?) -> Unit = {},
    override val enabled: Boolean = true,
    override val clickable: Boolean = true,
    override val isLoading: Boolean = false,
    override val titleOverflow: TextOverflow = TextOverflow.Visible,
    override val subtitleOverflow: TextOverflow = TextOverflow.Visible,
    override val customContent: @Composable () -> Unit = {}
) : SettingsState(
    title = title,
    subtitle = subtitle,
    type = type,
    doAction = doAction,
    enabled = enabled,
    clickable = clickable,
    isLoading = isLoading,
    titleOverflow = titleOverflow,
    subtitleOverflow = subtitleOverflow,
    customContent = customContent
)

data class PainterSettingsState(
    val painter: Painter? = null,
    override val title: String = "",
    override val subtitle: String? = null,
    override val type: SettingsType = SettingsType.DISPLAY,
    override val checked: Boolean? = null,
    override val doAction: (Any?) -> Unit = {},
    override val enabled: Boolean = true,
    override val clickable: Boolean = true,
    override val isLoading: Boolean = false,
    override val titleOverflow: TextOverflow = TextOverflow.Visible,
    override val subtitleOverflow: TextOverflow = TextOverflow.Visible,
    override val customContent: @Composable () -> Unit = {}
) : SettingsState(
    title = title,
    subtitle = subtitle,
    type = type,
    doAction = doAction,
    enabled = enabled,
    clickable = clickable,
    isLoading = isLoading,
    titleOverflow = titleOverflow,
    subtitleOverflow = subtitleOverflow,
    customContent = customContent
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSettings(
    settingsState: SettingsState,
    hour: Int = 0,
    minute: Int = 0,
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(hour, minute)

    if (showTimePicker) {
        TimePickerDialog(
            title = stringResource(id = R.string.settingsHomework_defaultNotificationTimeTitle),
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    settingsState.doAction("${timePickerState.hour}:${timePickerState.minute}")
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
        ) {
            androidx.compose.material3.TimePicker(state = timePickerState, layoutType = TimePickerLayoutType.Vertical)
        }
    }

    Settings(
        title = settingsState.title,
        subtitle = settingsState.subtitle,
        type = SettingsType.FUNCTION,
        checked = settingsState.checked,
        doAction = {
            showTimePicker = true
        },
        enabled = settingsState.enabled,
        clickable = settingsState.clickable,
        isLoading = settingsState.isLoading,
        titleOverflow = settingsState.titleOverflow,
        subtitleOverflow = settingsState.subtitleOverflow,
        customContent = settingsState.customContent,
        imageDrawer = {
            if (settingsState is IconSettingsState) {
                if (settingsState.imageVector != null) {
                    Icon(
                        imageVector = settingsState.imageVector,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(start = 12.dp, end = 16.dp)
                    )
                } else {
                    Box(modifier = Modifier.size(56.dp))
                }
            } else if (settingsState is PainterSettingsState) {
                if (settingsState.painter != null) {
                    Icon(
                        painter = settingsState.painter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(start = 12.dp, end = 16.dp)
                    )
                } else {
                    Box(modifier = Modifier.size(56.dp))
                }
            }
        }
    )
}

@Composable
fun TimePickerDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = containerColor
                ),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}