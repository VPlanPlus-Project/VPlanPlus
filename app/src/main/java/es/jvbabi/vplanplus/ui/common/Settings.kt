package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsCategory(title: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 24.dp)
        )
        content()
    }
}

@Composable
fun SettingsSetting(
    icon: ImageVector?,
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit,
    enabled: Boolean = true,
    clickable: Boolean = true,
    customContent: @Composable () -> Unit = {}
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
            Row(modifier = Modifier.weight(1f, false), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            modifier = Modifier.padding(start = 12.dp, end = 16.dp)
                        )
                    } else {
                        Box(modifier = Modifier.width(52.dp))
                    }
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
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
                        Switch(checked = checked ?: false, onCheckedChange = { doAction() })
                    }

                    SettingsType.NUMERIC_INPUT -> {}
                    SettingsType.SELECT -> {}
                    SettingsType.DISPLAY -> {}

                    SettingsType.CHECKBOX -> {
                        // TODO
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
fun SettingsOptionPreview() {
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