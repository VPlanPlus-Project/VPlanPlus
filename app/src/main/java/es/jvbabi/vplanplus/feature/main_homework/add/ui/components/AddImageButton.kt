package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Modal
import es.jvbabi.vplanplus.ui.common.ModalOption
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun AddImageButton(
    onOpenCamera: () -> Unit = {},
    onOpenGallery: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    var isOpen by rememberSaveable { mutableStateOf(false) }
    val onHide = { scope.launch { sheetState.hide(); isOpen = false } }
    if (isOpen) Modal(
        sheetState = sheetState,
        entries = listOf(
            ModalOption(
                title = stringResource(id = R.string.addHomework_addImageGalleryTitle),
                subtitle = stringResource(id = R.string.addHomework_addImageGallerySubtitle),
                icon = Icons.Default.Image,
                isEnabled = true,
                isSelected = false,
                onClick = { onHide(); onOpenGallery() }
            ),
            ModalOption(
                title = stringResource(id = R.string.addHomework_addImageCameraTitle),
                subtitle = stringResource(id = R.string.addHomework_addImageCameraSubtitle),
                icon = Icons.Default.AddAPhoto,
                isEnabled = true,
                isSelected = false,
                onClick = { onHide(); onOpenCamera() }
            )
        ),
        onDismiss = { isOpen = false }
    )
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { isOpen = true },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(id = R.string.addHomework_addImage),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun AddDocumentButton(
    onScanDocument: () -> Unit = {},
    onChooseDocument: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    var isOpen by rememberSaveable { mutableStateOf(false) }
    val onHide = { scope.launch { sheetState.hide(); isOpen = false } }
    if (isOpen) Modal(
        sheetState = sheetState,
        entries = listOf(
            ModalOption(
                title = stringResource(id = R.string.addHomework_addDocumentChooseDocumentTitle),
                subtitle = stringResource(id = R.string.addHomework_addDocumentChooseDocumentSubtitle),
                icon = Icons.Default.FileOpen,
                isEnabled = true,
                isSelected = false,
                onClick = { onHide(); onChooseDocument() }
            ),
            ModalOption(
                title = stringResource(id = R.string.addHomework_addDocumentScanDocumentTitle),
                subtitle = stringResource(id = R.string.addHomework_addDocumentScanDocumentSubtitle),
                icon = Icons.Default.DocumentScanner,
                isEnabled = true,
                isSelected = false,
                onClick = { onHide(); onScanDocument() }
            )
        ),
        onDismiss = { isOpen = false }
    )
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { isOpen = true },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(id = R.string.addHomework_addDocument),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}