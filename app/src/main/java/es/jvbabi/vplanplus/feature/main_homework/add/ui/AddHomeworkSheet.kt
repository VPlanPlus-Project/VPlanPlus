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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.AddDocumentModal
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.AddImageModel
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.StoreSaveModal
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog.SelectDefaultLessonSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to.SetDueToModal
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.unsaved_changes_dialog.Dialog
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private val scannerOptions = GmsDocumentScannerOptions.Builder()
    .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
    .setGalleryImportAllowed(true)
    .setPageLimit(3)
    .setResultFormats(RESULT_FORMAT_PDF)
    .build()


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkSheet(
    onClose: () -> Unit,
    viewModel: AddHomeworkViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = Unit) { viewModel.init() }

    val state = viewModel.state.value
    val context = LocalContext.current

    val pickDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            it.forEach { uri -> viewModel.onUiAction(AddDocument(fileFromContentUri(context, uri).toUri())) }
        }
    )

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

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        if (it) takePhotoLauncher.launch(uri)
        else Log.e("AddHomeworkScreen", "Permission denied")
    }

    var isDismissDialogOpen by rememberSaveable { mutableStateOf(false) }
    if (isDismissDialogOpen) {
        Dialog(onCancel = { isDismissDialogOpen = false }, onDiscard = onClose)
    }

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
            }
        )
    }

    var isAddPhotoModalOpen by rememberSaveable { mutableStateOf(false) }
    val addPhotoSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (isAddPhotoModalOpen) {
        AddImageModel(
            sheetState = addPhotoSheetState,
            onOpenCamera = {
                isAddPhotoModalOpen = false
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    takePhotoLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onOpenGallery = {
                isAddPhotoModalOpen = false
                pickPhotosLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
    }

    var isSaveLocationModalOpen by rememberSaveable { mutableStateOf(false) }
    val saveLocationModalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )
    if (isSaveLocationModalOpen) StoreSaveModal(
        sheetState = saveLocationModalSheetState,
        currentState = state.saveType ?: SaveType.LOCAL,
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

    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            if (it == SheetValue.Hidden) {
                if (state.tasks.isNotEmpty()) {
                    isDismissDialogOpen = true
                    return@rememberModalBottomSheetState false
                }
            }
            true
        },
    )
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = modalSheetState,
    ) {
        AddHomeworkSheetContent(
            tasks = state.tasks,
            onAddTask = { viewModel.onUiAction(CreateTask(it)) },
            onModifyTask = { index, task -> viewModel.onUiAction(UpdateTask(index, task)) },
            onRemoveTask = { viewModel.onUiAction(DeleteTask(it)) },

            until = state.until,
            onUntilClicked = { isUntilSheetOpen = true },

            selectedDefaultLesson = state.selectedDefaultLesson,
            onSelectDefaultLessonClicked = { isSelectDefaultLessonSheetOpen = true },

            canUseCloud = state.canUseCloud,
            saveType = state.saveType ?: SaveType.LOCAL,
            onSaveTypeClicked = { isSaveLocationModalOpen = true },

            documents = state.documents,
            onAddDocumentClicked = { isAddDocumentModalOpen = true },
            onAddPhotoClicked = { isAddPhotoModalOpen = true },
            onDeleteDocumentClicked = { viewModel.onUiAction(RemoveDocument(it)) },

            canSave = state.canSave,
            isLoading = state.isLoading,
            onSave = { viewModel.save { onClose() } }
        )
    }
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