package es.jvbabi.vplanplus.feature.exams.ui.details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun DeleteDialog(
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    YesNoDialog(
        icon = Icons.Default.Delete,
        title = stringResource(R.string.examsDetails_deleteTitle),
        message = stringResource(R.string.examsDetails_deleteMessage),
        onYes = onYes,
        onNo = onNo,
        onDismiss = onNo
    )
}