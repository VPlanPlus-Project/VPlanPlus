package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import es.jvbabi.vplanplus.ui.common.DOT
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
    hideFinishedLessons: Boolean,
    showCountdown: Boolean,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onAddHomework: (vpId: Long?) -> Unit,
    onBookRoomClicked: () -> Unit,
    scrollState: ScrollState
) {
    var stillShowHiddenLessons by rememberSaveable { mutableStateOf(false) }
    when (day?.type) {
        DayType.NORMAL -> {
            if (day.lessons.isEmpty()) return
            Column(
                Modifier.verticalScroll(scrollState)
            ) {
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
                if (hideFinishedLessons && day.lessons.any { it.progress(currentTime) >= 1 } && !stillShowHiddenLessons) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_someLessonsHidden) + " $DOT",
                            style = MaterialTheme.typography.labelLarge
                        )
                        TextButton(onClick = { stillShowHiddenLessons = true }) {
                            Text(text = stringResource(id = R.string.home_show))
                        }
                    }
                }
                day
                    .getFilteredLessons(currentIdentity.profile!!)
                    .groupBy { it.lessonNumber }
                    .toList()
                    .forEach { (_, lessons) ->
                        AnimatedVisibility(
                            visible = !hideFinishedLessons || stillShowHiddenLessons || (lessons.any { it.progress(currentTime) < 1.0 }),
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250))
                        ) {
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
                            )
                        }
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