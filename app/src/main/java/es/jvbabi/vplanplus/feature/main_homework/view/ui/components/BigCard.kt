package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp

@Composable
fun BigCustomCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
        Spacer4Dp()
        RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun BigCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String? = null
) {
    BigCustomCard(
        modifier = modifier,
        icon = icon,
        title = title
    ) {
        if (subtitle != null) Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
@Preview(showBackground = true)
private fun BigCardPreview() {
    BigCard(
        icon = Icons.Default.AccessTime,
        title = "Created at",
        subtitle = "12:00"
    )
}