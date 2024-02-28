package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.view.ui.components.HomeworkCard
import es.jvbabi.vplanplus.feature.homework.view.ui.components.WrongProfile
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.ChangeVisibilityDialog
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.DeleteHomeworkDialog
import es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs.DeleteHomeworkTaskDialog
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun HomeworkScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

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
        state = state,
        navBar = navBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkScreenContent(
    onBack: () -> Unit = {},
    onAddHomework: () -> Unit = {},
    onMarkAllDone: (homework: Homework, done: Boolean) -> Unit = { _, _ -> },
    onMarkSingleDone: (homeworkTask: HomeworkTask, done: Boolean) -> Unit = { _, _ -> },
    onAddTask: (homework: Homework, task: String) -> Unit = { _, _ -> },
    onHomeworkDeleteRequest: (homework: Homework?) -> Unit = {},
    onHomeworkDeleteRequestConfirm: () -> Unit = {},
    onHomeworkChangeVisibilityRequest: (homework: Homework?) -> Unit = {},
    onHomeworkChangeVisibilityRequestConfirm: () -> Unit = {},
    onHomeworkTaskDeleteRequest: (homeworkTask: HomeworkTask?) -> Unit = {},
    onHomeworkTaskDeleteRequestConfirm: () -> Unit = {},
    onHomeworkTaskEditRequest: (homeworkTask: HomeworkTask?) -> Unit = {},
    onHomeworkTaskEditRequestConfirm: (newContent: String?) -> Unit = {},
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
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.wrongProfile) {
                WrongProfile()
                return@Column
            }

            LazyColumn {
                items(state.homework.sortedBy { it.homework.until }) { homework ->
                    HomeworkCard(
                        currentUser = state.identity.vppId,
                        homework = homework.homework,
                        isOwner = homework.isOwner,
                        allDone = { onMarkAllDone(homework.homework, it) },
                        singleDone = { task, done -> onMarkSingleDone(task, done) },
                        onAddTask = { onAddTask(homework.homework, it) },
                        onDeleteRequest = { onHomeworkDeleteRequest(homework.homework) },
                        onChangePublicVisibility = { onHomeworkChangeVisibilityRequest(homework.homework) },
                        onDeleteTaskRequest = { onHomeworkTaskDeleteRequest(it) },
                        onEditTaskRequest = { onHomeworkTaskEditRequest(it) }
                    )
                }
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
        )
    )
}