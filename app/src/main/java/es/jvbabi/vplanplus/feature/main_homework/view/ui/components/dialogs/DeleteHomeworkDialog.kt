package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun DeleteHomeworkDialog(
    homework: Homework,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    YesNoDialog(
        icon = Icons.Default.DeleteForever,
        title = stringResource(id = R.string.homework_deleteHomeworkTitle),
        message = buildDialogMessage(homework),
        onYes = onConfirm,
        onNo = onDismiss,
        onDismiss = onDismiss
    )
}

@Composable
private fun buildDialogMessage(homework: Homework): String {
    return if (homework.id < 0) {
        stringResource(id = R.string.homework_deleteHomeworkTextLocal)
    } else if (homework.isPublic) {
        stringResource(id = R.string.homework_deleteHomeworkTextPublic)
    } else {
        stringResource(id = R.string.homework_deleteHomeworkTextPrivate)
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteHomeworkDialogPreview() {
    val school = School.generateRandomSchools(1).first()
    val `class` = ClassesPreview.generateClass(school)
    val defaultLesson = DefaultLesson(
        teacher = null,
        defaultLessonId = UUID.randomUUID(),
        `class` = `class`,
        subject = "IT",
        vpId = 42
    )
    val createdBy = VppIdPreview.generateVppId(`class`)
    DeleteHomeworkDialog(
        homework = Homework(
            id = 1,
            createdBy = createdBy,
            createdAt = ZonedDateTime.now(),
            defaultLesson = defaultLesson,
            until = ZonedDateTime.now(),
            tasks = listOf(
                HomeworkTask(
                    id = 1,
                    content = "Test 1",
                    done = false,
                ),
                HomeworkTask(
                    id = 1,
                    content = "Test 2",
                    done = true,
                )
            ),
            classes = `class`,
            isPublic = true,
            isHidden = false
        ),
        onConfirm = {},
        onDismiss = {}
    )
}