package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.times

@Composable
fun MainToggle(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean,
    title: String,
    onToggle: (enabled: Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onToggle(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = (MaterialTheme.typography.titleLarge.fontSize + MaterialTheme.typography.titleMedium.fontSize)/2),
            color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f, true)
        )
        Switch(
            modifier = Modifier
                .padding(start = 8.dp),
            checked = checked,
            onCheckedChange = { onToggle(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                disabledUncheckedThumbColor = MaterialTheme.colorScheme.outlineVariant,
                disabledUncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            )
        )
    }
}

@Composable
@Preview
fun MainTogglePreview() {
    MainToggle(
        checked = true,
        enabled = true,
        title = "Test " * 50,
        onToggle = {}
    )
}
