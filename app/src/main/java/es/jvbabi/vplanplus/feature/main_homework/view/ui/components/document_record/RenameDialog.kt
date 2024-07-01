package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InputDialog

@Composable
@Preview
fun RenameDialog(
    currentValue: String? = null,
    onOk: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    InputDialog(
        icon = Icons.Default.Edit,
        title = stringResource(id = R.string.homework_detailViewEditRenameDialogTitle),
        value = currentValue,
        placeholder = stringResource(id = R.string.homework_detailViewEditRenameDialogPlaceholder),
        message = stringResource(id = R.string.homework_detailViewEditRenameDialogMessage),
        onOk = {
            if (it.isNullOrBlank()) onDismiss()
            else onOk(it)
        }
    )
}