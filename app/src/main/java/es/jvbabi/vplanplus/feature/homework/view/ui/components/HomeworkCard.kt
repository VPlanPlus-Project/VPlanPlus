package es.jvbabi.vplanplus.feature.homework.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun HomeworkCard(
    currentUser: VppId?,
    homework: Homework,
    allDone: (Boolean) -> Unit,
    singleDone: (HomeworkTask, Boolean) -> Unit,
    onAddTask: (String) -> Unit
) {
    var isAdding by rememberSaveable {
        mutableStateOf(false)
    }
    var newTask by rememberSaveable {
        mutableStateOf("")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val state =
                    if (homework.tasks.all { it.done }) ToggleableState.On
                    else if (homework.tasks.all { !it.done }) ToggleableState.Off
                    else ToggleableState.Indeterminate
                TriStateCheckbox(state = state, onClick = { allDone(state != ToggleableState.On ) })
                Column {
                    Text(
                        text = stringResource(
                            id = R.string.homework_homeworkHead,
                            homework.defaultLesson.subject,
                            homework.until.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                            )
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = createSubtext(homework, currentUser), style = MaterialTheme.typography.labelMedium)
                }
            }

            HorizontalDivider()
            homework.tasks.sortedBy { it.done.toString() + it.content }.forEach { task ->
                Row(
                    modifier = Modifier.padding(start = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = task.done, onCheckedChange = { singleDone(task, it) })
                    Text(
                        text = task.content,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
            }

            val height = animateFloatAsState(
                targetValue = if (isAdding) 56f else 48f,
                label = "addTaskHeightAnimation"
            ).value.dp
            Column(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .height(height)
                    .fillMaxWidth(),
            ) {AnimatedVisibility(
                    visible = !isAdding,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(250))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isAdding = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.homework_addTask),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isAdding,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(250))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTask,
                            onValueChange = { newTask = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(stringResource(id = R.string.homework_addTask)) }
                        )
                        IconButton(onClick = { isAdding = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.close)
                            )
                        }
                        IconButton(
                            onClick = {
                                onAddTask(newTask)
                                newTask = ""
                                isAdding = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun createSubtext(homework: Homework, currentUser: VppId?): String {
    val builder = StringBuilder()
    if (homework.createdBy != null) {
        if (currentUser == homework.createdBy) builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedByYou,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        ) else builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedBy,
                homework.createdBy.name,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        )
    } else {
        builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleLocally,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        )
    }
    return builder.toString()
}

@Preview(showBackground = true)
@Composable
private fun HomeworkCardPreview() {
    val school = School.generateRandomSchools(1).first()
    val `class` = ClassesPreview.generateClass(school)
    val currentVppId = null
    val creator = VppIdPreview.generateVppId(`class`)
    val defaultLesson = DefaultLesson(
        teacher = null,
        defaultLessonId = UUID.randomUUID(),
        `class` = `class`,
        subject = "IT",
        vpId = 42
    )
    HomeworkCard(
        currentUser = currentVppId,
        homework = Homework(
            id = 1,
            createdBy = creator,
            createdAt = LocalDateTime.now(),
            defaultLesson = defaultLesson,
            until = LocalDate.now(),
            tasks = listOf(
                HomeworkTask(
                    id = 1,
                    content = "Test 1",
                    done = false,
                    individualId = null
                ),
                HomeworkTask(
                    id = 1,
                    content = "Test 2",
                    done = true,
                    individualId = null
                )
            ),
            classes = `class`
        ),
        allDone = {},
        singleDone = { _, _ -> },
        onAddTask = {}
    )
}