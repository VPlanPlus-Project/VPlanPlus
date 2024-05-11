package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.util.DateUtils.isBeforeOrEqual
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private enum class DragValue { Center, End }


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeInfo(
    modifier: Modifier = Modifier,
    room: Room,
    selectedTime: ZonedDateTime,
    currentTime: ZonedDateTime = ZonedDateTime.now(),
    lessons: List<Lesson>,
    onClosed: () -> Unit,
) {

    var height by remember { mutableIntStateOf(1) }
    var anchors = DraggableAnchors {
        DragValue.Center at 0f
        DragValue.End at height.toFloat()
    }

    val dragState = remember {
        AnchoredDraggableState(
            anchors = anchors,
            initialValue = DragValue.Center,
            positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
            velocityThreshold = { height.toFloat() },
            animationSpec = tween()
        ).apply {
            updateAnchors(anchors)
        }
    }

    LaunchedEffect(key1 = height) {
        anchors = DraggableAnchors {
            DragValue.Center at 0f
            DragValue.End at height.toFloat()
        }
        dragState.updateAnchors(anchors)
    }

    LaunchedEffect(key1 = dragState.requireOffset()) {
        if (dragState.requireOffset() == height.toFloat()) onClosed()
    }

    var showDataForNow by rememberSaveable { mutableStateOf(false) }
    var time: ZonedDateTime by rememberSaveable { mutableStateOf((if (showDataForNow) currentTime else selectedTime)) }
    LaunchedEffect(key1 = showDataForNow, key2 = selectedTime, key3 = currentTime) {
        time = (if (showDataForNow) currentTime else selectedTime)
    }

    val isInUseNow = lessons.any { it.start.isBeforeOrEqual(currentTime) && it.end.isAfter(currentTime) && it.displaySubject != "-" }
    val isInUseAtSelectedTime = lessons.any { it.start.isBeforeOrEqual(selectedTime) && it.end.isAfter(selectedTime) && it.displaySubject != "-" }

    Column(
        modifier
            .onSizeChanged { height = it.height }
            .alpha(1f - (dragState.requireOffset() / height))
            .offset {
                IntOffset(
                    0,
                    dragState
                        .requireOffset()
                        .roundToInt()
                )
            }
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .anchoredDraggable(dragState, Orientation.Vertical)
    ) {
        Spacer(
            Modifier
                .padding(top = 8.dp)
                .align(CenterHorizontally)
                .width(40.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Row(
            Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .align(CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                onClick = { showDataForNow = true },
                label = { Text(text = stringResource(id = R.string.searchAvailableRoom_sheetNow)) },
                selected = showDataForNow
            )
            FilterChip(
                onClick = { showDataForNow = false },
                label = { Text(text = stringResource(id = R.string.searchAvailableRoom_sheetTimeTitle, selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))) },
                selected = !showDataForNow
            )
        }

        RowVerticalCenter(
            Modifier.padding(start = 16.dp, end = 16.dp),
            Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.searchAvailableRoom_sheetTitle, room.name),
                style = MaterialTheme.typography.headlineLarge
            )
            if (showDataForNow) {
                if (isInUseNow) Badge(color = MaterialTheme.colorScheme.error, text = stringResource(id = R.string.searchAvailableRoom_sheetNowInUse))
                else Badge(color = Color(37, 190, 120), text = stringResource(id = R.string.searchAvailableRoom_sheetNowAvailable))
            } else {
                if (isInUseAtSelectedTime) Badge(color = MaterialTheme.colorScheme.error, text = stringResource(id = R.string.searchAvailableRoom_sheetInUse))
                else Badge(color = Color(37, 190, 120), text = stringResource(id = R.string.searchAvailableRoom_sheetAvailable))
            }
        }
        Text(
            text = room.school.name,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            overflow = TextOverflow.Ellipsis
        )
        RowVerticalCenter(
            Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (showDataForNow) stringResource(id = R.string.searchAvailableRoom_sheetNow) else stringResource(id = R.string.searchAvailableRoom_sheetTimeTitle, time.format(DateTimeFormatter.ofPattern("HH:mm"))),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        val showIndicatorAfterLesson = lessons.filter { it.end.isAfter(time) }.maxByOrNull { it.lessonNumber }?.lessonNumber

        LazyRow(
            Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.width(16.dp))
            }
            items(lessons.filter { it.displaySubject != "-" }.sortedBy { it.lessonNumber }.groupBy { it.lessonNumber }.toList()) { (lessonNumber, currentLessons) ->
                val colorScheme = MaterialTheme.colorScheme
                RowVerticalCenter(
                    Modifier
                        .padding(end = 8.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .drawWithContent {
                            drawRect(
                                color = colorScheme.primaryContainer,
                                topLeft = Offset(0f, 0f),
                                size = size
                            )
                            drawRect(
                                topLeft = Offset(0f, 0f),
                                size = Size(
                                    currentLessons
                                        .first()
                                        .progress(time) * size.width, size.height
                                ),
                                color = colorScheme.tertiaryContainer
                            )
                            drawContent()
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$lessonNumber.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    currentLessons.forEach { lesson ->
                        VerticalDivider()
                        Column {
                            Text(
                                text = lesson.`class`.name + " $DOT " + lesson.displaySubject + " $DOT " + lesson.teachers.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(text = lesson.start.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + lesson.end.format(DateTimeFormatter.ofPattern("HH:mm")), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                if (lessonNumber == showIndicatorAfterLesson) {
                    Box(contentAlignment = Alignment.CenterStart) {
                        Spacer(modifier = Modifier
                            .width(32.dp)
                            .height(48.dp)
                            .background(
                                Brush.horizontalGradient(colors = listOf(Color(37, 190, 120).copy(alpha = .5f), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0f)))
                            )
                        )
                        Text(
                            text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            modifier = Modifier.padding(4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeInfoPreview() {
    val school = School.generateRandomSchools(1).first()
    TimeInfo(
        room = es.jvbabi.vplanplus.ui.preview.Room.generateRoom(school),
        selectedTime = ZonedDateTime.now().withHour(19).withMinute(31),
        lessons = Lessons.generateLessons(2, true),
        onClosed = {}
    )
}