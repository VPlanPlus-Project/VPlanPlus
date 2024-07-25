package es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Modal
import es.jvbabi.vplanplus.ui.common.ModalOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddImageModel(
    sheetState: SheetState,
    onOpenGallery: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Modal(
        sheetState = sheetState,
        entries = listOf(
            ModalOption(
                title = stringResource(id = R.string.addHomework_addImageGalleryTitle),
                subtitle = stringResource(id = R.string.addHomework_addImageGallerySubtitle),
                icon = Icons.Default.Image,
                isEnabled = true,
                isSelected = false,
                onClick = { onDismiss(); onOpenGallery() }
            ),
            ModalOption(
                title = stringResource(id = R.string.addHomework_addImageCameraTitle),
                subtitle = stringResource(id = R.string.addHomework_addImageCameraSubtitle),
                icon = Icons.Default.AddAPhoto,
                isEnabled = true,
                isSelected = false,
                onClick = { onDismiss(); onOpenCamera() }
            )
        ),
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentModal(
    sheetState: SheetState,
    onScanDocument: () -> Unit = {},
    onChooseDocument: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Modal(
        sheetState = sheetState,
        entries = listOf(
            ModalOption(
                title = stringResource(id = R.string.addHomework_addDocumentChooseDocumentTitle),
                subtitle = stringResource(id = R.string.addHomework_addDocumentChooseDocumentSubtitle),
                icon = Icons.Default.FileOpen,
                isEnabled = true,
                isSelected = false,
                onClick = { onDismiss(); onChooseDocument() }
            ),
            ModalOption(
                title = stringResource(id = R.string.addHomework_addDocumentScanDocumentTitle),
                subtitle = stringResource(id = R.string.addHomework_addDocumentScanDocumentSubtitle),
                icon = Icons.Default.DocumentScanner,
                isEnabled = true,
                isSelected = false,
                onClick = { onDismiss(); onScanDocument() }
            )
        ),
        onDismiss = onDismiss
    )
}