package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.noRippleClickable
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.view.ui.EditTask
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter

@Composable
fun Tasks(
    tasks: List<HomeworkTask>,
    editTasks: List<EditTask>,
    newTasks: List<EditTask>,
    onTaskClicked: (HomeworkTask) -> Unit,
    onAddTask: (newTaskId: Long, content: String) -> Unit,
    onUpdateExistingTask: (existingTaskId: Long, content: String) -> Unit,
    onUpdateNewTask: (newTaskId: Long, content: String) -> Unit,
    onDeleteExistingTask: (HomeworkTask) -> Unit,
    onDeleteNewTask: (newTaskId: Long) -> Unit,
    isEditing: Boolean
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.homework_detailViewTasksTitle),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            editTasks.forEach { editTask ->
                val task = tasks.firstOrNull { it.id == editTask.id }
                TaskRecord(
                    id = editTask.id,
                    task = editTask.content,
                    isDone = task?.done ?: false,
                    isNewTask = false,
                    isEditing = isEditing,
                    onClick = { if (task != null) onTaskClicked(task) },
                    onUpdateTask = { if (task != null) onUpdateExistingTask(task.id, it) },
                    onDelete = { if (task != null) onDeleteExistingTask(task) }
                )
            }
            newTasks.forEach { newTask ->
                TaskRecord(
                    id = newTask.id,
                    task = newTask.content,
                    isDone = false,
                    isEditing = isEditing,
                    isNewTask = true,
                    onClick = {},
                    onUpdateTask = { onUpdateNewTask(newTask.id, it) },
                    onDelete = { onDeleteNewTask(newTask.id) },
                )
            }
            AnimatedVisibility(
                visible = isEditing,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                val focusRequester = remember { FocusRequester() }
                RowVerticalCenter(Modifier.noRippleClickable { focusRequester.requestFocus() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = stringResource(id = R.string.addHomework_newTask))
                        RowVerticalCenter(Modifier.fillMaxWidth()) {
                            BasicTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                value = "",
                                onValueChange = {
                                    if (it.isBlank()) return@BasicTextField
                                    onAddTask((newTasks.maxOfOrNull { newTask -> newTask.id } ?: 0) + 1, it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksPreview() {
    Tasks(
        tasks = listOf(
            HomeworkTask(1, "Task 1", false),
            HomeworkTask(2, "Task 2", true),
            HomeworkTask(3, "Task 3", false),
        ),
        onAddTask = { _, _ -> },
        onTaskClicked = {},
        onUpdateExistingTask = { _, _ -> },
        onUpdateNewTask = { _, _ -> },
        onDeleteExistingTask = {},
        onDeleteNewTask = {},
        newTasks = emptyList(),
        editTasks = emptyList(),
        isEditing = false
    )
}