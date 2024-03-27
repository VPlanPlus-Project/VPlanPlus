package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.Holiday
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.Weekend
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.ui.common.CollapsableInfoCard
import es.jvbabi.vplanplus.ui.common.InfoCard
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Composable
fun DayView(
    day: Day?,
    currentTime: ZonedDateTime,
    isInfoExpanded: Boolean?,
    currentIdentity: Identity,
    bookings: List<RoomBooking>,
    homework: List<Homework>,
    showCountdown: Boolean,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onAddHomework: (vpId: Long?) -> Unit,
    onBookRoomClicked: () -> Unit
) {
    when (day?.type) {
        DayType.NORMAL -> {
            if (day.lessons.isEmpty()) return
            Column {
                if (day.info != null) {
                    if (isInfoExpanded == null) {
                        InfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = day.info,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        CollapsableInfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = day.info,
                            modifier = Modifier.padding(vertical = 4.dp),
                            isExpanded = isInfoExpanded,
                            onChangeState = onChangeInfoExpandState
                        )
                    }
                }
                day
                    .getFilteredLessons(currentIdentity.profile!!)
                    .groupBy { it.lessonNumber }
                    .toList()
                    .forEach { (_, lessons) ->
                        LessonCard(
                            lessons = lessons.filter {
                                currentIdentity.profile.isDefaultLessonEnabled(it.vpId)
                            },
                            bookings = bookings,
                            time = currentTime,
                            modifier = Modifier.padding(vertical = 4.dp),
                            homework = homework,
                            onAddHomeworkClicked = { onAddHomework(it) },
                            onBookRoomClicked = onBookRoomClicked,
                            profileType = currentIdentity.profile.type
                        )
                    }
                val end = day
                    .lessons
                    .last {
                        currentIdentity.profile.isDefaultLessonEnabled(
                            it.vpId
                        )
                    }
                    .end
                if (!showCountdown) return@Column
                val difference = currentTime.until(end, ChronoUnit.SECONDS)
                if (difference > 0) Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.home_activeDayCountdown,
                            formatDuration(difference)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        DayType.WEEKEND -> Weekend()
        DayType.HOLIDAY -> Holiday()
        null -> {}
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}