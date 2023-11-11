package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
    doAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { doAction() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(modifier = Modifier.weight(1f, false)) {
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
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

                SettingsType.NUMERIC_INPUT -> {
                }

                SettingsType.CHECKBOX -> {
                    // TODO
                }

                SettingsType.FUNCTION -> {
                    // TODO
                }
            }
        }
    }
}

enum class SettingsType {
    TOGGLE,
    CHECKBOX,
    FUNCTION,
    NUMERIC_INPUT
}