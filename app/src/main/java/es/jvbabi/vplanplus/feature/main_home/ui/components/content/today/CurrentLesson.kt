package es.jvbabi.vplanplus.feature.main_home.ui.components.content.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.orUnknown
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.util.DateUtils.progress
import es.jvbabi.vplanplus.util.toDp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CurrentLesson(
    lesson: Lesson,
    homeworkForLesson: List<PersonalizedHomework>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {  }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(12.dp)
        ) {
            val lessonSubjectIsChanged = lesson is Lesson.SubstitutionPlanLesson && lesson.changedSubject != null
            SubjectIcon(
                lesson.subject,
                tint = if (lessonSubjectIsChanged) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (lessonSubjectIsChanged) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer)
                    .padding(6.dp)
            )
            Column(Modifier.weight(1f)) {
                RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        when (lesson) {
                            is Lesson.TimetableLesson -> lesson.subject
                            is Lesson.SubstitutionPlanLesson -> if (lesson.changedSubject == "-") stringResource(
                                R.string.home_activeDayNextLessonCanceled,
                                lesson.defaultLesson?.subject ?: ""
                            ) else lesson.changedSubject ?: lesson.subject
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                    Text(
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = MaterialTheme.typography.bodySmall.fontSize
                        ),
                        text = buildAnnotatedString {
                            val defaultStyle = MaterialTheme.typography.bodySmall
                                .copy(lineHeight = MaterialTheme.typography.bodySmall.fontSize)
                                .toSpanStyle()
                            val defaultSecondaryStyle = defaultStyle.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )

                            withStyle(defaultStyle) {
                                append(lesson.rooms.joinToString(", ") { it.name })

                                if (lesson is Lesson.SubstitutionPlanLesson && lesson.roomBooking != null) {
                                    if (lesson.rooms.isNotEmpty()) append(", ")
                                    withStyle(defaultSecondaryStyle) {
                                        append(lesson.roomBooking.room.name)
                                    }
                                }
                            }
                        }
                    )
                }
                Text(
                    style = MaterialTheme.typography.labelMedium.copy(
                        lineHeight = MaterialTheme.typography.labelMedium.fontSize
                    ),
                    text = buildAnnotatedString {
                        val defaultStyle = MaterialTheme.typography.labelMedium
                            .copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Light,
                                lineHeight = MaterialTheme.typography.labelMedium.fontSize
                            )
                            .toSpanStyle()

                        withStyle(defaultStyle) {
                            append(lesson.start.format(DateTimeFormatter.ofPattern("HH:mm")))
                            append(" - ")
                            append(lesson.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                            if (lesson.teachers.isNotEmpty()) append(" $DOT ")
                            append(lesson.teachers.joinToString(", ") { it.acronym })
                        }
                    }
                )
                if ((lesson is Lesson.SubstitutionPlanLesson && (lesson.info != null || lesson.roomBooking != null)) || homeworkForLesson.isNotEmpty()) {
                    Spacer4Dp()
                    if (lesson is Lesson.SubstitutionPlanLesson && lesson.info != null) {
                        RowVerticalCenter {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .size(MaterialTheme.typography.bodySmall.lineHeight.toDp())
                            )
                            Text(
                                text = lesson.info,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    if (lesson is Lesson.SubstitutionPlanLesson && lesson.roomBooking != null) {
                        RowVerticalCenter {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .size(MaterialTheme.typography.bodySmall.lineHeight.toDp())
                            )
                            Text(
                                text = stringResource(R.string.home_activeBookedBy, lesson.roomBooking.bookedBy?.name.orUnknown()),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    if (homeworkForLesson.isNotEmpty()) {
                        RowVerticalCenter {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .size(MaterialTheme.typography.bodySmall.lineHeight.toDp())
                            )
                            homeworkForLesson.forEach { homework ->
                                Text(
                                    text = homework.tasks.joinToString(", ") { it.content },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(
                    ZonedDateTime
                        .now()
                        .progress(lesson.start, lesson.end)
                )
                .height(2.dp)
                .clip(RoundedCornerShape(50, 50, 0, 0))
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CurrentLessonPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(group)
    val homework = PersonalizedHomework.LocalHomework(
        homework = HomeworkCore.LocalHomework(
            id = -1,
            defaultLesson = null,
            tasks = listOf(HomeworkTaskCore(id = -1, homeworkId = -1, content = "Task A")),
            until = ZonedDateTime.now(),
            profile = profile,
            documents = emptyList(),
            createdAt = ZonedDateTime.now().minusDays(1L)
        ),
        profile = profile,
        tasks = listOf(HomeworkTaskDone(id = -1, homeworkId = -1, isDone = false, content = "Task A")),
    )
    CurrentLesson(Lessons.generateLessons(1, true).first(), listOf(homework))
}