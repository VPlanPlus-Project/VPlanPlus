package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter

@Composable
fun Tasks(
    tasks: List<HomeworkTask>,
    onAddTask: (content: String) -> Unit,
    onTaskClicked: (HomeworkTask) -> Unit,
    onUpdateTaskContent: (task: HomeworkTask, content: String) -> Unit,
    onDeleteTask: (HomeworkTask) -> Unit,
    isEditing: Boolean
) {
    var newTasks by rememberSaveable { mutableStateOf(listOf<String>()) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.homework_detailViewTasksTitle),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            tasks.forEach { task ->
                TaskRecord(
                    id = task.id,
                    task = task.content,
                    isDone = task.done,
                    isNewTask = false,
                    isEditing = isEditing,
                    onClick = { onTaskClicked(task) },
                    onUpdateTask = { onUpdateTaskContent(task, it) },
                    onDelete = { onDeleteTask(task) }
                )
            }
            newTasks.forEachIndexed { i, t ->
                TaskRecord(
                    id = i.toLong(),
                    task = t,
                    isDone = false,
                    isEditing = isEditing,
                    isNewTask = true,
                    onClick = {},
                    onUpdateTask = { onAddTask(it); newTasks = newTasks - t },
                    onDelete = { newTasks = newTasks - t },
                )
            }
            AnimatedVisibility(
                visible = isEditing,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = stringResource(id = R.string.addHomework_newTask))
                    RowVerticalCenter(Modifier.fillMaxWidth()) {
                        BasicTextField(
                            value = "",
                            onValueChange = {
                                if (it.isBlank()) return@BasicTextField
                                newTasks = newTasks + it
                            }
                        )
                    }
                }
            }
        }
    }
}