package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.AddDocumentModal
import es.jvbabi.vplanplus.feature.main_homework.shared.ui.add_document_drawer.AddImageModel
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DocumentUpdate
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record.DocumentRecord
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.common.buildUri
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Documents(
    documents: List<HomeworkDocument>,
    changedDocuments: List<DocumentUpdate.EditedDocument>,
    newDocuments: Map<DocumentUpdate.NewDocument, Float?>,
    markedAsRemoveIds: List<Int>,
    isEditing: Boolean,
    onRename: (updated: DocumentUpdate) -> Unit,
    onRemove: (removed: DocumentUpdate) -> Unit,
    onPickPhotoClicked: () -> Unit,
    onTakePhotoClicked: () -> Unit,
    onPickDocumentClicked: () -> Unit,
    onScanDocumentClicked: () -> Unit
) {
    var isAddImageDrawerOpen by rememberSaveable { mutableStateOf(false) }
    val addImageDrawerSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (isAddImageDrawerOpen) {
        AddImageModel(
            sheetState = addImageDrawerSheetState,
            onOpenGallery = { isAddImageDrawerOpen = false; onPickPhotoClicked() },
            onOpenCamera = { isAddImageDrawerOpen = false; onTakePhotoClicked() },
            onDismiss = { isAddImageDrawerOpen = false }
        )
    }

    var isAddDocumentDrawerOpen by rememberSaveable { mutableStateOf(false) }
    val addDocumentDrawerSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (isAddDocumentDrawerOpen) {
        AddDocumentModal(
            sheetState = addDocumentDrawerSheetState,
            onChooseDocument = { isAddDocumentDrawerOpen = false; onPickDocumentClicked() },
            onScanDocument = { isAddDocumentDrawerOpen = false; onScanDocumentClicked() },
            onDismiss = { isAddDocumentDrawerOpen = false }
        )
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.homework_detailViewDocumentsTitle),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (documents.isEmpty() && newDocuments.isEmpty()) {
            RowVerticalCenter(
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.homework_detailViewDocumentsNoDocuments),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        } else {
            documents
                .filter { document -> document.documentId !in markedAsRemoveIds }
                .forEach { document ->
                    val uri = document.buildUri()
                    val editedDocument = changedDocuments.firstOrNull { it.documentId == document.documentId }
                    DocumentRecord(
                        uri = uri,
                        type = document.type,
                        name = document.name,
                        newName = editedDocument?.name,
                        isEditing = isEditing,
                        onRename = { to -> onRename(DocumentUpdate.EditedDocument(uri, to, document.documentId)) },
                        onRemove = { onRemove(DocumentUpdate.EditedDocument(uri, documentId = document.documentId)) }
                    )
                }
        }

        newDocuments.forEach { (document, progress) ->
            DocumentRecord(
                uri = document.uri,
                type = HomeworkDocumentType.fromExtension(document.extension),
                progress = progress,
                name = document.name,
                isEditing = isEditing,
                onRename = { to -> onRename(DocumentUpdate.NewDocument(document.uri, to, document.extension)) },
                onRemove = { onRemove(document) }
            )
        }

        VerticalExpandVisibility(visible = isEditing) {
            RowVerticalCenter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentedButtons {
                    SegmentedButtonItem(
                        selected = false,
                        onClick = { isAddImageDrawerOpen = true },
                        icon = {
                            Icon(imageVector = Icons.Outlined.AddPhotoAlternate, contentDescription = null)
                        },
                        label = { Text(text = stringResource(id = R.string.addHomework_addImage)) }
                    )
                    SegmentedButtonItem(
                        selected = false,
                        onClick = { isAddDocumentDrawerOpen = true },
                        icon = {
                            Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null)
                        },
                        label = { Text(text = stringResource(id = R.string.addHomework_addDocument)) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NoDocumentsPreview() {
    Documents(
        documents = emptyList(),
        newDocuments = emptyMap(),
        changedDocuments = emptyList(),
        markedAsRemoveIds = emptyList(),
        isEditing = true,
        onRename = {},
        onRemove = {},
        onPickPhotoClicked = {},
        onTakePhotoClicked = {},
        onPickDocumentClicked = {},
        onScanDocumentClicked = {}
    )
}