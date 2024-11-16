package es.jvbabi.vplanplus.feature.settings.profile.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
@Preview
fun ConfirmAssessmentsDisableDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    YesNoDialog(
        icon = Icons.Default.TaskAlt,
        title = stringResource(id = R.string.profileSettings_disableAssessmentsTitle),
        message = stringResource(id = R.string.profileSettings_disableAssessmentsMessage),
        onYes = onConfirm,
        onNo = onDismiss
    )
}