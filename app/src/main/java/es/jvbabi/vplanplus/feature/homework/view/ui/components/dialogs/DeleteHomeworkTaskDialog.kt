package es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun DeleteHomeworkTaskDialog(
    task: HomeworkTask,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    YesNoDialog(
        icon = Icons.Default.DeleteForever,
        title = stringResource(id = R.string.homework_deleteHomeworkTaskTitle),
        message = buildMessage(task.content),
        onYes = onConfirm,
        onNo = onDismiss,
        onDismiss = onDismiss
    )
}

@Composable
private fun buildMessage(content: String): String {
    return if (content.length < 50) {
        stringResource(id = R.string.homework_deleteHomeworkTaskMessage, content)
    } else {
        stringResource(id = R.string.homework_deleteHomeworkTaskMessage, content.substring(0, 50) + "...")
    }
}

@Composable
@Preview
private fun DeleteHomeworkTaskDialogPreview() {
    var task = ""
    repeat(50) {
        task += "Example $it "
    }
    DeleteHomeworkTaskDialog(
        task = HomeworkTask(
            id = 1,
            done = false,
            content = task
        ),
        onConfirm = {},
        onDismiss = {}
    )
}