package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.AddDocumentButton
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.AddImageButton
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.DocumentView
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog.SelectDefaultLessonSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to.SetDueToModal
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.blendColor
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.format.DateTimeFormatter

@Composable
fun AddHomeworkScreen(
    navHostController: NavHostController,
    viewModel: AddHomeworkViewModel = hiltViewModel(),
    vpId: Int? = null
) {
    val context = LocalContext.current

    val imageFile = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        imageFile
    )
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSaved ->
            if (isSaved) viewModel.onUiAction(AddImage(fileFromContentUri(context, uri).toUri()))
        }
    )

    val pickPhotosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            it.forEach { uri -> viewModel.onUiAction(AddImage(fileFromContentUri(context, uri).toUri())) }
        }
    )

    val pickDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            it.forEach { uri -> viewModel.onUiAction(AddDocument(fileFromContentUri(context, uri).toUri())) }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        if (it) takePhotoLauncher.launch(uri)
        else Log.e("AddHomeworkScreen", "Permission denied")
    }

    val scannerOptions = remember {
        GmsDocumentScannerOptions.Builder()
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setPageLimit(3)
            .setResultFormats(RESULT_FORMAT_PDF)
            .build()
    }

    val scanner = remember {
        GmsDocumentScanning.getClient(scannerOptions)
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
            Log.d("AddHomeworkScreen", "Scanned ${result?.pages?.size} pages")
            if (result?.pdf?.uri == null) return@rememberLauncherForActivityResult
            viewModel.onUiAction(AddDocument(result.pdf?.uri ?: return@rememberLauncherForActivityResult))
        }
    }

    val state = viewModel.state.value
    LaunchedEffect(vpId, state.initDone) {
        if (vpId == null) return@LaunchedEffect
        viewModel.setDefaultLesson(state.defaultLessons.firstOrNull { it.vpId == vpId })
    }
    AddHomeworkContent(
        onBack = { navHostController.popBackStack() },
        onSetDefaultLesson = { viewModel.setDefaultLesson(it) },
        onOpenVppIdSettings = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onTakePhoto = {
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                takePhotoLauncher.launch(uri)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onPickPhotos = {
            pickPhotosLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onPickDocuments = {
            pickDocumentLauncher.launch("application/pdf")
        },
        onSave = viewModel::requestSave,
        onAction = viewModel::onUiAction,
        onScanDocument = {
            scanner.getStartScanIntent(context as Activity)
                .addOnSuccessListener {
                    scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
                }
                .addOnFailureListener {
                    Log.e("AddHomeworkScreen", "Failed to start scanning", it)
                }
        },
        state = state
    )

    LaunchedEffect(key1 = state.result) {
        if (
            state.result == HomeworkModificationResult.SUCCESS_OFFLINE ||
            state.result == HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        ) {
            navHostController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHomeworkContent(
    onBack: () -> Unit = {},
    onSetDefaultLesson: (DefaultLesson?) -> Unit = {},
    onOpenVppIdSettings: () -> Unit = {},
    onSave: () -> Unit = {},
    onAction: (action: AddHomeworkUiEvent) -> Unit = { _ -> },
    onTakePhoto: () -> Unit = {},
    onPickPhotos: () -> Unit = {},
    onScanDocument: () -> Unit = {},
    onPickDocuments: () -> Unit = {},
    state: AddHomeworkState
) {

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
        onSelectDefaultLesson = onSetDefaultLesson
    )

    var isUntilSheetOpen by rememberSaveable { mutableStateOf(false) }
    val untilSheetState = rememberModalBottomSheetState(true)
    if (isUntilSheetOpen) SetDueToModal(
        sheetState = untilSheetState,
        selectedDate = state.until,
        onSelectDate = { onAction(UpdateUntil(it)) },
        onDismiss = { isUntilSheetOpen = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.home_addHomeworkLabel))
                        if (state.username != null) Text(
                            text = state.username,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                },
                actions = {
                    val colorScheme = MaterialTheme.colorScheme
                    Balloon(
                        builder = rememberBalloonBuilder {
                            setArrowSize(10)
                            setArrowPosition(0.5f)
                            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                            setWidth(BalloonSizeSpec.WRAP)
                            setHeight(BalloonSizeSpec.WRAP)
                            setPadding(12)
                            setMarginHorizontal(12)
                            setCornerRadius(16f)
                            setBackgroundColor(colorScheme.primaryContainer)
                            setBalloonAnimation(BalloonAnimation.FADE)
                        },
                        balloonContent = {
                            Text(text = stringResource(id = R.string.addHomework_newDesignInfo), color = colorScheme.onPrimaryContainer)
                        }
                    ) { balloonWindow ->
                        LaunchedEffect(key1 = state.showNewSaveButtonLocationBalloon) {
                            delay(100)
                            if (state.showNewSaveButtonLocationBalloon && state.canSubmit) {
                                balloonWindow.showAlignBottom()
                                onAction(NewLayoutBalloonDismissed)
                            }
                        }
                        TextButton(
                            onClick = onSave,
                            enabled = state.canSubmit
                        ) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val focusRequester = remember { FocusRequester() }
            var taskIndexToFocus: Int? by remember { mutableStateOf(null) }
            state.tasks.forEachIndexed { i, task ->
                var selection: TextRange by remember { mutableStateOf(TextRange.Zero) }
                var textFieldValueState by remember(i, task, selection) { mutableStateOf(TextFieldValue(text = task, selection = selection)) }

                TextField(
                    value = textFieldValueState,
                    onValueChange = {
                        selection = it.selection
                        onAction(UpdateTask(i, it.text))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (i == taskIndexToFocus) Modifier.focusRequester(focusRequester) else Modifier),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = stringResource(id = R.string.addHomework_taskPlaceholder, i + 1)) },
                    leadingIcon = {
                        Box(
                            Modifier
                                .drawWithContent {
                                    drawCircle(
                                        color = colorScheme.outline,
                                        center = center,
                                        radius = 10.dp.toPx()
                                    )
                                    drawCircle(
                                        color = colorScheme.background,
                                        center = center,
                                        radius = 8.dp.toPx()
                                    )
                                }
                        ) {}
                    },
                    trailingIcon = {
                        IconButton(onClick = { onAction(DeleteTask(i)) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                )
                LaunchedEffect(key1 = taskIndexToFocus) {
                    if (taskIndexToFocus != i) return@LaunchedEffect
                    focusRequester.requestFocus()
                    textFieldValueState = TextFieldValue(text = task, selection = TextRange(1))
                    taskIndexToFocus = null
                }
            }
            TextField(
                value = "",
                onValueChange = {
                    if (it.isBlank()) return@TextField
                    onAction(CreateTask(it))
                    taskIndexToFocus = state.tasks.size
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = stringResource(id = R.string.addHomework_newTask)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            )

            RowVerticalCenterSpaceBetweenFill(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(92.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = colorScheme.outline,
                            start = Offset(size.width / 2, 16.dp.toPx()),
                            end = Offset(size.width / 2, size.height - 16.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                RowVerticalCenter(
                    Modifier
                        .weight(1f, true)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isSelectDefaultLessonSheetOpen = true }
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubjectIcon(
                        subject = state.selectedDefaultLesson?.subject,
                        modifier = Modifier.size(32.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                    RowVerticalCenterSpaceBetweenFill {
                        Column {
                            Text(
                                text = stringResource(id = R.string.addHomework_lesson),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text =
                                if (state.selectedDefaultLesson == null) stringResource(id = R.string.addHomework_notSelected)
                                else if (state.selectedDefaultLesson.teacher == null) stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher, state.selectedDefaultLesson.subject)
                                else stringResource(
                                    id = R.string.addHomework_lessonSubtitle,
                                    state.selectedDefaultLesson.subject,
                                    state.selectedDefaultLesson.teacher.acronym
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        AnimatedVisibility(
                            visible = state.selectedDefaultLesson != null,
                            enter = expandHorizontally(),
                            exit = shrinkHorizontally()
                        ) {
                            IconButton(
                                onClick = { onSetDefaultLesson(null) }
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }
                }
                RowVerticalCenter(
                    Modifier
                        .weight(1f, true)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isUntilSheetOpen = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.addHomework_until),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (state.until == null) stringResource(id = R.string.addHomework_notSelected) else state.until.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.addHomework_storeTitle),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
            )
            RowVerticalCenter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentedButtons(Modifier.weight(1f, true)) {
                    SegmentedButtonItem(
                        icon = { Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.addHomework_saveThisDevice)) },
                        selected = state.saveType == SaveType.LOCAL,
                        onClick = { onAction(UpdateSaveType(SaveType.LOCAL)) }
                    )
                }

                SegmentedButtons(Modifier.weight(2f, true)) {
                    SegmentedButtonItem(
                        icon = { Icon(imageVector = Icons.Default.CloudQueue, contentDescription = null) },
                        label = { Text(stringResource(id = R.string.addHomework_saveVppId)) },
                        selected = state.saveType == SaveType.CLOUD,
                        onClick = { onAction(UpdateSaveType(SaveType.CLOUD)) }
                    )
                    SegmentedButtonItem(
                        icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
                        label = { Text(stringResource(id = R.string.addHomework_saveVppIdSharedTitle)) },
                        selected = state.saveType == SaveType.SHARED,
                        onClick = { onAction(UpdateSaveType(SaveType.SHARED)) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val style = MaterialTheme.typography.bodyMedium
                val iconColorFade by animateFloatAsState(targetValue = if (state.isInvalidSaveTypeSelected) 1f else 0f, label = "info icon fader")
                val iconColor = blendColor(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.error, iconColorFade)
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(style.lineHeight.value.dp),
                    tint = iconColor
                )
                Column {
                    Text(
                        text = when (state.saveType) {
                            SaveType.LOCAL -> stringResource(id = R.string.addHomework_saveThisDeviceText)
                            null -> ""
                            else -> {
                                if (state.canUseCloud) {
                                    var string =  stringResource(id = R.string.addHomework_saveVppIdText)
                                    if (state.saveType == SaveType.SHARED) string += " " + stringResource(id = R.string.addHomework_saveVppIdSharedDescription)
                                    string
                                }
                                else stringResource(id = R.string.addHomework_saveVppIdNoVppId)
                            }
                        },
                        style = style,
                    )
                    if (state.isInvalidSaveTypeSelected) {
                        TextButton(onClick = onOpenVppIdSettings, modifier = Modifier.align(Alignment.End)) {
                            Icon(imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight, contentDescription = null)
                            Text(text = stringResource(id = R.string.addHomework_noVppIdButtonOpenSettings))
                        }
                    }
                }
            }

            HorizontalDivider()

            LazyRow(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer8Dp() }
                items(state.documents.toList(), key = { it.first.hashCode() }) { documentUri ->
                    DocumentView(uri = documentUri.first) { onAction(RemoveDocument(documentUri.first)) }
                    VerticalDivider()
                }
                item {
                    Column {
                        AddImageButton(onOpenCamera = onTakePhoto, onOpenGallery = onPickPhotos)
                        AddDocumentButton(onScanDocument, onPickDocuments)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHomeworkScreenPreview() {
    AddHomeworkContent(
        state = AddHomeworkState(
            username = "John Doe",
            tasks = listOf("Task 1", "Task 2", "Task 3"),
            canUseCloud = true,
            canShowCloudInfoBanner = true
        )
    )
}

private fun Context.createImageFile(): File {
    val timestamp = System.currentTimeMillis().toString()
    val folder = File(cacheDir, "homework_documents")
    if (!folder.exists()) folder.mkdirs()
    val image = File.createTempFile("JPEG_${timestamp}_", ".jpg", folder)
    return image
}

private fun fileFromContentUri(context: Context, contentUri: Uri): File {

    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temporary_file" + contentUri.pathSegments.last() + if (fileExtension != null) ".$fileExtension" else ""

    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
            copy(inputStream, oStream)
        }

        oStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}