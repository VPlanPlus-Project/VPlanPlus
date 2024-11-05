package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    titleBadge: @Composable (() -> Unit)? = null,
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
        titleBadge = titleBadge,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Settings(
    title: String,
    titleBadge: @Composable (() -> Unit)? = null,
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
                    FlowRow(
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            maxLines = if (titleOverflow != TextOverflow.Visible) 1 else Int.MAX_VALUE,
                            overflow = titleOverflow
                        )
                        titleBadge?.invoke()
                    }
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
    titleBadge: @Composable (() -> Unit)? = null,
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
        titleBadge = titleBadge,
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
