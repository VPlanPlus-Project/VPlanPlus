package es.jvbabi.vplanplus.ui.screens.home.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.home.components.home.screens.Weekend
import es.jvbabi.vplanplus.ui.screens.home.components.home.text.LastSyncText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import es.jvbabi.vplanplus.ui.preview.Room as PreviewRoom

@Composable
fun ActiveDayContent(
    info: String?,
    currentTime: LocalDateTime,
    day: Day,
    profile: Profile,
    bookings: List<RoomBooking>,
    hiddenLessons: Int,
    lastSync: LocalDateTime?,
    isLoading: Boolean,
    onFindRoomClicked: () -> Unit = {}
) {
    val lessons = day.lessons.filter { profile.isDefaultLessonEnabled(it.vpId) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            return
        }
        if (day.type == DayType.WEEKEND) {
            Column {
                LastSyncText(lastSync, modifier = Modifier.padding(start = 16.dp))
                Weekend()
            }
            return
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) root@{
            Row(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                if (hiddenLessons > 0) {
                    Text(
                        text = stringResource(
                            id = R.string.home_lessonsHidden,
                            hiddenLessons
                        ) + " $DOT ",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                LastSyncText(lastSync)
            }
            if (info != null) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    InfoCard(
                        imageVector = Icons.Default.Info,
                        title = stringResource(id = R.string.home_activeDaySchoolInformation),
                        text = info
                    )
                }
            }
            val currentLessons = lessons.filter { it.progress(currentTime) in 0.0..<1.0 }
            if (currentLessons.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.home_activeDayNow),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    DetailedLessonCard(
                        lessons = currentLessons,
                        onFindRoomClicked = onFindRoomClicked
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
            val nextLesson = lessons.firstOrNull {
                it.progress(currentTime) < 0
            }
            if (nextLesson != null && currentLessons.isEmpty()) {
                val nextLessons = lessons.filter { it.lessonNumber == nextLesson.lessonNumber }
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    DetailedLessonCard(lessons = nextLessons, onFindRoomClicked = onFindRoomClicked)
                }

                lessons.filter { it.lessonNumber > nextLesson.lessonNumber }
                    .groupBy { it.lessonNumber }
                    .forEach { (_, lessons) ->
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            LessonCard(lessons = lessons)
                        }
                    }
            } else if (nextLesson != null) {
                lessons.filter { it.lessonNumber >= nextLesson.lessonNumber }
                    .groupBy { it.lessonNumber }
                    .forEach { (_, lessons) ->
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            LessonCard(lessons = lessons)
                        }
                    }
                HorizontalDivider(modifier = Modifier.padding(8.dp))
            }
            if (bookings.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.home_activeDayBookings),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                bookings.forEach { booking ->
                    Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        RoomBookingCard(roomBooking = booking)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val end = lessons.lastOrNull{ it.displaySubject != "-" }?.end ?: return@Column
                val difference = currentTime.until(end, ChronoUnit.SECONDS)
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                if (difference > 0) Text(
                    text = stringResource(
                        id = R.string.home_activeDayCountdown,
                        formatDuration(difference)
                    )
                )
                else Text(
                    text = stringResource(id = R.string.home_activeDayEnd),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DetailedLessonCard(
    lessons: List<Lesson>,
    onFindRoomClicked: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .drawWithContent {
                drawRect(
                    color = colorScheme.tertiaryContainer,
                    topLeft = Offset(0f, 0f),
                    size = Size(
                        size.width * lessons
                            .first()
                            .progress(LocalDateTime.now()), size.height
                    )
                )
                drawContent()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${lessons.first().lessonNumber}.",
            style = MaterialTheme.typography.headlineSmall
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) lessonContainer@{
            lessons.forEachIndexed { index, lesson ->
                val text = buildAnnotatedString {
                    val normal = MaterialTheme.typography.headlineSmall.toSpanStyle()
                    val changed = normal.copy(color = colorScheme.primary)
                    if (lesson.displaySubject == "-") withStyle(changed) {
                        append(stringResource(id = R.string.home_activeDayNextLessonCanceled))
                        if (lesson.roomBooking != null) {
                            withStyle(normal) { append(" $DOT ${lesson.roomBooking.room.name}") }
                            return@buildAnnotatedString
                        }
                    }
                    withStyle(if (lesson.subjectIsChanged) changed else normal) {
                        append(lesson.displaySubject)
                    }
                    if (lesson.teachers.isNotEmpty()) {
                        withStyle(normal) { append(" $DOT ") }
                        withStyle(if (lesson.teacherIsChanged) changed else normal) {
                            append(lesson.teachers.joinToString(", "))
                        }
                    }
                    if (lesson.rooms.isNotEmpty()) {
                        withStyle(normal) { append(" $DOT ") }
                        withStyle(if (lesson.roomIsChanged) changed else normal) {
                            append(lesson.rooms.joinToString(", "))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text)
                        if (!lesson.info.isNullOrEmpty()) {
                            Text(text = lesson.info, style = MaterialTheme.typography.bodySmall)
                        }

                        // show booking information if available
                        if (lesson.displaySubject == "-") {
                            if (lesson.roomBooking == null) {
                                if (lesson.progress(LocalDateTime.now()) < 1f) AssistChip(
                                    onClick = { onFindRoomClicked() },
                                    label = { Text(text = stringResource(id = R.string.home_activeBookRoom)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.MeetingRoom,
                                            contentDescription = null
                                        )
                                    }
                                )
                            } else {
                                Text(
                                    text = stringResource(
                                        id = R.string.home_activeRoomBookedBy,
                                        lesson.roomBooking.bookedBy?.name ?: "-",
                                        lesson.roomBooking.room.name
                                    ), style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    SubjectIcon(
                        subject = lesson.displaySubject,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                    )
                }

                if (index < lessons.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun LessonCard(
    lessons: List<Lesson>
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${lessons.first().lessonNumber}.",
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) lessonContainer@{
            lessons.forEachIndexed { index, lesson ->
                val text = buildAnnotatedString {
                    val normal = MaterialTheme.typography.titleMedium.toSpanStyle()
                    val changed = normal.copy(color = colorScheme.primary)
                    if (lesson.displaySubject == "-") withStyle(changed) {
                        append(stringResource(id = R.string.home_activeDayNextLessonCanceled))
                        if (lesson.roomBooking != null) {
                            withStyle(normal) {
                                append(" $DOT ${lesson.roomBooking.room.name} ")
                                append(stringResource(id = R.string.home_activeDayBooking))
                            }
                            return@buildAnnotatedString
                        }
                    }
                    withStyle(if (lesson.subjectIsChanged) changed else normal) {
                        append(lesson.displaySubject)
                    }
                    if (lesson.teachers.isNotEmpty()) {
                        withStyle(normal) { append(" $DOT ") }
                        withStyle(if (lesson.teacherIsChanged) changed else normal) {
                            append(lesson.teachers.joinToString(", "))
                        }
                    }
                    if (lesson.rooms.isNotEmpty()) {
                        withStyle(normal) { append(" $DOT ") }
                        withStyle(if (lesson.roomIsChanged) changed else normal) {
                            append(lesson.rooms.joinToString(", "))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text)
                        if (!lesson.info.isNullOrEmpty()) {
                            Text(text = lesson.info, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    SubjectIcon(
                        subject = lesson.displaySubject,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp)
                    )
                }

                if (index < lessons.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun RoomBookingCard(roomBooking: RoomBooking) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        Column {
            Text(
                text = "${roomBooking.room.name} $DOT ${roomBooking.from.format(formatter)} - ${roomBooking.to.plusSeconds(1).format(formatter)}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    id = R.string.home_activeBookedBy,
                    roomBooking.bookedBy?.name ?: stringResource(
                        id = R.string.unknownVppId
                    ),
                    roomBooking.`class`.name
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    ActiveDayContent(
        info = "Info",
        currentTime = LocalDateTime.now(),
        day = Day(
            lessons = Lessons.generateLessons(2, true),
            type = DayType.WEEKEND,
            date = LocalDateTime.now().toLocalDate(),
            info = null,
            state = DayDataState.DATA
        ),
        bookings = emptyList(),
        profile = es.jvbabi.vplanplus.ui.preview.Profile.generateClassProfile(),
        hiddenLessons = 2,
        lastSync = LocalDateTime.now(),
        isLoading = false
    )
}

@Preview
@Composable
private fun RoomBookingPreview() {
    val school = School.generateRandomSchools(1).first()
    val classes = ClassesPreview.generateClass(school)
    val room = PreviewRoom.generateRoom(school)
    RoomBookingCard(
        roomBooking = RoomBooking(
            1,
            from = LocalDateTime.now(),
            to = LocalDateTime.now().plusHours(1),
            bookedBy = VppIdPreview.generateVppId(null),
            room = room,
            `class` = classes
        )
    )
}

fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}