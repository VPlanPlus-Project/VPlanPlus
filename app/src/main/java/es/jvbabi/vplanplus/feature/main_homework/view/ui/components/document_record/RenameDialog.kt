package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.document_record

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    val name by rememberSaveable(currentValue) {
        mutableStateOf(
            if ("." !in currentValue.orEmpty()) currentValue.orEmpty()
            else currentValue?.split(".")?.dropLast(1)?.joinToString(".") ?: ""
        )
    }

    val extension by rememberSaveable(currentValue) {
        mutableStateOf(
            if ("." !in currentValue.orEmpty()) ""
            else currentValue?.split(".")?.lastOrNull() ?: ""
        )
    }
    InputDialog(
        icon = Icons.Default.Edit,
        title = stringResource(id = R.string.homework_detailViewEditRenameDialogTitle),
        value = name,
        selectContent = true,
        postfixText = ".$extension",
        placeholder = stringResource(id = R.string.homework_detailViewEditRenameDialogPlaceholder),
        message = stringResource(id = R.string.homework_detailViewEditRenameDialogMessage),
        onOk = {
            if (it.isNullOrBlank()) onDismiss()
            else onOk("$it.$extension")
        }
    )
}

@Composable
@Preview
private fun RenameDialogPreview() {
    RenameDialog(currentValue = "checklist.pdf")
}