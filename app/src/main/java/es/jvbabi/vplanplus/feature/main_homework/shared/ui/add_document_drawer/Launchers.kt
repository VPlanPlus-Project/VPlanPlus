package es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Composable
fun pickDocumentLauncher(
    onSuccess: (uri: Uri) -> Unit
): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            if (it.isEmpty()) return@rememberLauncherForActivityResult
            it.forEach { uri -> onSuccess(fileFromContentUri(context, uri).toUri()) }
        }
    )
}

private val defaultScannerOptions = GmsDocumentScannerOptions.Builder()
    .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
    .setGalleryImportAllowed(true)
    .setPageLimit(3)
    .setResultFormats(RESULT_FORMAT_PDF)
    .build()

@Composable
fun rememberScanner(
    scannerOptions: GmsDocumentScannerOptions = defaultScannerOptions,
    onSuccess: (uri: Uri) -> Unit
): Pair<GmsDocumentScanner, ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>> {
    val scanner = remember { GmsDocumentScanning.getClient(scannerOptions) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
            Log.d("AddHomeworkScreen", "Scanned ${result?.pages?.size} pages")
            onSuccess(result?.pdf?.uri ?: return@rememberLauncherForActivityResult)
        }
    }
    return scanner to launcher
}

@Composable
fun rememberTakePhotoLauncher(
    key1: Any,
    onResult: (uri: Uri) -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current

    val imageFile = remember(key1) { context.createImageFile() }
    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        imageFile
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSaved ->
            if (isSaved) {
                onResult(fileFromContentUri(context, uri).toUri())
            }
        }
    )

    return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        if (it) takePhotoLauncher.launch(uri)
        else Log.e("AddHomeworkScreen", "Permission denied")
    }
}

@Composable
fun rememberPickPhotoLauncher(
    onResult: (uri: Uri) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {
            it.forEach { uri -> onResult(fileFromContentUri(context, uri).toUri()) }
        }
    )
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

private fun Context.createImageFile(): File {
    val timestamp = System.currentTimeMillis().toString()
    val folder = File(cacheDir, "homework_documents")
    if (!folder.exists()) folder.mkdirs()
    val image = File.createTempFile("JPEG_${timestamp}_", ".jpg", folder)
    return image
}
