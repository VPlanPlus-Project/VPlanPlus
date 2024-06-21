package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.net.toFile

@Composable
@Preview(showBackground = true)
fun DocumentView(
    modifier: Modifier = Modifier,
    uri: Uri = Uri.EMPTY,
    onDelete: () -> Unit = {}
) {
    var isLoading by remember(uri) { mutableStateOf(true) }
    val bitmaps = remember(uri) { mutableStateListOf<Bitmap>() }
    LaunchedEffect(key1 = uri) {
        val file = uri.toFile()
        if (file.extension == "pdf") {
            val pdfRenderer = PdfRenderer(
                ParcelFileDescriptor.open(file, MODE_READ_ONLY)
            )
            for (i in 0 until pdfRenderer.pageCount) {
                pdfRenderer.openPage(i).use {
                    val bitmap = createBitmap(it.width, it.height)
                    it.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps += bitmap
                }
            }
        }
        if (file.extension == "jpg" || file.extension == "jpeg") {
            bitmaps += BitmapFactory.decodeFile(file.path)
        }
        isLoading = false
    }
    Box(
        modifier = modifier
            .height(192.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        bitmaps.forEach { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight()
            )
        }
        FilledTonalIconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onDelete,
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}