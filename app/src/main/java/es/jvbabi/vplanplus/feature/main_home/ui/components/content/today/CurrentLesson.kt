package es.jvbabi.vplanplus.feature.main_home.ui.components.content.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CurrentLesson(lesson: Lesson) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {  }
    ) {
        RowVerticalCenter(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            val lessonSubjectIsChanged = lesson is Lesson.SubstitutionPlanLesson && lesson.changedSubject != null
            val lessonRoomIsChanged = lesson is Lesson.SubstitutionPlanLesson && lesson.roomIsChanged
            val lessonTeacherIsChanged = lesson is Lesson.SubstitutionPlanLesson && lesson.teacherIsChanged
            SubjectIcon(
                lesson.subject,
                tint = if (lessonSubjectIsChanged) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (lessonSubjectIsChanged) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary)
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
                            color = if (lesson is Lesson.SubstitutionPlanLesson && lesson.changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
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
                            val defaultErrorStyle = defaultStyle.copy(
                                color = MaterialTheme.colorScheme.error
                            )
                            val defaultSecondaryStyle = defaultStyle.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )

                            withStyle(defaultStyle) {
                                withStyle(if (lessonRoomIsChanged) defaultErrorStyle else defaultStyle) {
                                    append(lesson.rooms.joinToString(", ") { it.name })
                                }

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
                                color = Color.Gray, fontWeight = FontWeight.Light,
                                lineHeight = MaterialTheme.typography.labelMedium.fontSize
                            )
                            .toSpanStyle()

                        val defaultErrorStyle = defaultStyle
                            .copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Light)

                        withStyle(defaultStyle) {
                            append(lesson.start.format(DateTimeFormatter.ofPattern("HH:mm")))
                            append(" - ")
                            append(lesson.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                            append(" $DOT ")
                        }
                        withStyle(if (lessonTeacherIsChanged) defaultErrorStyle else defaultStyle) {
                            append(lesson.teachers.joinToString(", ") { it.acronym })
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 8.dp)
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
    CurrentLesson(Lessons.generateLessons(1, true).first())
}