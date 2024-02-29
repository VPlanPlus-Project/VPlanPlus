package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LessonDialog(
    lesson: Lesson,
    onCloseLessonDetailDialog: () -> Unit
) {
    var info = lesson.info
    info = if (info == null) "" else "$info\n"
    InfoDialog(
        icon = Icons.Default.School,
        title = lesson.displaySubject + " " + DOT + " " + lesson.`class`.name,
        message = stringResource(
            id = R.string.searchAvailableRoom_lessonDetail,
            lesson.teachers.joinToString(", "),
            lesson.rooms.joinToString(", "),
            info,
            lesson.start.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            lesson.end.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
        ),
        onOk = { onCloseLessonDetailDialog() }
    )
}