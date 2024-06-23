package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.Holiday
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.Weekend
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.ui.common.CollapsableInfoCard
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun DayView(
    day: Day?,
    currentTime: ZonedDateTime,
    isInfoExpanded: Boolean?,
    currentProfile: Profile,
    bookings: List<RoomBooking>,
    homework: List<Homework>,
    hideFinishedLessons: Boolean,
    showCountdown: Boolean,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onAddHomework: (vpId: Int?) -> Unit,
    onBookRoomClicked: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    var ignoreAutoHideFinishedLessons by rememberSaveable { mutableStateOf(false) }

    when (day?.type) {
        DayType.NORMAL -> {
            if (day.lessons.isEmpty()) return
            Column {
                val allLessonsDone = day.anyLessonsLeft(currentTime, currentProfile)
                val markerCircleRadius = 18f
                val markerLineWidth = 3f

                val lastActualLesson = day
                    .lessons
                    .filter { (currentProfile as? ClassProfile)?.isDefaultLessonEnabled(it.vpId) ?: true && it.displaySubject != "-" }
                    .maxByOrNull { it.end }

                val uiWillShowHiddenLessonsCard = hideFinishedLessons && !ignoreAutoHideFinishedLessons && day.lessons.any { currentTime.progress(it.start, it.end) >= 1 }
                val uiWillShowCountdown = showCountdown && lastActualLesson != null && lastActualLesson.end.isAfter(currentTime)

                if (day.info != null) {
                    if (isInfoExpanded == null) {
                        InfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = day.info,
                            modifier = Modifier
                                .zIndex(1f)
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                    } else {
                        CollapsableInfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = day.info,
                            modifier = Modifier
                                .zIndex(1f)
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                            isExpanded = isInfoExpanded,
                            onChangeState = onChangeInfoExpandState
                        )
                    }
                }

                val padding = 32.dp
                if (uiWillShowHiddenLessonsCard) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .drawWithContent {
                                drawContent()

                                val pencilX = (padding / 2).toPx()
                                val circleY = size.height / 2

                                val lineHeight = if (allLessonsDone) size.height else circleY
                                drawLine(
                                    brush = Brush.verticalGradient(listOf(Color.Gray.copy(alpha = 0f), Color.Gray)),
                                    start = Offset(pencilX, (-8).dp.toPx()),
                                    end = Offset(pencilX, lineHeight),
                                    strokeWidth = markerLineWidth,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                                )

                                drawCircle(
                                    color = Color.Gray,
                                    center = Offset(pencilX, circleY),
                                    radius = markerCircleRadius
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_someLessonsHidden) + " $DOT",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = padding)
                        )
                        TextButton(onClick = { ignoreAutoHideFinishedLessons = true }) {
                            Text(text = stringResource(id = R.string.home_show))
                        }
                    }
                }

                val filteredLessons = day.getEnabledLessons(currentProfile).sortedBy { it.lessonNumber }

                val displayLessonGroups = filteredLessons.groupBy { it.lessonNumber }.toList()

                displayLessonGroups.forEachIndexed { i, (lessonNumber, lessons) ->
                        val isFirstVisibleLessonCard =
                            ((!hideFinishedLessons || ignoreAutoHideFinishedLessons) && i == 0) ||
                                    (hideFinishedLessons && !ignoreAutoHideFinishedLessons && filteredLessons.none { currentTime.progress(it.start, it.end) < 1 && it.lessonNumber < lessonNumber })
                        
                        val isLastVisibleLessonCard = i == displayLessonGroups.lastIndex

                        AnimatedVisibility(
                            visible = !hideFinishedLessons || ignoreAutoHideFinishedLessons || (lessons.any { currentTime.progress(it.start, it.end) < 1.0 }),
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250)),
                            modifier = Modifier.drawWithContent {
                                drawContent()

                                val pencilX = (padding/2).toPx()
                                val circleY = 24.dp.toPx()

                                if (isFirstVisibleLessonCard && !uiWillShowHiddenLessonsCard) {
                                    drawLine(
                                        brush = Brush.verticalGradient(listOf(Color.Gray.copy(alpha = 0f), Color.Gray, Color.Gray)),
                                        start = Offset(pencilX, 0f),
                                        end = Offset(pencilX, circleY-markerCircleRadius),
                                        strokeWidth = markerLineWidth,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                                    )
                                } else {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(pencilX, 0f),
                                        end = Offset(pencilX, circleY),
                                        strokeWidth = markerLineWidth,
                                        pathEffect = if (uiWillShowHiddenLessonsCard && isFirstVisibleLessonCard) PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f) else null
                                    )
                                }

                                if ((isLastVisibleLessonCard && uiWillShowCountdown) || !isLastVisibleLessonCard) drawLine(
                                    color = Color.Gray,
                                    start = Offset(pencilX, circleY),
                                    end = Offset(pencilX, size.height),
                                    strokeWidth = markerLineWidth
                                )
                                drawCircle(
                                    color =
                                        if (lessons.any { currentTime.progress(it.start, it.end) < 1 }) colorScheme.primary
                                        else colorScheme.secondary,
                                    center = Offset(pencilX, circleY),
                                    radius = markerCircleRadius
                                )
                            }
                        ) {
                            LessonCard(
                                lessons = lessons.filter { (currentProfile as? ClassProfile)?.isDefaultLessonEnabled(it.vpId) ?: true },
                                bookings = bookings,
                                time = currentTime,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = padding, end = 8.dp),
                                homework = homework,
                                allowHomeworkQuickAction = (currentProfile as? ClassProfile)?.isHomeworkEnabled ?: false,
                                onAddHomeworkClicked = { if ((currentProfile as? ClassProfile)?.isHomeworkEnabled == true) onAddHomework(it) },
                                onBookRoomClicked = onBookRoomClicked,
                                displayType = currentProfile.getType()
                            )
                        }
                    }
                if (!uiWillShowCountdown || lastActualLesson == null) return@Column
                val difference = currentTime.until(lastActualLesson.end, ChronoUnit.SECONDS)
                if (difference > 0) Row(
                    Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            drawLine(
                                color = Color.Gray,
                                start = Offset((padding / 2).toPx(), 0f),
                                end = Offset((padding / 2).toPx(), 22.dp.toPx()),
                                strokeWidth = markerLineWidth
                            )
                            drawCircle(
                                color = Color.Gray,
                                center = Offset((padding / 2).toPx(), 18.dp.toPx()),
                                radius = 6.dp.toPx()
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = padding + 8.dp)
                            .size(24.dp)
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
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, remainingSeconds)
}