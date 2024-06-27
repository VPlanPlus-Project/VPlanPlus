package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toFile
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.HomeworkDocumentType
import es.jvbabi.vplanplus.ui.common.HorizontalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.VerticalExpandAnimatedAndFadingVisibility
import es.jvbabi.vplanplus.ui.common.storageToHumanReadableFormat

@Composable
fun DocumentRecord(
    uri: Uri?,
    type: HomeworkDocumentType,
    progress: Float? = null,
    isEditing: Boolean,
    onRemove: () -> Unit = {}
) {
    var isLoading by remember(uri) { mutableStateOf(true) }
    val context = LocalContext.current
    var bitmap: Bitmap? by remember(uri) { mutableStateOf(null) }
    var pageCount by remember(uri) { mutableIntStateOf(0) }
    var documentSize by remember(uri) { mutableLongStateOf(0) }
    LaunchedEffect(key1 = uri) {
        if (uri == null) {
            isLoading = false
            return@LaunchedEffect
        }
        when (type) {
            HomeworkDocumentType.PDF -> {
                val file = uri.toFile()
                val pdfRenderer = PdfRenderer(
                    ParcelFileDescriptor.open(file, MODE_READ_ONLY)
                )
                pdfRenderer.openPage(0).use {
                    bitmap = createBitmap(it.width, it.height)
                    it.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }
                pageCount = pdfRenderer.pageCount
                documentSize = file.length()
            }
            HomeworkDocumentType.JPG -> {
                val file = uri.toFile()
                bitmap = context.contentResolver.openInputStream(uri)?.use {
                    it.use { stream -> Bitmap.createBitmap(BitmapFactory.decodeStream(stream)) }
                }
                documentSize = file.length()
            }
        }
        isLoading = false
    }
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                val file = uri!!.toFile()
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
            if (bitmap != null) Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(visible = !isLoading) {
            RowVerticalCenterSpaceBetweenFill {
                Column {
                    if (type == HomeworkDocumentType.PDF) Text(
                        text = pluralStringResource(id = R.plurals.homework_detailViewDocumentPages, count = pageCount, pageCount),
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (type == HomeworkDocumentType.JPG) Text(
                        text = bitmap?.width.toString() + "x" + bitmap?.height.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = storageToHumanReadableFormat(documentSize),
                        style = MaterialTheme.typography.labelSmall
                    )
                    VerticalExpandAnimatedAndFadingVisibility(visible = progress != null) {
                        LinearProgressIndicator(
                            progress = { progress ?: 0f },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                    }
                }
                HorizontalExpandAnimatedAndFadingVisibility(visible = isEditing) {
                    IconButton(onClick = onRemove) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DocumentRecordPreview() {
    DocumentRecord(null, HomeworkDocumentType.PDF, 0.3f, false)
}

@Composable
@Preview(showBackground = true)
private fun DocumentRecordEditingPreview() {
    DocumentRecord(null, HomeworkDocumentType.PDF, null, true)
}