package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.balloon.compose.Balloon
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.StoreSaveModal
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog.SelectDefaultLessonSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to.SetDueToModal
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.AddDocumentModal
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.AddImageModel
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.pickDocumentLauncher
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberPickPhotoLauncher
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberScanner
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.rememberTakePhotoLauncher
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record.DocumentRecord
import es.jvbabi.vplanplus.ui.common.BasicInputField
import es.jvbabi.vplanplus.ui.common.DefaultBalloonDescription
import es.jvbabi.vplanplus.ui.common.DefaultBalloonTitle
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.common.rememberDefaultBalloon
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkSheetContent(
    viewModel: AddHomeworkViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onChanged: () -> Unit,
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) { viewModel.init() }

    val pickDocumentLauncher = pickDocumentLauncher { viewModel.onUiAction(AddDocument(it)); onChanged() }
    val (scanner, scannerLauncher) = rememberScanner { viewModel.onUiAction(AddDocument(it)); onChanged() }

    val takePhotoLauncher = rememberTakePhotoLauncher(key1 = state.documents.size) { viewModel.onUiAction(AddImage(it)); onChanged() }
    val pickPhotosLauncher = rememberPickPhotoLauncher { viewModel.onUiAction(AddImage(it)); onChanged() }

    var isAddDocumentModalOpen by rememberSaveable { mutableStateOf(false) }
    val addDocumentSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (isAddDocumentModalOpen) {
        AddDocumentModal(
            sheetState = addDocumentSheetState,
            onChooseDocument = {
                isAddDocumentModalOpen = false
                pickDocumentLauncher.launch("application/pdf")
            },
            onScanDocument = {
                isAddDocumentModalOpen = false
                scanner.getStartScanIntent(context as Activity)
                    .addOnSuccessListener {
                        scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
                    }
                    .addOnFailureListener {
                        Log.e("AddHomeworkScreen", "Failed to start scanning", it)
                    }
            },
            onDismiss = { isAddDocumentModalOpen = false }
        )
    }

    var isAddPhotoModalOpen by rememberSaveable { mutableStateOf(false) }
    val addPhotoSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (isAddPhotoModalOpen) {
        AddImageModel(
            sheetState = addPhotoSheetState,
            onOpenCamera = {
                isAddPhotoModalOpen = false
                takePhotoLauncher.launch(Manifest.permission.CAMERA)
            },
            onOpenGallery = {
                isAddPhotoModalOpen = false
                pickPhotosLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onDismiss = { isAddPhotoModalOpen = false }
        )
    }

    var isSaveLocationModalOpen by rememberSaveable { mutableStateOf(false) }
    val saveLocationModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )
    if (isSaveLocationModalOpen && state.saveType != null) StoreSaveModal(
        sheetState = saveLocationModalSheetState,
        currentState = state.saveType,
        onSubmit = { viewModel.onUiAction(UpdateSaveType(it)) },
        onDismissRequest = { isSaveLocationModalOpen = false },
    )

    var isUntilSheetOpen by rememberSaveable { mutableStateOf(false) }
    val untilSheetState = rememberModalBottomSheetState(true)
    if (isUntilSheetOpen) SetDueToModal(
        sheetState = untilSheetState,
        selectedDate = state.until,
        onSelectDate = { viewModel.onUiAction(UpdateUntil(it)) },
        onDismiss = { isUntilSheetOpen = false }
    )

    var isSelectDefaultLessonSheetOpen by rememberSaveable { mutableStateOf(false) }
    val selectDefaultLessonSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )
    if (isSelectDefaultLessonSheetOpen) SelectDefaultLessonSheet(
        defaultLessons = state.defaultLessons,
        selectedDefaultLesson = state.selectedDefaultLesson,
        hasDefaultLessonsFiltered = state.defaultLessonsFiltered,
        sheetState = selectDefaultLessonSheetState,
        onDismiss = { isSelectDefaultLessonSheetOpen = false },
        onSelectDefaultLesson = { viewModel.setDefaultLesson(it) }
    )

    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(8.dp)
        ) tasks@{
            for (i in 0..state.tasks.size) {
                BasicInputField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.tasks.getOrElse(i) { "" },
                    onValueChange = {
                        if (state.tasks.getOrNull(i) == null) {
                            if (it.isBlank()) return@BasicInputField
                            viewModel.onUiAction(CreateTask(it))
                        }
                        else viewModel.onUiAction(UpdateTask(i, it))
                        onChanged()
                    },
                    placeholder = { Text(text = stringResource(id = R.string.homework_addTask)) },
                    trailingIcon = {
                        if (state.tasks.getOrNull(i) != null) IconButton(onClick = { viewModel.onUiAction(DeleteTask(i)) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    focusRequester = if (i == 0 && state.tasks.isEmpty()) focusRequester else null
                )
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Spacer4Dp() }
            item {
                FilterChip(
                    selected = state.until != null && state.until.isAfter(LocalDate.now().minusDays(1L)),
                    onClick = { isUntilSheetOpen = true },
                    label = {
                        Text(text = if (state.until == null) stringResource(id = R.string.addHomework_until) else stringResource(id = R.string.homework_dueTo, state.until.getRelativeStringResource().run {
                            if (this == null) return@run state.until.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                            return@run stringResource(id = this)
                        }))
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                    trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                )
            }
            item {
                FilterChip(
                    selected = state.selectedDefaultLesson != null,
                    onClick = { isSelectDefaultLessonSheetOpen = true },
                    label = { Text(text = state.selectedDefaultLesson?.subject ?: stringResource(id = R.string.addHomework_lesson)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Tag, contentDescription = null) },
                    trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                )
            }
            if (state.canUseCloud) {
                item { VerticalDivider(Modifier.height(32.dp)) }
                item {
                    Balloon(
                        builder = rememberDefaultBalloon(),
                        balloonContent = {
                            Column {
                                DefaultBalloonTitle(text = stringResource(id = R.string.addHomework_saveTypeBalloonTitle))
                                DefaultBalloonDescription(text = stringResource(id = R.string.addHomework_saveTypeBalloonText))
                            }
                        }
                    ) { balloonWindow ->
                        if (state.showVppIdStorageBalloon) balloonWindow.showAlignTop()
                        balloonWindow.setOnBalloonDismissListener { viewModel.onUiAction(HideVppIdStorageBalloon) }
                        AssistChip(
                            onClick = { isSaveLocationModalOpen = true },
                            label = { Text(text = when (state.saveType) {
                                SaveType.LOCAL -> stringResource(id = R.string.addHomework_saveThisDevice)
                                SaveType.CLOUD -> stringResource(id = R.string.addHomework_saveVppId)
                                SaveType.SHARED -> stringResource(id = R.string.addHomework_saveVppIdSharedTitle)
                                null -> ""
                            }) },
                            leadingIcon = {
                                if (state.saveType == null) return@AssistChip
                                Icon(imageVector = when (state.saveType) {
                                    SaveType.LOCAL -> Icons.Default.PhoneAndroid
                                    SaveType.CLOUD -> Icons.Default.CloudQueue
                                    SaveType.SHARED -> Icons.Default.Share
                                }, contentDescription = null)
                            },
                            trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                        )
                    }
                }
            }
        }

        RowVerticalCenterSpaceBetweenFill(modifier = Modifier.padding(8.dp)) {
            val colorScheme = MaterialTheme.colorScheme
            Balloon(
                builder = rememberDefaultBalloon(),
                balloonContent = {
                    Text(text = stringResource(id = R.string.addHomework_addDocumentsBalloon), color = colorScheme.onPrimaryContainer)
                }
            ) { balloonWindow ->
                if (state.showDocumentsBalloon) balloonWindow.showAlignTop()
                balloonWindow.setOnBalloonDismissListener { viewModel.onUiAction(HideDocumentBalloon) }
                RowVerticalCenter {
                    IconButton(onClick = { isAddDocumentModalOpen = true }) {
                        Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { isAddPhotoModalOpen = true }) {
                        Icon(imageVector = Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            if (state.documents.isEmpty()) SaveButton(canSave = state.canSave, isLoading = state.isLoading, onSave = { viewModel.onUiAction(SaveHomework { onClose() }) })
        }
        Column {
            state.documents.forEach { document ->
                DocumentRecord(
                    uri = document.uri,
                    type = document.type,
                    name = document.name,
                    isEditing = true,
                    progress = document.uploadProgress,
                    onRemove = { viewModel.onUiAction(RemoveDocument(document.uri)) }
                )
            }
        }
        if (state.documents.isNotEmpty()) Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            SaveButton(canSave = state.canSave, isLoading = state.isLoading, onSave = { viewModel.onUiAction(SaveHomework { onClose() }) })
        }
        VerticalExpandVisibility(visible = state.result == HomeworkModificationResult.FAILED, modifier = Modifier.padding(horizontal = 8.dp)) {
            InfoCard(
                imageVector = Icons.Default.Error,
                title = stringResource(id = R.string.something_went_wrong),
                text = stringResource(id = R.string.addHomework_saveFailedOnlineText),
                textColor = MaterialTheme.colorScheme.onErrorContainer,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (state.showDocumentsBalloon || state.showVppIdStorageBalloon) return@LaunchedEffect
        try { focusRequester.requestFocus() } catch (_: IllegalStateException) {}
    }
}

@Composable
private fun SaveButton(
    canSave: Boolean,
    isLoading: Boolean,
    onSave: () -> Unit
) {
    TextButton(
        onClick = onSave,
        enabled = canSave
    ) {
        val alpha = animateFloatAsState(targetValue = if (isLoading) 1f else 0f, label = "loading_alpha")
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.save), modifier = Modifier.alpha(1-alpha.value))
            CircularProgressIndicator(
                Modifier
                    .size(24.dp)
                    .alpha(alpha.value))
        }
    }
}
