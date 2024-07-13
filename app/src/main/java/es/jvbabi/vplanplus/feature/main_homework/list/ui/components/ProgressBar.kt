package es.jvbabi.vplanplus.feature.main_homework.list.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeworkProgressBar(
    modifier: Modifier = Modifier,
    tasks: Int,
    tasksDone: Int
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            Modifier
                .fillMaxWidth(animateFloatAsState(targetValue = if (tasks == 0) 1f else tasksDone.toFloat () / tasks, label = "Progress").value)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}

@Composable
@Preview
private fun HomeworkProgressBarPreview() {
    HomeworkProgressBar(
        tasks = 5,
        tasksDone = 2
    )
}