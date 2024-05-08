package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
private fun headerItemHeight() = MaterialTheme.typography.headlineSmall.lineHeight.value.dp

@OptIn(ExperimentalLayoutApi::class)
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
    displayType: ProfileType
) {

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
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        lessons.forEachIndexed { i, lesson ->
            val relevantHomeworkTasks = homework
                .filter { hw -> hw.defaultLesson?.vpId == lesson.vpId }
                .filter { hw -> hw.until.toLocalDate().isEqual(lesson.start.toLocalDate()) }
                .map { it.tasks }

            val booking = bookings.firstOrNull { it.from.isEqual(lesson.start) && it.to.isEqual(lesson.end) }

            FlowRow(
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) header@{
                SubjectText(lesson.displaySubject, lesson.changedSubject != null)
                RoomText(lesson.rooms, booking, changed = lesson.roomIsChanged)
                TeacherText(lesson.teachers, changed = lesson.teacherIsChanged)
                if (displayType != ProfileType.STUDENT) ClassText(lesson.`class`.name)
            }
            ProgressBar(lesson = lesson, now = time, activeModifier = activeModifier.value)
            Spacer(Modifier.size(8.dp))

            if (!lesson.info.isNullOrBlank()) RowRecord(expand = true, icon = Icons.Outlined.Info, text = lesson.info)
            RowRecord(
                expand = relevantHomeworkTasks.isNotEmpty(),
                icon = Icons.Outlined.ImportContacts
            ) {
                Expandable(expanded) {
                    Column {
                        relevantHomeworkTasks.forEachIndexed { taskIndex, homeworkTasks ->
                            val tasksText = buildHomeworkTasksText(homeworkTasks)
                            Text(text = tasksText, style = MaterialTheme.typography.bodyMedium)
                            if (taskIndex != relevantHomeworkTasks.lastIndex) HorizontalDivider()
                        }
                    }
                }
                Expandable(!expanded) {
                    Text(
                        text = pluralStringResource(R.plurals.home_lessonCardHomework, relevantHomeworkTasks.size, relevantHomeworkTasks.size),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            RowRecord(
                expand = booking != null,
                icon = Icons.Outlined.MeetingRoom,
                text = stringResource(id = R.string.home_activeBookedBy, booking?.bookedBy?.name ?: "Unknown", booking?.`class`?.name ?: "--")
            )

            val showBookRoomAssistChip = lesson.rooms.isEmpty() && booking == null && time.isBefore(lesson.end)
            val showCreateHomeworkAssistChip = expanded
            Expandable(isExpanded = (showBookRoomAssistChip || showCreateHomeworkAssistChip) && allowActions) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(text = stringResource(id = R.string.home_quickActionsTitle), style = MaterialTheme.typography.labelSmall, modifier = Modifier.offset(y = 6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            AnimatedVisibility(
                                visible = showBookRoomAssistChip,
                                enter = expandHorizontally(tween(250)),
                                exit = shrinkHorizontally(tween(250))
                            ) {
                                AssistChip(
                                    onClick = onBookRoomClicked,
                                    label = { Text(text = stringResource(id = R.string.home_activeBookRoom)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.MeetingRoom, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                        item {
                            AnimatedVisibility(
                                visible = showCreateHomeworkAssistChip,
                                enter = expandHorizontally(tween(250)),
                                exit = shrinkHorizontally(tween(250))
                            ) {
                                AssistChip(
                                    onClick = { onAddHomeworkClicked(lesson.vpId) },
                                    label = { Text(text = stringResource(id = R.string.home_addHomeworkLabel)) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Outlined.MenuBook, null)
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (i != lessons.lastIndex) Spacer(modifier = Modifier.height(16.dp))
        }
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
                teachers = listOf("WMA"),
                info = "This is an information about this lesson.\nIt also supports multiline.",
                changedSubject = null,
                teacherIsChanged = false,
                roomIsChanged = false,
                roomBooking = null
            )
        ),
        displayType = ProfileType.TEACHER,
        time = ZonedDateTime.now()
    )
}

@Composable
private fun SubjectText(subject: String, changed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(headerItemHeight())
    ) {
        SubjectIcon(subject = subject, modifier = Modifier.size(24.dp))
        Text(
            text =
            if (subject == "-") stringResource(id = R.string.home_activeDayNextLessonCanceled)
            else subject,
            color = if (changed || subject == "-") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun RoomText(rooms: List<String>, booking: RoomBooking?, changed: Boolean) {
    val style = MaterialTheme.typography.labelLarge.toSpanStyle()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(headerItemHeight())
    ) {
        Icon(
            imageVector = if (rooms.isNotEmpty()) Icons.Outlined.LocationOn else Icons.Outlined.LocationOff,
            contentDescription = null,
            tint = if (!changed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        val text = buildAnnotatedString {
            withStyle(style) {
                rooms.forEachIndexed { i, room ->
                    if (changed) withStyle(style.copy(color = MaterialTheme.colorScheme.error)) { append(room) }
                    else append(room)
                    if (i != rooms.lastIndex) append(", ")
                }
                if (rooms.isNotEmpty() && booking != null) append(", ")
                if (booking != null) withStyle(style.copy(color = MaterialTheme.colorScheme.tertiary)) { append(booking.room.name) }
            }
        }
        Text(text = text)
    }
}

@Composable
private fun TeacherText(teachers: List<String>, changed: Boolean) {
    val style = MaterialTheme.typography.labelLarge.toSpanStyle()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(headerItemHeight())
    ) {
        Icon(
            imageVector = if (teachers.isNotEmpty()) Icons.Outlined.Person else Icons.Outlined.PersonOff,
            contentDescription = null,
            tint = if (!changed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        val text = buildAnnotatedString {
            withStyle(style) {
                teachers.forEachIndexed { i, teacher ->
                    if (changed) withStyle(style.copy(color = MaterialTheme.colorScheme.error)) { append(teacher) }
                    else append(teacher)
                    if (i != teachers.lastIndex) append(", ")
                }
            }
        }
        Text(text = text)
    }
}

@Composable
private fun ClassText(className: String) {
    val style = MaterialTheme.typography.labelLarge.toSpanStyle()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(headerItemHeight())
    ) {
        Icon(
            imageVector = Icons.Outlined.PeopleAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        val text = buildAnnotatedString {
            withStyle(style) {
                append(className)
            }
        }
        Text(text = text)
    }
}

@Composable
private fun ProgressBar(lesson: Lesson, now: ZonedDateTime, activeModifier: Float) {
    val progress = lesson.progress(now)
    Expandable(isExpanded = progress in 0.0..1.0) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = (2 + 4 * activeModifier).dp, bottom = (2 + 4 * activeModifier).dp)
                    .height((1 + 14 * activeModifier).dp)
                    .clip(RoundedCornerShape((50 * activeModifier).toInt()))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape((50 * activeModifier).toInt()))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val minutesLeft = now.until(lesson.end, ChronoUnit.MINUTES).toInt()+1
                Text(
                    text = stringResource(
                        id = R.string.home_lessonCardProgressBottomLeft,
                        lesson.lessonNumber.toLocalizedString(),
                        lesson.start.format(timeFormatter),
                        lesson.end.format(timeFormatter)
                    ), style = MaterialTheme.typography.labelSmall
                )
                Text(text = pluralStringResource(id = R.plurals.home_lessonCardProgressBottomRight, count = minutesLeft, minutesLeft), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Expandable(isExpanded = progress !in 0.0..1.0) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        Text(
            text = stringResource(
                id = R.string.home_lessonCardProgressBottomLeft,
                lesson.lessonNumber.toLocalizedString(),
                lesson.start.format(timeFormatter),
                lesson.end.format(timeFormatter)
            ), style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun RowRecord(expand: Boolean?, icon: ImageVector, content: @Composable () -> Unit) {
    Expandable(isExpanded = expand ?: true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Box(modifier = Modifier.padding(start = 8.dp)) { content() }
        }
    }
}

@Composable
fun RowRecord(expand: Boolean?, icon: ImageVector, text: String) {
    RowRecord(expand, icon) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}