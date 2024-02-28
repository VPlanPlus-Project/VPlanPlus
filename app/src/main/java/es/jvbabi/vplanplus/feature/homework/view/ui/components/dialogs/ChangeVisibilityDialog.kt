package es.jvbabi.vplanplus.feature.homework.view.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun ChangeVisibilityDialog(
    homework: Homework,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    YesNoDialog(
        icon = Icons.Default.Share,
        title = buildTitleText(homework),
        message = buildMessageText(homework),
        onYes = onConfirm,
        onNo = onDismiss,
        onDismiss = onDismiss
    )
}

@Composable
private fun buildTitleText(homework: Homework): String {
    if (homework.isPublic) {
        return stringResource(id = R.string.homework_changeVisibilityTitleToPrivate)
    }
    return stringResource(id = R.string.homework_changeVisibilityTitleToPublic)
}

@Composable
private fun buildMessageText(homework: Homework): String {
    if (homework.isPublic) {
        return stringResource(id = R.string.homework_changeVisibilityTextToPrivate)
    }
    return stringResource(id = R.string.homework_changeVisibilityTextToPublic)
}

@Composable
@Preview(showBackground = true)
private fun ChangeVisibilityDialogPreview() {
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
    ChangeVisibilityDialog(
        homework = Homework(
            id = 1,
            createdBy = createdBy,
            createdAt = LocalDateTime.now(),
            defaultLesson = defaultLesson,
            until = LocalDate.now(),
            tasks = listOf(
                HomeworkTask(
                    id = 1,
                    content = "Test 1",
                    done = false,
                    individualId = null
                ),
                HomeworkTask(
                    id = 1,
                    content = "Test 2",
                    done = true,
                    individualId = null
                )
            ),
            classes = `class`,
            isPublic = true
        ),
        onConfirm = {},
        onDismiss = {}
    )
}