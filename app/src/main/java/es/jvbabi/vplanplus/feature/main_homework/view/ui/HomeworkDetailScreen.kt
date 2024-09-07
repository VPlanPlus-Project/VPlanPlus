package es.jvbabi.vplanplus.feature.main_homework.view.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.pickDocumentLauncher
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberPickPhotoLauncher
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberScanner
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberTakePhotoLauncher
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DocumentUpdate
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DefaultLessonCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Documents
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DueToCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.ProgressCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Tasks
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.UnsavedChangesDialog
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.visibility.VisibilityCard
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.FadeAnimatedVisibility
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.VerticalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.util.getFileSize
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
    val context = LocalContext.current

    val pickDocumentLauncher = pickDocumentLauncher { viewModel.onAction(AddDocumentAction(DocumentUpdate.NewDocument(it, size = it.toFile().getFileSize(), extension = HomeworkDocumentType.PDF.extension))) }
    val (scanner, scannerLauncher) = rememberScanner { viewModel.onAction(AddDocumentAction(DocumentUpdate.NewDocument(it, size = it.toFile().getFileSize(), extension = HomeworkDocumentType.PDF.extension))) }

    val takePhotoLauncher =
        rememberTakePhotoLauncher(key1 = state.newDocuments.size) { viewModel.onAction(AddDocumentAction(DocumentUpdate.NewDocument(it, size = it.toFile().getFileSize(), extension = HomeworkDocumentType.JPG.extension))) }
    val pickPhotosLauncher = rememberPickPhotoLauncher { viewModel.onAction(AddDocumentAction(DocumentUpdate.NewDocument(it, size = it.toFile().getFileSize(), extension = HomeworkDocumentType.JPG.extension))) }

    HomeworkDetailScreenContent(
        onBack = { navHostController.popBackStack() },
        onAction = viewModel::onAction,
        onTakePhotoClicked = { takePhotoLauncher.launch(android.Manifest.permission.CAMERA) },
        onPickPhotoClicked = { pickPhotosLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onScanDocumentClicked = {
            scanner.getStartScanIntent(context as Activity)
                .addOnSuccessListener {
                    scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
                }
                .addOnFailureListener {
                    Log.e("AddHomeworkScreen", "Failed to start scanning", it)
                }
        },
        onPickDocumentClicked = { pickDocumentLauncher.launch("application/pdf") },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkDetailScreenContent(
    onBack: () -> Unit = {},
    onAction: (action: UiAction) -> Unit = {},
    onTakePhotoClicked: () -> Unit = {},
    onPickPhotoClicked: () -> Unit = {},
    onScanDocumentClicked: () -> Unit = {},
    onPickDocumentClicked: () -> Unit = {},
    state: HomeworkDetailState
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

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
                            if (state.canEditOrigin) IconButton(onClick = { isDeleteDialogOpen = true }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete))
                            }
                            IconButton(onClick = { onAction(StartEditModeAction) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(id = R.string.homework_detailViewEditTitle)
                                )
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
                                else onAction(ExitAndDiscardChangesAction)
                            }
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
                                enabled = state.personalizedHomework?.tasks?.isNotEmpty() == true && !state.isLoading
                            ) {
                                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                else Icon(
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
                        tasks = state.personalizedHomework?.tasks?.size ?: 0,
                        done = state.personalizedHomework?.tasks?.count { it.isDone } ?: 0)
                    Spacer8Dp()
                }
            }
            RowVerticalCenter(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultLessonCard(defaultLesson = state.personalizedHomework?.homework?.defaultLesson)
                VerticalDivider(Modifier.height(64.dp))
                DueToCard(
                    until = (if (state.isEditing) state.editDueDate else null)
                        ?: state.personalizedHomework?.homework?.until?.toLocalDate(),
                    onUpdateDueDate = { onAction(UpdateDueDateAction(it)) },
                    isEditModeActive = state.isEditing && state.canEditOrigin
                )
            }
            RowVerticalCenter(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                if (state.personalizedHomework == null || state.personalizedHomework !is PersonalizedHomework.CloudHomework) return@RowVerticalCenter
                VisibilityCard(
                    isEditModeActive = state.isEditing,
                    isCurrentlyVisibleOrPublic = (state.canEditOrigin && state.personalizedHomework.homework.isPublic) || (!state.canEditOrigin && !state.personalizedHomework.isHidden),
                    willBeVisibleOrPublic = state.editVisibility,
                    canModifyOrigin = state.canEditOrigin,
                    onChangeVisibility = { onAction(ChangeVisibilityAction(it)) }
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            Tasks(
                tasks = state.personalizedHomework?.tasks ?: emptyList(),
                isEditing = state.isEditing && state.canEditOrigin,
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
                documents = state.personalizedHomework?.homework?.documents ?: emptyList(),
                changedDocuments = state.editedDocuments,
                newDocuments = state.newDocuments,
                markedAsRemoveIds = state.documentsToDelete.map { it.documentId },
                isEditing = state.isEditing && state.canEditOrigin,
                onRename = { onAction(RenameDocumentAction(it)) },
                onRemove = { onAction(DeleteDocumentAction(it)) },
                onPickPhotoClicked = onPickPhotoClicked,
                onTakePhotoClicked = onTakePhotoClicked,
                onPickDocumentClicked = onPickDocumentClicked,
                onScanDocumentClicked = onScanDocumentClicked
            )
        }
    }

    if (isDeleteDialogOpen) {
        YesNoDialog(
            icon = Icons.Default.DeleteForever,
            title = stringResource(id = R.string.homework_deleteHomeworkTitle),
            message =
            when (state.personalizedHomework) {
                is PersonalizedHomework.CloudHomework -> if (state.personalizedHomework.homework.isPublic) stringResource(id = R.string.homework_deleteHomeworkTextPublic) else stringResource(id = R.string.homework_deleteHomeworkTextPrivate)
                is PersonalizedHomework.LocalHomework -> stringResource(id = R.string.homework_deleteHomeworkTextLocal)
                else -> ""
            },
            onYes = { onAction(DeleteHomework) },
            onNo = { isDeleteDialogOpen = false },
        )
    }
}

