package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LessonCard(
    modifier: Modifier = Modifier,
    homework: List<Homework>,
    bookings: List<RoomBooking>,
    onAddHomeworkClicked: (vpId: Long?) -> Unit = {},
    onBookRoomClicked: () -> Unit = {},
    lessons: List<Lesson>,
    time: ZonedDateTime,
    allowActions: Boolean = true,
    profileType: ProfileType
) {
    val colorScheme = MaterialTheme.colorScheme
    var expanded by rememberSaveable {
        mutableStateOf(lessons.any { it.progress(time) in 0.0..<1.0 })
    }

    LaunchedEffect(time) {
        expanded = lessons.any { it.progress(time) in 0.0..<1.0 }
    }

    val activeModifier = animateFloatAsState(
        targetValue = if (lessons.any { it.progress(time) in 0.0..<1.0 }) 1f else 0f,
        animationSpec = tween(300),
        label = "activeModifier"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = (4 * activeModifier.value).dp,
                shape = RoundedCornerShape((8 + 8 * activeModifier.value).dp),
            )
            .padding(top = (4 * activeModifier.value).dp, bottom = (4 * activeModifier.value).dp)
            .clip(RoundedCornerShape((8 + 8 * activeModifier.value).dp))
            .drawWithContent {
                drawRect(
                    color = colorScheme.surfaceVariant,
                    topLeft = Offset(0f, 0f),
                    size = size
                )
                drawRect(
                    color = colorScheme.surfaceContainer,
                    topLeft = Offset(0f, 0f),
                    size = Size(
                        this.size.width * lessons
                            .first()
                            .progress(time), this.size.height
                    )
                )
                drawContent()
            }
    ) {
        Expandable(lessons.any { it.progress(time) in 0.0..<1.0 }) {
            Text(
                text = pluralStringResource(id = R.plurals.homeLesson_current, lessons.size),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
        Expandable(lessons.none { it.progress(time) in 0.0..<1.0 }) {
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            Modifier
                .padding(start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LessonNumberAndTime(
                lessons.first().lessonNumber,
                lessons.first().start,
                lessons.first().end
            )
            Column {
                lessons.forEachIndexed { i, lesson ->
                    val booking = bookings
                        .firstOrNull {
                            it.from.isEqual(lesson.start) && it.to.isEqual(lesson.end)
                        }
                    val relevantHomeworkTasks = homework
                        .filter { hw -> hw.defaultLesson.vpId == lesson.vpId }
                        .filter { hw -> hw.until.toLocalDate().isEqual(time.toLocalDate()) }
                        .map { it.tasks }

                    Text(text = buildHeaderText(lesson, booking, profileType))

                    if (!lesson.info.isNullOrBlank()) Text(
                        text = lesson.info,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Expandable(expanded) {
                        if (booking != null) Text(
                            text = stringResource(
                                id = R.string.home_activeBookedBy,
                                booking.bookedBy?.name ?: "Unknown",
                                booking.`class`.name
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                        relevantHomeworkTasks.forEachIndexed { taskIndex, homeworkTasks ->
                            val tasksText = buildHomeworkTasksText(homeworkTasks)
                            Text(text = tasksText, style = MaterialTheme.typography.labelMedium)
                            if (taskIndex != relevantHomeworkTasks.lastIndex) HorizontalDivider()
                        }
                    }

                    Expandable(!expanded && relevantHomeworkTasks.isNotEmpty()) {
                        Text(
                            text = pluralStringResource(R.plurals.home_lessonCardHomework, relevantHomeworkTasks.size, relevantHomeworkTasks.size),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Expandable(expanded && allowActions) {
                        LazyRow {
                            if (lesson.rooms.isEmpty() && booking == null) item {
                                AssistChip(
                                    onClick = onBookRoomClicked,
                                    label = { Text(text = stringResource(id = R.string.home_activeBookRoom)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.MeetingRoom, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            item {
                                AssistChip(
                                    onClick = { onAddHomeworkClicked(lesson.vpId) },
                                    label = { Text(text = stringResource(id = R.string.home_addHomeworkLabel)) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Default.MenuBook, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }

                    if (i != lessons.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 8.dp, bottom = 6.dp, top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonNumberAndTime(lessonNumber: Int, start: ZonedDateTime, end: ZonedDateTime) {
    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$lessonNumber.",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = start.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = end.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun Expandable(isExpanded: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {
        content()
    }
}

@Composable
private fun buildHeaderText(lesson: Lesson, booking: RoomBooking?, profileType: ProfileType) = buildAnnotatedString {
    val defaultStyle =
        MaterialTheme
            .typography
            .titleMedium
            .toSpanStyle()
    withStyle(
        defaultStyle
            .copy(
                color = MaterialTheme.colorScheme.onSurface
            )
    ) {
        if (profileType != ProfileType.STUDENT) {
            append(lesson.`class`.name)
            append(" $DOT ")
        }
        if (lesson.subjectIsChanged) {
            withStyle(
                defaultStyle.copy(
                    color = MaterialTheme.colorScheme.error
                )
            ) {
                if (lesson.displaySubject == "-") append(
                    stringResource(
                        id = R.string.home_activeDayNextLessonCanceled,
                    )
                )
                else append(lesson.displaySubject)
            }
        } else {
            if (lesson.displaySubject == "-") append(
                stringResource(
                    id = R.string.home_activeDayNextLessonCanceled,
                )
            )
            else append(lesson.displaySubject)
        }

        if (lesson.rooms.isEmpty() && booking != null) {
            append(" ")
            append(DOT)
            append(" ")
            withStyle(
                defaultStyle.copy(
                    color = MaterialTheme.colorScheme.tertiary
                )
            ) {
                append(
                    stringResource(id = R.string.home_lessonCardBooking,
                        booking.room.name)
                )
            }
        }

        if (lesson.displaySubject == "-") return@buildAnnotatedString

        if (lesson.rooms.isNotEmpty()) {
            append(" ")
            append(DOT)
            append(" ")
            if (lesson.roomIsChanged) {
                withStyle(
                    defaultStyle.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                ) {
                    append(lesson.rooms.joinToString(", "))
                }
            } else append(lesson.rooms.joinToString(", "))
        }

        if (lesson.teachers.isNotEmpty()) {
            append(" ")
            append(DOT)
            append(" ")
            if (lesson.teacherIsChanged) {
                withStyle(
                    defaultStyle.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                ) {
                    append(lesson.teachers.joinToString(", "))
                }
            } else append(lesson.teachers.joinToString(", "))
        }
    }
}

@Composable
private fun buildHomeworkTasksText(tasks: List<HomeworkTask>) = buildAnnotatedString {
    if (tasks.isNotEmpty()) {
        append(stringResource(id = R.string.homework_title))
        append("\n")
    }
    tasks.forEachIndexed { i, task ->
        append("   $DOT ")
        if (task.done) withStyle(style = SpanStyle(textDecoration = TextDecoration.Companion.LineThrough)) {
            append(task.content)
        } else append(task.content)
        if (i != tasks.lastIndex) append("\n")
    }
}