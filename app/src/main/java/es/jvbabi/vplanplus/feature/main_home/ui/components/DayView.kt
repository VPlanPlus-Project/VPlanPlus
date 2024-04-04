package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
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
    val colorScheme = MaterialTheme.colorScheme

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
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        )
                    } else {
                        CollapsableInfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = day.info,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                            isExpanded = isInfoExpanded,
                            onChangeState = onChangeInfoExpandState
                        )
                    }
                }

                val padding = 32.dp
                if (hideFinishedLessons && day.lessons.any { it.progress(currentTime) >= 1 } && !stillShowHiddenLessons) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .drawWithContent {
                                drawContent()
                                val lineHeight =
                                    if (day.lessons.any { it.progress(currentTime) < 1 }) size.height else size.height / 2
                                drawLine(
                                    color = Color.Gray,
                                    start = Offset((padding/2).toPx(), 0f),
                                    end = Offset((padding/2).toPx(), lineHeight),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                                drawCircle(
                                    color = Color.Gray,
                                    center = Offset((padding/2).toPx(), size.height/2),
                                    radius = 6.dp.toPx()
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_someLessonsHidden) + " $DOT",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = padding)
                        )
                        TextButton(onClick = { stillShowHiddenLessons = true }) {
                            Text(text = stringResource(id = R.string.home_show))
                        }
                    }
                }

                val filteredLessons = day.getFilteredLessons(currentIdentity.profile!!).sortedBy { it.lessonNumber }
                var isFirstDisplay = true

                filteredLessons
                    .groupBy { it.lessonNumber }
                    .toList()
                    .forEach { (lessonNumber, lessons) ->
                        AnimatedVisibility(
                            visible = !hideFinishedLessons || stillShowHiddenLessons || (lessons.any { it.progress(currentTime) < 1.0 }),
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250)),
                            modifier = Modifier.drawWithContent {
                                drawContent()
                                drawLine(
                                    color = Color.Gray,
                                    start = Offset((padding/2).toPx(), 0f),
                                    end = Offset((padding/2).toPx(), 25.dp.toPx()),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect =
                                        if (isFirstDisplay) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        else null
                                )
                                if (lessonNumber != filteredLessons.lastOrNull()?.lessonNumber && showCountdown) drawLine(
                                    color = Color.Gray,
                                    start = Offset((padding/2).toPx(), 27.dp.toPx()),
                                    end = Offset((padding/2).toPx(), size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                drawCircle(
                                    color =
                                        if (lessons.any { it.progress(currentTime) < 1 }) colorScheme.primary
                                        else colorScheme.secondary,
                                    center = Offset((padding/2).toPx(), 27.dp.toPx()),
                                    radius = 6.dp.toPx()
                                )

                                isFirstDisplay = false
                            }
                        ) {
                            LessonCard(
                                lessons = lessons.filter {
                                    currentIdentity.profile.isDefaultLessonEnabled(it.vpId)
                                },
                                bookings = bookings,
                                time = currentTime,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = padding),
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
                if (difference > 0) Row(
                    Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            drawLine(
                                color = Color.Gray,
                                start = Offset((padding/2).toPx(), 0f),
                                end = Offset((padding/2).toPx(), 22.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawCircle(
                                color = Color.Gray,
                                center = Offset((padding/2).toPx(), 18.dp.toPx()),
                                radius = 6.dp.toPx()
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier.padding(start = padding + 8.dp).size(24.dp)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.home_activeDayCountdown,
                            formatDuration(difference)
                        ),
                        style = MaterialTheme.typography.labelLarge,
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