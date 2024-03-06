package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.homework.view.ui.components.HomeworkCard
import es.jvbabi.vplanplus.feature.homework.view.ui.components.NoHomework
import es.jvbabi.vplanplus.feature.homework.view.ui.components.WrongProfile
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.ChangeVisibilityDialog
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.DeleteHomeworkDialog
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.DeleteHomeworkTaskDialog
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeworkScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val clipboardManager = LocalClipboardManager.current

    HomeworkScreenContent(
        onBack = { navHostController.popBackStack() },
        onAddHomework = { navHostController.navigate(Screen.AddHomeworkScreen.route) },
        onMarkAllDone = viewModel::markAllDone,
        onMarkSingleDone = viewModel::markSingleDone,
        onAddTask = viewModel::onAddTask,
        onHomeworkDeleteRequest = viewModel::onHomeworkDeleteRequest,
        onHomeworkDeleteRequestConfirm = viewModel::onConfirmHomeworkDeleteRequest,
        onHomeworkChangeVisibilityRequest = viewModel::onHomeworkChangeVisibilityRequest,
        onHomeworkChangeVisibilityRequestConfirm = viewModel::onConfirmHomeworkChangeVisibilityRequest,
        onHomeworkTaskDeleteRequest = viewModel::onHomeworkTaskDeleteRequest,
        onHomeworkTaskDeleteRequestConfirm = viewModel::onHomeworkTaskDeleteRequestConfirm,
        onHomeworkTaskEditRequest = viewModel::onHomeworkTaskEditRequest,
        onHomeworkTaskEditRequestConfirm = viewModel::onHomeworkTaskEditRequestConfirm,
        onResetError = viewModel::onResetError,
        onCopyToClipboard = { clipboardManager.setText(buildAnnotatedString { append(it) }) },
        onHomeworkHide = viewModel::onHomeworkHideToggle,
        onToggleShowHidden = viewModel::onToggleShowHidden,
        onToggleShowDisabled = viewModel::onToggleShowDisabled,
        onToggleShowDone = viewModel::onToggleShowDone,
        refresh = viewModel::refresh,
        state = state,
        navBar = navBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkScreenContent(
    onBack: () -> Unit = {},
    onAddHomework: () -> Unit = {},
    onMarkAllDone: (homework: HomeworkViewModelHomework, done: Boolean) -> Unit = { _, _ -> },
    onMarkSingleDone: (homeworkTask: HomeworkViewModelTask, done: Boolean) -> Unit = { _, _ -> },
    onAddTask: (homework: HomeworkViewModelHomework, task: String) -> Unit = { _, _ -> },
    onHomeworkDeleteRequest: (homework: HomeworkViewModelHomework?) -> Unit = {},
    onHomeworkDeleteRequestConfirm: () -> Unit = {},
    onHomeworkChangeVisibilityRequest: (homework: HomeworkViewModelHomework?) -> Unit = {},
    onHomeworkChangeVisibilityRequestConfirm: () -> Unit = {},
    onHomeworkTaskDeleteRequest: (homeworkTask: HomeworkViewModelTask?) -> Unit = {},
    onHomeworkTaskDeleteRequestConfirm: () -> Unit = {},
    onHomeworkTaskEditRequest: (homeworkTask: HomeworkViewModelTask?) -> Unit = {},
    onHomeworkTaskEditRequestConfirm: (newContent: String?) -> Unit = {},
    onHomeworkHide: (homework: HomeworkViewModelHomework) -> Unit = {},
    onResetError: () -> Unit = {},
    onToggleShowHidden: () -> Unit = {},
    onToggleShowDisabled: () -> Unit = {},
    onToggleShowDone: () -> Unit = {},
    onCopyToClipboard: (String) -> Unit = {},
    refresh: () -> Unit = {},
    state: HomeworkState,
    navBar: @Composable () -> Unit = {},
) {
    if (state.homeworkDeletionRequest != null) {
        DeleteHomeworkDialog(
            homework = state.homeworkDeletionRequest,
            onConfirm = { onHomeworkDeleteRequestConfirm() },
            onDismiss = { onHomeworkDeleteRequest(null) }
        )
    }
    if (state.homeworkChangeVisibilityRequest != null) {
        ChangeVisibilityDialog(
            homework = state.homeworkChangeVisibilityRequest,
            onConfirm = { onHomeworkChangeVisibilityRequestConfirm() },
            onDismiss = { onHomeworkChangeVisibilityRequest(null) }
        )
    }
    if (state.homeworkTaskDeletionRequest != null) {
        DeleteHomeworkTaskDialog(
            task = state.homeworkTaskDeletionRequest,
            onConfirm = { onHomeworkTaskDeleteRequestConfirm() },
            onDismiss = { onHomeworkTaskDeleteRequest(null) }
        )
    }
    if (state.editHomeworkTask != null) {
        InputDialog(
            icon = Icons.Default.Edit,
            placeholder = stringResource(id = R.string.homework_editTaskPlaceholder),
            value = state.editHomeworkTask.content,
            title = stringResource(id = R.string.homework_editTaskTitle),
            message = stringResource(id = R.string.homework_editTaskText),
            onOk = { onHomeworkTaskEditRequestConfirm(it) },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.homework_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                }
            )
        },
        bottomBar = navBar,
        floatingActionButton = {
            if (!state.wrongProfile) FloatingActionButton(onClick = onAddHomework) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.wrongProfile) {
                WrongProfile()
                return@Column
            }

            val pullRefreshState = rememberPullToRefreshState()
            LaunchedEffect(key1 = state.isUpdating) {
                if (state.isUpdating) pullRefreshState.startRefresh()
                else pullRefreshState.endRefresh()
            }
            if (pullRefreshState.isRefreshing) {
                LaunchedEffect(key1 = Unit, block = {
                    refresh()
                })
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) pullToRefresh@{
                Column(
                    modifier = Modifier.fillMaxSize()
                ) content@{
                    LazyRow {
                        item {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        item {
                            FilterChip(
                                selected = !state.showDisabled,
                                onClick = onToggleShowDisabled,
                                label = { Text(text = stringResource(id = R.string.homework_filterShowOnlyMy)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.showDone,
                                onClick = onToggleShowDone,
                                label = { Text(text = stringResource(id = R.string.homework_filterShowDone)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.showHidden,
                                onClick = onToggleShowHidden,
                                label = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.homework_filterShowHidden,
                                            state.homework.count { it.isHidden })
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = state.errorVisible,
                        enter = expandVertically(tween(250)),
                        exit = shrinkVertically(tween(250))
                    ) {
                        InfoCard(
                            modifier = Modifier.padding(16.dp),
                            imageVector = Icons.Default.Error,
                            title = stringResource(id = R.string.something_went_wrong),
                            text = when (state.errorResponse!!.first) {
                                ErrorOnUpdate.DELETE_TASK -> stringResource(id = R.string.homework_errorDeleteTask)
                                ErrorOnUpdate.CHANGE_HOMEWORK_VISIBILITY -> stringResource(id = R.string.homework_errorChangeVisibility)
                                ErrorOnUpdate.DELETE_HOMEWORK -> stringResource(id = R.string.homework_errorDeleteHomework)
                                ErrorOnUpdate.ADD_TASK -> stringResource(id = R.string.homework_errorAddTask)
                                ErrorOnUpdate.EDIT_TASK -> stringResource(id = R.string.homework_errorEditTask)
                                ErrorOnUpdate.CHANGE_TASK_STATE -> {
                                    if (state.errorResponse.second == true) {
                                        stringResource(id = R.string.homework_errorMarkTaskDone)
                                    } else {
                                        stringResource(id = R.string.homework_errorMarkTaskUndone)
                                    }
                                }

                                ErrorOnUpdate.CHANGE_HOMEWORK_STATE -> {
                                    if (state.errorResponse.second == true) {
                                        stringResource(id = R.string.homework_errorMarkHomeworkDone)
                                    } else {
                                        stringResource(id = R.string.homework_errorMarkHomeworkUndone)
                                    }
                                }
                            } + " " + stringResource(id = R.string.homework_errorInternetTryAgain),
                            buttonText1 = stringResource(id = R.string.close),
                            buttonAction1 = onResetError,
                            buttonText2 = when (state.errorResponse.first) {
                                ErrorOnUpdate.ADD_TASK, ErrorOnUpdate.EDIT_TASK -> stringResource(id = R.string.copy)
                                else -> null
                            },
                            buttonAction2 = when (state.errorResponse.first) {
                                ErrorOnUpdate.ADD_TASK, ErrorOnUpdate.EDIT_TASK -> {
                                    {
                                        onCopyToClipboard(
                                            state.errorResponse.second.toString().trim()
                                        )
                                    }
                                }

                                else -> {
                                    {}
                                }
                            }
                        )
                    }
                    LazyColumn(Modifier.fillMaxSize()) {
                        val list =
                            state.homework.sortedBy { it.until }.groupBy { it.until }.toList()
                        items(list) { (until, todo) ->
                            Row(Modifier.fillMaxWidth()) {
                                val colorScheme = MaterialTheme.colorScheme
                                val textMeasurer = rememberTextMeasurer()
                                val style = MaterialTheme.typography.labelSmall
                                val difference = LocalDateTime.now()
                                    .until(until.toZonedLocalDateTime(), ChronoUnit.DAYS)

                                val text =
                                    if (difference < 6) until.format(DateTimeFormatter.ofPattern("E"))
                                    else until.format(DateTimeFormatter.ofPattern("d.M."))
                                val textLayoutResult = remember(text, style) {
                                    textMeasurer.measure(text, style)
                                }
                                Column(
                                    modifier = Modifier
                                        .drawWithContent {
                                            drawContent()
                                            if (todo.none { hw ->
                                                    (hw.isEnabled || state.showDisabled) &&
                                                            (!hw.isHidden || state.showHidden) &&
                                                            (hw.tasks.any { task -> !task.done } || state.showDone)
                                                }) return@drawWithContent
                                            drawCircle(
                                                color = colorScheme.tertiary,
                                                center = Offset(90f, 105f),
                                                radius = 50f
                                            )
                                            drawCircle(
                                                color = colorScheme.surface,
                                                center = Offset(90f, 105f),
                                                radius = 40f
                                            )
                                            try {
                                                drawText(
                                                    textMeasurer = textMeasurer,
                                                    text = text,
                                                    style = style.copy(color = colorScheme.onSurface),
                                                    topLeft = Offset(
                                                        90f - (textLayoutResult.size.width / 2),
                                                        105f - textLayoutResult.size.height / 2,
                                                    )
                                                )
                                            } catch (_: IllegalArgumentException) {}
                                        }
                                ) {
                                    todo.forEach { homework ->
                                        HomeworkCard(
                                            modifier = Modifier.padding(start = 60.dp),
                                            homework = homework,
                                            isOwner = homework.isOwner,
                                            showHidden = state.showHidden,
                                            showDisabled = state.showDisabled,
                                            showDone = state.showDone,
                                            allDone = { onMarkAllDone(homework, it) },
                                            singleDone = { task, done ->
                                                onMarkSingleDone(
                                                    task,
                                                    done
                                                )
                                            },
                                            onAddTask = { onAddTask(homework, it) },
                                            onDeleteRequest = { onHomeworkDeleteRequest(homework) },
                                            onChangePublicVisibility = {
                                                onHomeworkChangeVisibilityRequest(
                                                    homework
                                                )
                                            },
                                            onDeleteTaskRequest = { onHomeworkTaskDeleteRequest(it) },
                                            onEditTaskRequest = { onHomeworkTaskEditRequest(it) },
                                            onHomeworkHide = { onHomeworkHide(homework) }
                                        )
                                    }
                                }
                            }
                        }
                        if (state.homework.none {
                                (it.isEnabled || state.showDisabled) &&
                                        (!it.isHidden || state.showHidden) &&
                                        (it.tasks.any { task -> !task.done } || state.showDone)
                            }) {
                            item { NoHomework() }
                        }
                    }
                }
                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(alignment = Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeworkScreenPreview() {
    HomeworkScreenContent(
        state = HomeworkState(
            wrongProfile = false,
            homework = listOf()
        )
    )
}