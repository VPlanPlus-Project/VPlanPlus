package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.RoomPreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.util.LessonTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun LessonItem(
    lesson: Lesson
) {
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LessonIcon(lesson.displaySubject, (lesson as? Lesson.SubstitutionPlanLesson)?.changedSubject != null)
        Column {
            LessonTitle(
                regularSubject = (lesson as? Lesson.SubstitutionPlanLesson)?.defaultLesson?.subject ?: lesson.subject,
                changedSubject = (lesson as? Lesson.SubstitutionPlanLesson)?.changedSubject,
                rooms = (lesson as? Lesson.SubstitutionPlanLesson)?.rooms?.map { it.name } ?: emptyList(),
                isRoomsChanged = (lesson as? Lesson.SubstitutionPlanLesson)?.roomIsChanged ?: false,
                booking = (lesson as? Lesson.SubstitutionPlanLesson)?.roomBooking
            )
            LessonSubtitle(
                start = lesson.start,
                end = lesson.end,
                teachers = lesson.teachers,
                isTeachersChanged = (lesson as? Lesson.SubstitutionPlanLesson)?.teacherIsChanged ?: false
            )
        }
    }
}

@Composable
private fun LessonIcon(
    subject: String,
    isChanged: Boolean,
) {
    SubjectIcon(
        subject = subject,
        tint = if (isChanged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp)
    )
}

@Composable
private fun LessonTitle(
    regularSubject: String,
    changedSubject: String?,
    rooms: List<String>,
    isRoomsChanged: Boolean,
    booking: RoomBooking?,
) {
    RowVerticalCenter(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val titleFont = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        ).toSpanStyle()

        val defaultStyle = MaterialTheme.typography.bodyMedium.toSpanStyle()
        val defaultErrorStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.error
        ).toSpanStyle()
        val defaultSecondaryStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.secondary
        ).toSpanStyle()

        val text = buildAnnotatedString {
            withStyle(defaultStyle) {
                withStyle(titleFont) {
                    if (changedSubject === "-") append(stringResource(id = R.string.home_activeDayNextLessonCanceled, regularSubject))
                    else append(changedSubject ?: regularSubject)
                }
                append(" ")
                withStyle(if (isRoomsChanged) defaultErrorStyle else defaultStyle) {
                    append(rooms.joinToString(", "))
                }
                if (booking != null) withStyle(defaultSecondaryStyle) {
                    if (rooms.isNotEmpty()) append(", ")
                    append(booking.room.name)
                }
            }
        }

        Text(text)
    }
}

@Composable
private fun LessonSubtitle(
    start: ZonedDateTime,
    end: ZonedDateTime,
    teachers: List<Teacher>,
    isTeachersChanged: Boolean,
) {
    val defaultStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
    val defaultErrorStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.error
    ).toSpanStyle()

    val text = buildAnnotatedString {
        withStyle(defaultStyle) {
            append(start.format(DateTimeFormatter.ofPattern("HH:mm")))
            append(" - ")
            append(end.format(DateTimeFormatter.ofPattern("HH:mm")))
        }
        if (teachers.isNotEmpty()) {
            withStyle(if (isTeachersChanged) defaultErrorStyle else defaultStyle) {
                append(" $DOT ")
                append(teachers.joinToString(", ") { it.acronym })
            }
        }
    }

    Text(text)
}

@Composable
@Preview
private fun LessonItemPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val lessonTimes = LessonTime.fallbackTime(group.groupId, 1)

    val subject1 = "Math"
    val teacher1 = TeacherPreview.teacher(school)
    val room1 = RoomPreview.generateRoom(school)

    LessonItem(
        lesson = Lesson.SubstitutionPlanLesson(
            id = UUID.randomUUID(),
            subject = subject1,
            start = lessonTimes.start,
            end = lessonTimes.end,
            teachers = listOf(teacher1),
            rooms = listOf(room1),
            defaultLesson = DefaultLesson(UUID.randomUUID(), 1, subject1, teacher1, group, null),
            lessonNumber = 1,
            info = "Info",
            week = null,
            teacherIsChanged = false,
            roomIsChanged = true,
            roomBooking = null,
            group = group,
            weekType = null,
            changedSubject = null
        ),
    )
}