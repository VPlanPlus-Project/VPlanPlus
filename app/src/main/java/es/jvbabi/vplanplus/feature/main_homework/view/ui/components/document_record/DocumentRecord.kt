package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toFile
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.ui.common.HorizontalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.VerticalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.storageToHumanReadableFormat
import okio.FileNotFoundException
import java.io.File

@Composable
fun DocumentRecord(
    uri: Uri?,
    isDownloaded: Boolean,
    size: Long,
    name: String?,
    newName: String? = null,
    type: HomeworkDocumentType,
    progress: Float? = null,
    isEditing: Boolean,
    onRename: (to: String) -> Unit = {},
    onRemove: () -> Unit = {},
    onDownload: (onDownloaded: (downloadedUri: Uri) -> Unit) -> Unit = {}
) {
    var isLoading by remember(uri) { mutableStateOf(true) }
    val context = LocalContext.current
    var bitmap: Bitmap? by remember(uri) { mutableStateOf(null) }
    var pageCount by remember(uri) { mutableIntStateOf(0) }
    var isDownloadStarted by remember(isDownloaded) { mutableStateOf(false) }
    val isLoadingAnim by animateFloatAsState(targetValue = if (isDownloadStarted) 1f else 0f, label = "isLoadingAnim")
    LaunchedEffect(key1 = uri, key2 = isDownloaded) {
        if (uri == null || !isDownloaded) {
            isLoading = false
            return@LaunchedEffect
        }
        when (type) {
            HomeworkDocumentType.PDF -> {
                val file = uri.toFile()
                val pdfRenderer = try {
                    PdfRenderer(
                        ParcelFileDescriptor.open(file, MODE_READ_ONLY)
                    )
                } catch (e: Exception) {
                    isLoading = false
                    return@LaunchedEffect
                }
                pdfRenderer.openPage(0).use {
                    bitmap = createBitmap(it.width, it.height)
                    it.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }
                pageCount = pdfRenderer.pageCount
            }

            HomeworkDocumentType.JPG -> {
                bitmap = try {
                    context.contentResolver.openInputStream(uri)?.use {
                        it.use { stream -> Bitmap.createBitmap(BitmapFactory.decodeStream(stream)) }
                    }
                } catch (_: FileNotFoundException) { null }
            }
        }
        isLoading = false
    }

    var showEditFilenameDialog by rememberSaveable { mutableStateOf(false) }
    if (showEditFilenameDialog) {
        RenameDialog(
            currentValue = name,
            onDismiss = { showEditFilenameDialog = false },
            onOk = { showEditFilenameDialog = false; onRename(it) }
        )
    }

    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                val openFile = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val file = File(context.cacheDir, FileRepository.createSafeFileName(name ?: "unknown"))
                    uri!!
                        .toFile()
                        .copyTo(file, overwrite = true)
                    val newUri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".fileprovider",
                        file
                    )
                    when (type) {
                        HomeworkDocumentType.PDF -> intent.setDataAndType(newUri, "application/pdf")
                        HomeworkDocumentType.JPG -> intent.setDataAndType(newUri, "image/*")
                    }
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.startActivity(intent)
                }
                if (!isDownloaded) {
                    isDownloadStarted = true
                    onDownload { openFile() }
                }
                else openFile()
            }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else if (!isDownloaded) {
                Icon(imageVector = Icons.Outlined.CloudDownload, contentDescription = null, modifier = Modifier
                    .size(32.dp)
                    .scale(1 - 0.3f * isLoadingAnim))
                CircularProgressIndicator(Modifier.alpha(isLoadingAnim))
            }
            if (bitmap != null) Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(visible = !isLoading) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    if (name != null) {
                        if (newName != null) {
                            Text(
                                text = newName,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        val nameStyle = if (newName == null) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge.copy(textDecoration = TextDecoration.LineThrough, fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic)
                        Text(
                            text = name,
                            style = nameStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isDownloaded) {
                        if (type == HomeworkDocumentType.PDF) Text(
                            text = pluralStringResource(id = R.plurals.homework_detailViewDocumentPages, count = pageCount, pageCount),
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (type == HomeworkDocumentType.JPG) Text(
                            text = bitmap?.width.toString() + "x" + bitmap?.height.toString(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.homework_detailViewDocumentOnline),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = storageToHumanReadableFormat(size),
                        style = MaterialTheme.typography.labelSmall
                    )
                    VerticalExpandAnimatedAndFadingVisibility(visible = progress != null) {
                        LinearProgressIndicator(
                            progress = { progress ?: 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                HorizontalExpandAnimatedAndFadingVisibility(
                    visible = isEditing,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .shadow(4.dp, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(4.dp)
                ) {
                    RowVerticalCenter {
                        IconButton(onClick = { showEditFilenameDialog = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        }
                        IconButton(onClick = onRemove) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DocumentRecordPreview() {
    DocumentRecord(null, true, 453614563,null, null, HomeworkDocumentType.PDF, 0.3f, false)
}

@Composable
@Preview(showBackground = true)
private fun DocumentRecordEditingPreview() {
    DocumentRecord(null, false, 4513, "A file", null, HomeworkDocumentType.PDF, null, true)
}