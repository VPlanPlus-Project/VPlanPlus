package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DocumentUpdate
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record.DocumentRecord
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter

@Composable
fun Documents(
    documents: List<HomeworkDocument>,
    markedAsRemoveUris: List<Uri>,
    isEditing: Boolean,
    onRename: (updated: DocumentUpdate) -> Unit,
    onRemove: (removed: DocumentUpdate) -> Unit
) {
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
        if (documents.isEmpty()) {
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
                .filter { document -> document.uri !in markedAsRemoveUris }
                .forEach { document ->
                    DocumentRecord(
                        uri = document.uri,
                        type = document.type,
                        name = document.name,
                        isEditing = isEditing,
                        onRename = { to -> onRename(DocumentUpdate.EditedDocument(document.uri, to)) },
                        onRemove = { onRemove(DocumentUpdate.EditedDocument(document.uri)) }
                    )
                }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NoDocumentsPreview() {
    Documents(
        documents = emptyList(),
        markedAsRemoveUris = emptyList(),
        isEditing = false,
        onRename = {},
        onRemove = {}
    )
}