@OptIn(PreviewFunction::class)
@Preview
@Composable
fun HomeworkDetailScreenPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val group = GroupPreview.generateGroup(school)
    val vppId = VppIdPreview.generateVppId(group).toActiveVppId()
    val teacher = TeacherPreview.teacher(school)
    HomeworkDetailScreenContent(
        state = HomeworkDetailState(
            personalizedHomework = PersonalizedHomework.CloudHomework(
                homework = HomeworkCore.CloudHomework(
                    id = 1,
                    createdBy = vppId,
                    documents = listOf(
                        HomeworkDocument(
                            documentId = 1,
                            homeworkId = 1,
                            type = HomeworkDocumentType.JPG,
                            name = "Document 1.jpg",
                            isDownloaded = true,
                            size = 1024
                        ),
                        HomeworkDocument(
                            documentId = 2,
                            homeworkId = 1,
                            type = HomeworkDocumentType.PDF,
                            name = "Document 2.pdf",
                            isDownloaded = false,
                            size = 2048*1024
                        )
                    ),
                    tasks = listOf(
                        HomeworkTaskCore(id = 1, content = "Task 1", homeworkId = 1),
                        HomeworkTaskCore(id = 2, content = "Task 2", homeworkId = 1),
                        HomeworkTaskCore(id = 3, content = "Task 3", homeworkId = 1)
                    ),
                    group = group,
                    createdAt = ZonedDateTime.now(),
                    until = ZonedDateTime.now().plusDays(1),
                    defaultLesson = DefaultLesson(
                        defaultLessonId = UUID.randomUUID(),
                        `class` = group,
                        vpId = 1,
                        subject = "DEU",
                        teacher = teacher,
                        courseGroup = null
                    ),
                    isPublic = true,
                ),
                isHidden = false,
                tasks = listOf(
                    HomeworkTaskDone(id = 1, content = "Task 1", isDone = false, homeworkId = 1),
                    HomeworkTaskDone(id = 2, content = "Task 2", isDone = true, homeworkId = 1),
                    HomeworkTaskDone(id = 3, content = "Task 3", isDone = false, homeworkId = 1)
                ),
                profile = ProfilePreview.generateClassProfile(
                    group, vppId
                )
            ),
            isEditing = true,
            isLoading = true,
        )
    )
}