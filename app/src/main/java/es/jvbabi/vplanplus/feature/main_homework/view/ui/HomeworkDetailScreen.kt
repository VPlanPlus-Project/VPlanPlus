package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DefaultLessonCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Documents
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DueToCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.ProgressCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Tasks
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.UnsavedChangesDialog
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.FadeAnimatedVisibility
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.VerticalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun HomeworkDetailScreen(
    navHostController: NavHostController,
    viewModel: HomeworkDetailViewModel = hiltViewModel(),
    homeworkId: Int
) {
    LaunchedEffect(key1 = homeworkId) { viewModel.init(homeworkId) { navHostController.popBackStack() } }
    val state = viewModel.state
    HomeworkDetailScreenContent(
        onBack = { navHostController.popBackStack() },
        onAction = viewModel::onAction,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkDetailScreenContent(
    onBack: () -> Unit = {},
    onAction: (action: UiAction) -> Unit = {},
    state: HomeworkDetailState
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var showUnsavedChangesDialog by rememberSaveable { mutableStateOf(false) }
    if (showUnsavedChangesDialog) UnsavedChangesDialog(
        onDismiss = { showUnsavedChangesDialog = false },
        onDiscardChanges = { onAction(ExitAndDiscardChangesAction) }
    )

    BackHandler(state.isEditing) {
        if (state.hasEdited) showUnsavedChangesDialog = true
        else onAction(ExitAndDiscardChangesAction)
    }

    Scaffold(
        topBar = {
            Box {
                VerticalExpandAnimatedAndFadingVisibility(visible = !state.isEditing) {
                    LargeTopAppBar(
                        title = { Text(stringResource(id = R.string.homework_detailViewTitle)) },
                        navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                        actions = {
                            if (state.canEdit) {
                                IconButton(onClick = { onAction(StartEditModeAction) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(id = R.string.homework_detailViewEditTitle)
                                    )
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
                FadeAnimatedVisibility(visible = state.isEditing) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(text = stringResource(id = R.string.homework_detailViewEditTitle))
                                VerticalExpandVisibility(visible = state.hasEdited) {
                                    Text(
                                        text = stringResource(id = R.string.homework_detailViewUnsavedChanges),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (state.hasEdited) showUnsavedChangesDialog = true
                                else onAction(ExitAndDiscardChangesAction) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(id = android.R.string.cancel)
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { onAction(ExitAndSaveHomeworkAction) },
                                enabled = state.homework?.tasks?.isNotEmpty() == true
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(id = R.string.save)
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer4Dp()
            VerticalExpandVisibility(visible = !state.isEditing) {
                Column {
                    ProgressCard(
                        tasks = state.homework?.tasks?.size ?: 0,
                        done = state.homework?.tasks?.count { it.isDone } ?: 0)
                    Spacer8Dp()
                }
            }
            RowVerticalCenter(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultLessonCard(defaultLesson = state.homework?.defaultLesson)
                VerticalDivider(Modifier.height(64.dp))
                DueToCard(
                    until = (if (state.isEditing) state.editDueDate else null)
                        ?: state.homework?.until?.toLocalDate(),
                    onUpdateDueDate = { onAction(UpdateDueDateAction(it)) },
                    isEditModeActive = state.isEditing
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            Tasks(
                tasks = state.homework?.tasks ?: emptyList(),
                isEditing = state.isEditing,
                newTasks = state.newTasks,
                editTasks = state.editedTasks,
                deletedTasks = state.tasksToDelete,
                onAddTask = { newTask -> onAction(AddTaskAction(newTask)) },
                onTaskClicked = { onAction(TaskDoneStateToggledAction(it)) },
                onDeleteTask = { onAction(DeleteTaskAction(it)) },
                onUpdateTask = { task -> onAction(UpdateTaskContentAction(task)) }
            )
            Spacer8Dp()
            Documents(
                documents = state.homework?.documents ?: emptyList(),
                markedAsRemoveUris = state.documentsToDelete.map { it.uri },
                isEditing = state.isEditing,
                onRename = { onAction(RenameDocumentAction(it)) },
                onRemove = { onAction(DeleteDocumentAction(it)) }
            )
        }
    }
}

@Preview
@Composable
fun HomeworkDetailScreenPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val group = GroupPreview.generateGroup(school)
    val vppId = VppIdPreview.generateVppId(group)
    val profile = ProfilePreview.generateClassProfile(group, vppId)
    val teacher = TeacherPreview.teacher(school)
    HomeworkDetailScreenContent(
        state = HomeworkDetailState(
            homework = Homework(
                id = 1,
                createdBy = vppId,
                documents = emptyList(),
                tasks = listOf(
                    HomeworkTask(id = 1, content = "Task 1", isDone = false),
                    HomeworkTask(id = 2, content = "Task 2", isDone = true),
                    HomeworkTask(id = 3, content = "Task 3", isDone = false)
                ),
                profile = profile,
                group = group,
                createdAt = ZonedDateTime.now(),
                until = ZonedDateTime.now().plusDays(1),
                defaultLesson = DefaultLesson(
                    defaultLessonId = UUID.randomUUID(),
                    `class` = group,
                    vpId = 1,
                    subject = "DEU",
                    teacher = teacher
                ),
                isHidden = false,
                isPublic = true
            ),
            canEdit = true,
            isEditing = true
        )
    )
}