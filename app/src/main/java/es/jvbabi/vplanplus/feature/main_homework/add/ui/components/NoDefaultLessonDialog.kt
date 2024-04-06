package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun NoDefaultLessonDialog(
    onYes: () -> Unit = {},
    onNo: () -> Unit = {}
) {
    YesNoDialog(
        icon = Icons.Outlined.Warning,
        title = stringResource(id = R.string.addHomework_noDefaultLessonDialogTitle),
        message = stringResource(id = R.string.addHomework_noDefaultLessonDialogText),
        onYes = onYes,
        onNo = onNo
    )
}

@Preview(showBackground = true)
@Composable
private fun NoDefaultLessonDialogPreview() {
    NoDefaultLessonDialog()
}