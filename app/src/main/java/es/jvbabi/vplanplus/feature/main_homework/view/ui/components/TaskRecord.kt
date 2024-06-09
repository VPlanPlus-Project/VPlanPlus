package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.util.blendColor

@Composable
fun TaskRecord(
    task: String,
    isDone: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val blendValue by animateFloatAsState(targetValue = if (isDone) 0f else 1f, label = "blendValue")
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .drawWithContent {
                    drawCircle(
                        color = blendColor(colorScheme.primary, colorScheme.outline, blendValue),
                        radius = 14.dp.toPx(),
                    )
                    drawCircle(
                        color = blendColor(colorScheme.background.copy(alpha = 0f), colorScheme.background, blendValue),
                        radius = 12.dp.toPx(),
                    )
                    drawContent()
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(1-blendValue),
            )
        }
        Text(
            text = task,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskRecordPreview() {
    TaskRecord(
        task = "Task",
        isDone = false,
        isLoading = false,
    )
}

@Preview(showBackground = true)
@Composable
private fun TaskRecordDonePreview() {
    TaskRecord(
        task = "Task",
        isDone = true,
        isLoading = false,
    )
}