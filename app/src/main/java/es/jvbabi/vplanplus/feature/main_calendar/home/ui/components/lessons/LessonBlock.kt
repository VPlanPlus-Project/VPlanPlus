package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.RoomPreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.util.LessonTime
import java.util.UUID

@Composable
fun LessonBlock(
    lessonNumber: Int,
    lessons: List<Lesson>,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
    ) {
        Column {
            lessons.forEachIndexed { i, lesson ->
                LessonItem(lesson)
                if (i != lessons.lastIndex) HorizontalDivider(Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 8.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp)
        ) {
            LessonNumber(lessonNumber)
        }
    }
}

@Composable
private fun LessonNumber(
    lessonNumber: Int
) {
    Text(
        text = stringResource(
            id = R.string.calendar_dayLessonNumber,
            lessonNumber.toLocalizedString()
        ),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
@Preview(showBackground = true)
private fun LessonBlockPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val lessonTimes = LessonTime.fallbackTime(group.groupId, 1)

    val subject1 = "Math"
    val teacher1 = TeacherPreview.teacher(school)
    val room1 = RoomPreview.generateRoom(school)

    val subject2 = "English"
    val teacher2 = TeacherPreview.teacher(school)
    val room2 = RoomPreview.generateRoom(school)
    LessonBlock(
        lessonNumber = 1,
        lessons = listOf(
            Lesson.SubstitutionPlanLesson(
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
            Lesson.SubstitutionPlanLesson(
                id = UUID.randomUUID(),
                subject = subject2,
                start = lessonTimes.start,
                end = lessonTimes.end,
                teachers = listOf(teacher2),
                rooms = listOf(room2),
                defaultLesson = DefaultLesson(UUID.randomUUID(), 1, subject2, teacher2, group, null),
                lessonNumber = 1,
                info = "Info",
                week = null,
                teacherIsChanged = false,
                roomIsChanged = false,
                roomBooking = null,
                group = group,
                weekType = null,
                changedSubject = null
            )
        )
    )
}