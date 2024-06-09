package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask

@Composable
fun Tasks(
    tasks: List<HomeworkTask>,
    onClick: (HomeworkTask) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) {
        Text(
            text = "Aufgaben", // TODO sr
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        tasks.forEach { task ->
            TaskRecord(task = task.content, isDone = task.done, isLoading = false, onClick = { onClick(task) })
        }
    }
}