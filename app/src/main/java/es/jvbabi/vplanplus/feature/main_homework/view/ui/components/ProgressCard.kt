package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import kotlin.math.roundToInt

@Composable
fun ProgressCard(tasks: Int, done: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
        val progress by animateFloatAsState(targetValue = done.toFloat() / tasks.toFloat().coerceAtLeast(1f), label = "progress")
        Text(
            text = "Fortschritt",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = "${(progress*100).roundToInt()}%",
            style = MaterialTheme.typography.displaySmall,
        )
        Box(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .height(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        RowVerticalCenterSpaceBetweenFill(Modifier.padding(bottom = 4.dp)) {
            val style = MaterialTheme.typography.labelMedium
            RowVerticalCenter {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier
                        .size(style.lineHeight.value.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$done erledigt", // TODO sr
                    style = style.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            RowVerticalCenter {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(style.lineHeight.value.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${tasks-done} offen", // TODO sr
                    style = style,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ProgressCardPreview() {
    ProgressCard(tasks = 5, done = 2)
}

@Composable
@Preview(showBackground = true)
private fun ProgressCardPreviewEmpty() {
    ProgressCard(tasks = 0, done = 0)
}

@Composable
@Preview(showBackground = true)
private fun ProgressCardPreviewAllDone() {
    ProgressCard(tasks = 5, done = 5)
}