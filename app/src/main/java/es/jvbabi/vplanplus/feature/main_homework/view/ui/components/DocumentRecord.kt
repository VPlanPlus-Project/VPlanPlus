package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.storageToHumanReadableFormat

@Composable
fun DocumentRecord(
    uri: Uri?,
) {
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    var bitmap = remember<Bitmap?> { null }
    var pageCount by remember { mutableIntStateOf(0) }
    var documentSize by remember { mutableLongStateOf(0) }
    LaunchedEffect(key1 = uri) {
        if (uri != null) {
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
                intent.setDataAndType(newUri, "application/pdf")
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
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            if (bitmap != null) Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        AnimatedVisibility(visible = !isLoading) {
            Column {
                Text(
                    text = pluralStringResource(id = R.plurals.homework_detailViewDocumentPages, count = pageCount, pageCount),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = storageToHumanReadableFormat(documentSize),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DocumentRecordPreview() {
    DocumentRecord(null)
}