package es.jvbabi.vplanplus.feature.main_homework.list.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.list.ui.HomeworkViewModelHomework
import es.jvbabi.vplanplus.feature.main_homework.list.ui.HomeworkViewModelTask
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.homeworkcard.AddHomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.homeworkcard.DraggableHost
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.homeworkcard.HomeworkProgressBar
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.homeworkcard.HomeworkTask
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
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
    onUpdateDueDate: (LocalDate) -> Unit,
    onHomeworkHide: () -> Unit,
    onClick: () -> Unit
) {
    var isAdding by rememberSaveable { mutableStateOf(false) }

    val mainCheckboxState =
        if (homework.tasks.all { it.done }) ToggleableState.On
        else if (homework.tasks.all { !it.done }) ToggleableState.Off
        else ToggleableState.Indeterminate

    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = DateUtils.getDateFromTimestamp(utcTimeMillis / 1000)
            return date.isAfter(LocalDate.now().minusDays(1L))
        }
    })

    var isChangeDateDialogOpen by rememberSaveable { mutableStateOf(false) }
    if (isChangeDateDialogOpen) {
        DatePickerDialog(
            confirmButton = {
                TextButton(onClick = {
                    val date =
                        if (datePickerState.selectedDateMillis == null) null
                        else DateUtils.getDateFromTimestamp(datePickerState.selectedDateMillis!! / 1000)
                    if (date != null) onUpdateDueDate(date)
                    isChangeDateDialogOpen = false
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { isChangeDateDialogOpen = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            },
            onDismissRequest = { isChangeDateDialogOpen = false },
        ) { DatePicker(state = datePickerState) }
    }

    AnimatedVisibility(
        visible = (showHidden || !homework.isHidden) && (showDisabled || homework.isEnabled) && (showDone || homework.tasks.any { !it.done }),
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        Box(modifier.clickable { onClick() }) {
            DraggableHost(
                iconLeft = if (isOwner || homework.createdBy == null) Icons.Outlined.Delete else Icons.Outlined.VisibilityOff,
                iconRight = if (mainCheckboxState == ToggleableState.On) Icons.Outlined.CheckBoxOutlineBlank else Icons.Outlined.CheckBox,
                background = MaterialTheme.colorScheme.background,
                colorLeft = MaterialTheme.colorScheme.error,
                onColorLeft = MaterialTheme.colorScheme.onError,
                colorRight = Color.Gray,
                onColorRight = MaterialTheme.colorScheme.onError,
                onDragToLeft = {
                    if (isOwner || homework.createdBy == null) onDeleteRequest()
                    else onHomeworkHide()
                },
                onDragToRight = { allDone(mainCheckboxState != ToggleableState.On) },
            ) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    RowVerticalCenterSpaceBetweenFill {
                        Title(
                            subject = homework.defaultLesson?.subject,
                            tasks = homework.tasks.size,
                            documents = homework.documentUris.size,
                            homeworkTaskState = mainCheckboxState,
                            onAllDone = allDone,
                            isLoading = homework.isLoading || homework.tasks.any { it.isLoading }
                        )
                        HeaderActions(
                            isLoading = homework.isLoading || homework.tasks.any { it.isLoading },
                            isHomeworkHidden = homework.isHidden,
                            isHomeworkPublic = homework.isPublic,
                            userCanEditHomework = isOwner || (homework.createdBy == null && homework.id < 0),
                            isLocalHomework = homework.createdBy == null && homework.id < 0,
                            onAddTaskClicked = { isAdding = true },
                            onChangeDateClicked = { isChangeDateDialogOpen = true },
                            onDeleteClicked = onDeleteRequest,
                            onChangeVisibilityClicked = onChangePublicVisibility,
                            onChangeHomeworkHideStateClicked = onHomeworkHide
                        )
                    }
                    HomeworkProgressBar(
                        modifier = Modifier.padding(bottom = 4.dp, top = 8.dp),
                        tasks = homework.tasks.size,
                        tasksDone = homework.tasks.count { it.done }
                    )
                    homework.tasks.forEach { task ->
                        HomeworkTask(
                            content = task.content,
                            isLoading = task.isLoading || homework.isLoading,
                            isDone = task.done,
                            userCanEditThisTask = isOwner || homework.createdBy == null,
                            onToggleDone = { singleDone(task, !task.done) },
                            onDeleteClicked = { onDeleteTaskRequest(task) },
                            onEditClicked = { onEditTaskRequest(task) },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    AddHomeworkTask(
                        isVisible = isAdding,
                        isEnabled = !homework.isLoadingNewTask,
                        onCloseClicked = { isAdding = false },
                        onAddTask = onAddTask
                    )
                    HorizontalDivider()
                    RowVerticalCenterSpaceBetweenFill(
                        Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    ) {
                        val resource = homework.until.toLocalDate().getRelativeStringResource(LocalDate.now())
                        val untilText = buildAnnotatedString {
                            var style = MaterialTheme.typography.labelSmall.toSpanStyle()
                            if (homework.toHomework().isOverdue(LocalDate.now())) style = style.copy(color = MaterialTheme.colorScheme.error)
                            withStyle(style) {
                                append(
                                    stringResource(
                                        id = R.string.homework_dueTo, if (resource == null) homework.until.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                        else stringResource(resource)
                                    )
                                )
                                if (homework.toHomework().isOverdue(LocalDate.now())) {
                                    append(" $DOT ")
                                    append(stringResource(id = R.string.homework_overdue))
                                }
                            }
                        }
                        Text(untilText)
                        RowVerticalCenter(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) creatorInformation@{
                            val text = buildAnnotatedString {
                                val style = MaterialTheme.typography.labelSmall.toSpanStyle()
                                withStyle(style) {
                                    append(homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                    append(" $DOT ")
                                    withStyle(style.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                        if (homework.id < 0) append(stringResource(id = R.string.homework_thisDevice))
                                        else if (isOwner) append(stringResource(id = R.string.homework_you))
                                        else if (homework.createdBy == null) append(stringResource(id = R.string.unknownVppId))
                                        else append(homework.createdBy.name)
                                    }
                                }
                            }
                            Text(text)
                            if (isOwner && homework.isPublic) Icon(modifier = Modifier.size(12.dp), imageVector = Icons.Default.Share, contentDescription = null)
                            if (homework.isHidden) Icon(modifier = Modifier.size(12.dp), imageVector = Icons.Default.VisibilityOff, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeworkCardPreview() {
    val school = School.generateRandomSchools(1).first()
    val `class` = ClassesPreview.generateClass(school)
    val creator = VppIdPreview.generateVppId(`class`)
    val profile = ProfilePreview.generateClassProfile(creator)
    val defaultLesson = DefaultLesson(
        teacher = null,
        defaultLessonId = UUID.randomUUID(),
        `class` = `class`,
        subject = "IT",
        vpId = 42
    )
    HomeworkCard(
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
            isEnabled = false,
            profile = profile,
            documentUris = emptyList()
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
        onUpdateDueDate = {},
        showHidden = true,
        showDisabled = true,
        showDone = true,
        onClick = {}
    )
}

@Composable
private fun Title(
    subject: String?,
    tasks: Int,
    documents: Int,
    homeworkTaskState: ToggleableState,
    isLoading: Boolean = false,
    onAllDone: (allDone: Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!isLoading) {
            TriStateCheckbox(
                state = homeworkTaskState,
                onClick = { onAllDone(homeworkTaskState != ToggleableState.On) }
            )
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
        SubjectIcon(subject = subject, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.padding(start = 8.dp)) {
            Text(
                text = subject ?: stringResource(id = R.string.homework_noSubject),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = buildAnnotatedString {
                    append(pluralStringResource(id = R.plurals.homework_taskSize, count = tasks, tasks))
                    if (documents > 0) {
                        append(" $DOT ")
                        append(pluralStringResource(id = R.plurals.homework_documentCount, count = documents, documents))
                    }
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun HeaderActions(
    isLoading: Boolean,
    userCanEditHomework: Boolean,
    isLocalHomework: Boolean,
    isHomeworkPublic: Boolean,
    isHomeworkHidden: Boolean,
    onAddTaskClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onChangeDateClicked: () -> Unit,
    onChangeVisibilityClicked: () -> Unit,
    onChangeHomeworkHideStateClicked: () -> Unit,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userCanEditHomework) IconButton(onClick = onAddTaskClicked) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.homework_addTask))
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
                if (userCanEditHomework) DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.delete)) },
                    onClick = { menuExpanded = false; onDeleteClicked() },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    },
                    enabled = !isLoading
                )
                if (userCanEditHomework) DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.homework_changeDueDate)) },
                    onClick = { menuExpanded = false; onChangeDateClicked() },
                    enabled = !isLoading,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                    }
                )
                if (userCanEditHomework && !isLocalHomework) {
                    DropdownMenuItem(
                        text = {
                            if (isHomeworkPublic) Text(text = stringResource(id = R.string.homework_shared))
                            else Text(text = stringResource(id = R.string.homework_share))
                        },
                        onClick = {
                            menuExpanded = false; onChangeVisibilityClicked()
                        },
                        enabled = !isLoading,
                        leadingIcon = {
                            if (isHomeworkPublic)
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
                } else if (!userCanEditHomework && !isLocalHomework) {
                    DropdownMenuItem(
                        text = {
                            if (isHomeworkHidden) Text(text = stringResource(id = R.string.homework_show))
                            else Text(text = stringResource(id = R.string.homework_hide))
                        },
                        onClick = { menuExpanded = false; onChangeHomeworkHideStateClicked() },
                        leadingIcon = {
                            Icon(
                                imageVector =
                                if (!isHomeworkHidden) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        },
                    )
                }
            }
        }
    }
}