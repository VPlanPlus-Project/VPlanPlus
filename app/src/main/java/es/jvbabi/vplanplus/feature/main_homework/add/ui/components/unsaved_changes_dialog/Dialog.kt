package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.unsaved_changes_dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
@Preview
fun Dialog(
    onCancel: () -> Unit = {},
    onDiscard: () -> Unit = {},
) {
    YesNoDialog(
        icon = Icons.Default.WarningAmber,
        title = stringResource(id = R.string.addHomework_unsavedChangesDialogTitle),
        message = stringResource(id = R.string.addHomework_unsavedChangesDialogText),
        no = stringResource(id = android.R.string.cancel),
        yes = stringResource(id = R.string.discard),
        onNo = onCancel,
        onYes = onDiscard
    )
}