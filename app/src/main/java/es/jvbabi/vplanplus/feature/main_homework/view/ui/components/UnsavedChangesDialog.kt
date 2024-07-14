package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
@Preview
fun UnsavedChangesDialog(
    onDismiss: () -> Unit = {},
    onDiscardChanges: () -> Unit = {},
) {
    YesNoDialog(
        icon = Icons.Default.WarningAmber,
        title = stringResource(id = R.string.homework_detailViewUnsavedChanges),
        message = stringResource(id = R.string.homework_detailViewUnsavedChangesText),
        no = stringResource(id = R.string.back),
        yes = stringResource(id = R.string.discard_changes),
        onNo = onDismiss,
        onYes = { onDismiss(); onDiscardChanges() }
    )
}