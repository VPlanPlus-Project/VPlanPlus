package es.jvbabi.vplanplus.feature.homework.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.homework.view.ui.HomeworkViewModelHomework
import es.jvbabi.vplanplus.feature.homework.view.ui.HomeworkViewModelTask
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.util.toBlackAndWhite
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeworkCard(
    modifier: Modifier = Modifier,
    homework: HomeworkViewModelHomework,
    isOwner: Boolean,
    showHidden: Boolean,
    showDisabled: Boolean,
    showDone: Boolean,
    allDone: (Boolean) -> Unit,
    singleDone: (HomeworkViewModelTask, Boolean) -> Unit,
    onAddTask: (String) -> Unit,
    onDeleteRequest: () -> Unit,
    onChangePublicVisibility: () -> Unit,
    onDeleteTaskRequest: (HomeworkViewModelTask) -> Unit,
    onEditTaskRequest: (HomeworkViewModelTask) -> Unit,
    onHomeworkHide: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var isAdding by rememberSaveable {
        mutableStateOf(false)
    }
    var newTask by rememberSaveable {
        mutableStateOf("")
    }
    var isEmpty by rememberSaveable {
        mutableStateOf(false)
    }

    val mainCheckboxState =
        if (homework.tasks.all { it.done }) ToggleableState.On
        else if (homework.tasks.all { !it.done }) ToggleableState.Off
        else ToggleableState.Indeterminate

    val progress = animateFloatAsState(targetValue = homework.tasks.count { it.done }
        .toFloat() / homework.tasks.size, label = "progress").value

    val colorScheme = MaterialTheme.colorScheme
    AnimatedVisibility(
        visible = (showHidden || !homework.isHidden) && (showDisabled || homework.isEnabled) && (showDone || homework.tasks.any { !it.done }),
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        Box {
            var offsetX by remember { mutableFloatStateOf(0f) }
            val displayOffset = animateFloatAsState(targetValue = offsetX, label = "Drag Card")
            var isDragging by remember { mutableStateOf(false) }
            var width by remember { mutableFloatStateOf(0f) }
            Box(
                modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .drawWithContent {
                        width = size.width
                        val error = colorScheme.error
                        val blendFactor = (offsetX / (width / 3))
                        val background =
                            if (blendFactor > 0) Color(
                                ColorUtils.blendARGB(
                                    colorScheme.primaryContainer.toArgb(),
                                    error.toArgb(),
                                    minOf(blendFactor, 1f)
                                )
                            ) else Color(
                                ColorUtils.blendARGB(
                                    colorScheme.primaryContainer.toArgb(),
                                    Color.Gray.toArgb(),
                                    minOf(abs(blendFactor), 1f)
                                )
                            )
                        drawRect(
                            color = background,
                            topLeft = Offset(0f, 0f),
                            size = Size(this.size.width, this.size.height)
                        )
                        drawContent()
                    }) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(62.dp)
                        .padding(start = 20.dp)
                        .alpha(minOf(maxOf(offsetX / (width / 3), 0f), 1f)),
                    imageVector = if (isOwner || homework.createdBy == null) Icons.Outlined.Delete else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(62.dp)
                        .padding(end = 20.dp)
                        .alpha(minOf(maxOf(-offsetX / (width / 3), 0f), 1f)),
                    imageVector = if (mainCheckboxState == ToggleableState.On) Icons.Outlined.CheckBoxOutlineBlank else Icons.Outlined.CheckBox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            if (isDragging) IntOffset(offsetX.roundToInt(), 0)
                            else IntOffset(displayOffset.value.roundToInt(), 0)
                        }
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                offsetX += delta
                            },
                            onDragStopped = {
                                isDragging = false
                                if (offsetX in width / 3..width) {
                                    if (isOwner || homework.createdBy == null) onDeleteRequest()
                                    else onHomeworkHide()
                                }
                                if (offsetX in -width..-width / 3) {
                                    allDone(mainCheckboxState != ToggleableState.On)
                                }
                                offsetX = 0f
                            },
                            onDragStarted = {
                                isDragging = true
                            }
                        )
                        .drawWithContent {
                            drawRect(
                                color = colorScheme.tertiaryContainer,
                                topLeft = Offset(0f, 0f),
                                size = Size(this.size.width * progress, this.size.height)
                            )
                            drawContent()
                        },
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) titleHost@{
                            Row(
                                modifier = Modifier.weight(1f, false),
                                verticalAlignment = Alignment.CenterVertically
                            ) titleContainer@{
                                if (!homework.isLoading) {
                                    TriStateCheckbox(
                                        state = mainCheckboxState,
                                        onClick = { allDone(mainCheckboxState != ToggleableState.On) })
                                } else {
                                    Box(
                                        modifier = Modifier.width(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = stringResource(
                                            id = R.string.homework_homeworkHead,
                                            homework.defaultLesson.subject,
                                            homework.until.format(
                                                DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                                            )
                                        ),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (homework.isPublic) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = DOT,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                        if (homework.isHidden || !homework.isEnabled) {
                                            Icon(
                                                imageVector = Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = DOT,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = createSubtext(homework),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                            Box {
                                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = stringResource(id = R.string.menu)
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(id = R.string.delete)) },
                                        onClick = { menuExpanded = false; onDeleteRequest() },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        },
                                        enabled = (isOwner || homework.createdBy == null) && !homework.isLoading && homework.tasks.all { !it.isLoading }
                                    )
                                    if (isOwner && homework.id > 0) {
                                        DropdownMenuItem(
                                            text = {
                                                if (homework.isPublic) Text(text = stringResource(id = R.string.homework_shared))
                                                else Text(text = stringResource(id = R.string.homework_share))
                                            },
                                            onClick = {
                                                menuExpanded = false; onChangePublicVisibility()
                                            },
                                            enabled = !homework.isLoading && homework.tasks.all { !it.isLoading },
                                            leadingIcon = {
                                                if (homework.isPublic)
                                                    Icon(
                                                        imageVector = Icons.Default.CheckBox,
                                                        contentDescription = null
                                                    )
                                                else
                                                    Icon(
                                                        imageVector = Icons.Default.Share,
                                                        contentDescription = null
                                                    )
                                            }
                                        )
                                    } else if (!isOwner && homework.createdBy != null) {
                                        DropdownMenuItem(
                                            text = {
                                                if (homework.isHidden) Text(text = stringResource(id = R.string.homework_show))
                                                else Text(text = stringResource(id = R.string.homework_hide))
                                            },
                                            onClick = { menuExpanded = false; onHomeworkHide() },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector =
                                                    if (!homework.isHidden) Icons.Default.VisibilityOff
                                                    else Icons.Default.Visibility,
                                                    contentDescription = null
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider()
                        homework.tasks.sortedBy { it.content }.forEach { task ->
                            var isMenuOpened by remember {
                                mutableStateOf(false)
                            }
                            Row(
                                modifier = Modifier.padding(start = 32.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (task.isLoading || homework.isLoading) {
                                    Box(
                                        modifier = Modifier.size(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                } else Checkbox(
                                    checked = task.done,
                                    onCheckedChange = { singleDone(task, it) })
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .combinedClickable(
                                            onLongClick = { isMenuOpened = true },
                                            onClick = { singleDone(task, !task.done) }
                                        )
                                ) {
                                    Text(
                                        text = task.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    DropdownMenu(
                                        expanded = isMenuOpened,
                                        onDismissRequest = { isMenuOpened = false }) {
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(id = R.string.delete)) },
                                            onClick = {
                                                isMenuOpened = false; onDeleteTaskRequest(
                                                task
                                            )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null
                                                )
                                            },
                                            enabled = (isOwner || homework.createdBy == null) && !task.isLoading && !homework.isLoading
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(id = R.string.homework_edit)) },
                                            onClick = {
                                                isMenuOpened = false; onEditTaskRequest(task)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null
                                                )
                                            },
                                            enabled = (isOwner || homework.createdBy == null) && !task.isLoading && !homework.isLoading
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                        }

                        val height = animateFloatAsState(
                            targetValue = if (isAdding) 86f else 48f,
                            label = "addTaskHeightAnimation"
                        ).value.dp
                        if (isOwner || homework.createdBy == null) Column(
                            modifier = Modifier
                                .padding(start = 32.dp)
                                .height(height)
                                .fillMaxWidth(),
                        ) {
                            AnimatedVisibility(
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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    OutlinedTextField(
                                        value = newTask,
                                        onValueChange = { isEmpty = it.isBlank(); newTask = it },
                                        modifier = Modifier.weight(1f),
                                        placeholder = { Text(stringResource(id = R.string.homework_addTask)) },
                                        enabled = !homework.isLoadingNewTask,
                                        supportingText = {
                                            if (isEmpty) {
                                                Text(
                                                    text = stringResource(id = R.string.homework_emptyTask),
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    )
                                    IconButton(
                                        onClick = { isAdding = false },
                                        enabled = !homework.isLoadingNewTask,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(id = R.string.close)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            if (newTask.isBlank()) {
                                                isEmpty = true
                                                return@IconButton
                                            }
                                            onAddTask(newTask)
                                            newTask = ""
                                            isAdding = false
                                        },
                                        enabled = !homework.isLoadingNewTask,
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .then(
                                                if (!homework.isLoadingNewTask && newTask.isNotBlank()) Modifier.background(
                                                    MaterialTheme.colorScheme.primary,
                                                    RoundedCornerShape(50)
                                                )
                                                else Modifier.background(
                                                    MaterialTheme.colorScheme.primary.toBlackAndWhite(),
                                                    RoundedCornerShape(50)
                                                )
                                            )
                                    ) {
                                        if (homework.isLoading) CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        else Icon(
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
        }
    }

}

@Composable
private fun createSubtext(homework: HomeworkViewModelHomework): String {
    val builder = StringBuilder()
    if (homework.createdBy != null) {
        if (homework.isOwner) builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedByYou,
                homework.createdAt.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        ) else builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedBy,
                homework.createdBy.name,
                homework.createdAt.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        )
    } else {
        builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleLocally,
                homework.createdAt.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
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
    val creator = VppIdPreview.generateVppId(`class`)
    val defaultLesson = DefaultLesson(
        teacher = null,
        defaultLessonId = UUID.randomUUID(),
        `class` = `class`,
        subject = "IT",
        vpId = 42
    )
    HomeworkCard(
        modifier = Modifier.padding(start = 60.dp),
        homework = HomeworkViewModelHomework(
            id = 1,
            createdBy = creator,
            createdAt = ZonedDateTime.now(),
            defaultLesson = defaultLesson,
            until = ZonedDateTime.now(),
            tasks = listOf(
                HomeworkViewModelTask(
                    id = 1,
                    content = "Test 1",
                    done = true,
                ),
                HomeworkViewModelTask(
                    id = 1,
                    content = "Test 2",
                    done = true,
                    isLoading = false
                )
            ),
            classes = `class`,
            isPublic = true,
            isOwner = true,
            isLoading = false,
            isLoadingNewTask = true,
            isHidden = true,
            isEnabled = false
        ),
        isOwner = true,
        allDone = {},
        singleDone = { _, _ -> },
        onAddTask = {},
        onDeleteRequest = {},
        onChangePublicVisibility = {},
        onDeleteTaskRequest = {},
        onEditTaskRequest = {},
        onHomeworkHide = {},
        showHidden = true,
        showDisabled = true,
        showDone = true,
    )
}