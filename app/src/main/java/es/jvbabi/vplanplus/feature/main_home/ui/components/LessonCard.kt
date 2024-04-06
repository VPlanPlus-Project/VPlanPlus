package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
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
                elevation = 2.dp,
                shape = RoundedCornerShape((12 + 12 * activeModifier.value).dp),
            )
            .clip(RoundedCornerShape((12 + 12 * activeModifier.value).dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = (4+4*activeModifier.value).dp)
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
                        .filter { hw -> hw.until.toLocalDate().isEqual(lesson.start.toLocalDate()) }
                        .map { it.tasks }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (lesson.displaySubject == "-") {
                            Text(
                                text = stringResource(id = R.string.home_activeDayNextLessonCanceled),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.error
                            )
                        } else {
                            Text(
                                text = lesson.displaySubject,
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onSurface
                            )
                        }
                        if (lesson.rooms.isNotEmpty() || booking != null) {
                            Text(
                                text = DOT,
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Icon(
                                imageVector = Icons.Outlined.MeetingRoom,
                                contentDescription = null,
                                tint =
                                if (lesson.roomIsChanged) colorScheme.error
                                else if (booking != null) colorScheme.secondary
                                else colorScheme.onSurface
                            )
                            Text(
                                text = lesson.rooms.joinToString(", ") +
                                        if (booking != null) {
                                            if (lesson.rooms.isNotEmpty()) " ${booking.room.name}" else booking.room.name
                                        } else "",
                                style = MaterialTheme.typography.titleLarge,
                                color =
                                if (lesson.roomIsChanged) colorScheme.error
                                else if (booking != null) colorScheme.secondary
                                else colorScheme.onSurface
                            )
                        }
                        if (lesson.teachers.isNotEmpty()) {
                            if (!(lesson.rooms.isEmpty() || booking != null)) {
                                Text(
                                    text = DOT,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint =
                                if (lesson.teacherIsChanged) colorScheme.error
                                else colorScheme.onSurface
                            )
                            Text(
                                text = lesson.teachers.joinToString(", "),
                                style = MaterialTheme.typography.labelMedium,
                                color =
                                if (lesson.teacherIsChanged) colorScheme.error
                                else colorScheme.onSurface
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, top = (2+4*activeModifier.value).dp, bottom = (2+4*activeModifier.value).dp)
                            .height((1 + 14 * activeModifier.value).dp)
                            .clip(RoundedCornerShape((50 * activeModifier.value).toInt()))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(lesson.progress(time))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape((50 * activeModifier.value).toInt()))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (!lesson.info.isNullOrBlank()) {
                        InfoRow(icon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }) {
                            Text(
                                text = lesson.info,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    if (booking != null) {
                        InfoRow(icon = {
                            Icon(
                                imageVector = Icons.Outlined.MeetingRoom,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }) {
                            Text(
                                text = stringResource(
                                    id = R.string.home_activeBookedBy,
                                    booking.bookedBy?.name ?: "Unknown",
                                    booking.`class`.name
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    if (relevantHomeworkTasks.isNotEmpty()) {
                        InfoRow(icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }) {
                            Expandable(expanded) {
                                relevantHomeworkTasks.forEachIndexed { taskIndex, homeworkTasks ->
                                    val tasksText = buildHomeworkTasksText(homeworkTasks)
                                    Text(text = tasksText, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                    if (taskIndex != relevantHomeworkTasks.lastIndex) HorizontalDivider()
                                }
                            }
                            Expandable(!expanded) {
                                Text(
                                    text = pluralStringResource(R.plurals.home_lessonCardHomework, relevantHomeworkTasks.size, relevantHomeworkTasks.size),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
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

                    if (i != lessons.lastIndex) Spacer(modifier = Modifier.height(8.dp))
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
            text = "$lessonNumber",
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
private fun InfoRow(
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 1.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .padding(end = 4.dp)
                .size(20.dp)
        ) { icon() }
        content()
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

@Preview(showBackground = true)
@Composable
fun LessonCardPreview() {
    LessonCard(
        homework = emptyList(),
        bookings = emptyList(),
        lessons = listOf(
            Lesson(
                vpId = 1,
                lessonNumber = 1,
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now().plusHours(1),
                `class` = ClassesPreview.generateClass(school = null),
                originalSubject = "Math",
                rooms = listOf("A1"),
                teachers = listOf("Mr. Smith"),
                info = "Info",
                changedSubject = null,
                teacherIsChanged = false,
                roomIsChanged = false,
                roomBooking = null
            )
        ),
        time = ZonedDateTime.now()
    )
}