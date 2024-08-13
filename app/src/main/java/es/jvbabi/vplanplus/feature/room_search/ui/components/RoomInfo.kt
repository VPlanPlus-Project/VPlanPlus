package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomState
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.common.unknownVppId
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.util.DateUtils.isBeforeOrEqual
import es.jvbabi.vplanplus.util.DateUtils.progress
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private enum class DragValue { Center, End }


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeInfo(
    modifier: Modifier = Modifier,
    selectedTime: ZonedDateTime,
    selectedLessonTime: LessonTime? = null,
    currentTime: ZonedDateTime = ZonedDateTime.now(),
    currentProfile: Profile,
    data: RoomState,
    isBookingRelatedOperationInProgress: Boolean = false,
    onClosed: () -> Unit,
    paddingBottom: Dp = 0.dp,
    onRequestBookingForSelectedContext: () -> Unit,
    onRequestBookingForCancellation: (RoomBooking) -> Unit
) {
    var height by remember { mutableIntStateOf(1) }
    var anchors = DraggableAnchors {
        DragValue.Center at 0f
        DragValue.End at height.toFloat()
    }

    val dragState = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center,
            positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
            velocityThreshold = { height.toFloat() },
            snapAnimationSpec = tween(),
            decayAnimationSpec = exponentialDecay(0.9f),
            confirmValueChange = { it == DragValue.End },
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

    val isInUseNow = data.lessons.any { it.start.isBeforeOrEqual(currentTime) && it.end.isAfter(currentTime) && it.displaySubject != "-" }
    val isInUseAtSelectedTime = data.lessons.any { it.start.isBeforeOrEqual(selectedTime) && it.end.isAfter(selectedTime) && it.displaySubject != "-" }
    val scope = rememberCoroutineScope()

    BackHandler {
        scope.launch {
            dragState.anchoredDrag(targetValue = DragValue.End, dragPriority = MutatePriority.PreventUserInput) { anchors, latestTarget ->
                val targetOffset = anchors.positionOf(latestTarget)
                if (!targetOffset.isNaN()) {
                    var prev = if (dragState.requireOffset().isNaN()) 0f else dragState.requireOffset()
                    animate(prev, targetOffset, dragState.lastVelocity, tween()) { value, velocity ->
                        dragTo(value, velocity)
                        prev = value
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
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
            .anchoredDraggable(dragState, Orientation.Vertical)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        .padding(start = 16.dp, end = 16.dp)
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

                HorizontalDivider(Modifier.padding(horizontal = 32.dp))
            }


            Column {
                RowVerticalCenter(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.searchAvailableRoom_sheetTitle, data.room.name),
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
                    text = data.room.school.name,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column eventInfo@{
                RowVerticalCenter(
                    Modifier.padding(start = 16.dp, end = 16.dp),
                    Arrangement.spacedBy(8.dp)
                ) selectedTime@{
                    Text(
                        text = if (showDataForNow) stringResource(id = R.string.searchAvailableRoom_sheetNow) else stringResource(
                            id = R.string.searchAvailableRoom_sheetTimeTitle,
                            time.format(DateTimeFormatter.ofPattern("HH:mm"))
                        ),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                if (data.lessons.isEmpty()) Text(
                    text = stringResource(id = R.string.searchAvailableRoom_sheetNoLessonsForRoom, data.room.name),
                    modifier = Modifier
                        .height(48.dp)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .align(CenterHorizontally),
                    style = MaterialTheme.typography.labelMedium.copy(lineHeight = with(LocalDensity.current) { 48.dp.toSp() }),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) else LazyRow(Modifier.fillMaxWidth()) {
                    item {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    items(data.lessons.filter { it.displaySubject != "-" }.sortedBy { it.lessonNumber }.groupBy { it.lessonNumber }.toList()) { (lessonNumber, currentLessons) ->
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
                                            run {
                                                val c = currentLessons.first()
                                                return@run time.progress(c.start, c.end)
                                            } * size.width, size.height
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
                                        text = lesson.`class`.name + " $DOT " + lesson.displaySubject + " $DOT " + lesson.teachers.joinToString(", ") { it.acronym },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = lesson.start.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + lesson.end.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column {
                AnimatedVisibility(
                    visible = selectedLessonTime != null,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    val currentBooking = data.bookings.firstOrNull { time.progress(it.from, it.to) in 0f..1f }
                    val userCanCancelBooking = data.bookings.any { time.progress(it.from, it.to) in 0f..1f && (it.bookedBy?.id ?: -1) == (currentProfile as? ClassProfile)?.vppId?.id }
                    OutlinedButton(
                        onClick = {
                            if (userCanCancelBooking) onRequestBookingForCancellation(currentBooking ?: return@OutlinedButton)
                            else onRequestBookingForSelectedContext()
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        enabled = ((!isInUseAtSelectedTime && selectedLessonTime != null) || userCanCancelBooking) && !isBookingRelatedOperationInProgress
                    ) {
                        if (isBookingRelatedOperationInProgress) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp)
                                    .padding(6.dp)
                            )
                            return@OutlinedButton
                        }
                        var text by remember { mutableStateOf("") }
                        val context = LocalContext.current

                        LaunchedEffect(key1 = selectedLessonTime, userCanCancelBooking, currentBooking) {
                            text = if (userCanCancelBooking) context.getString(R.string.searchAvailableRoom_sheetCancelBooking)
                            else if (currentBooking != null) context.getString(R.string.searchAvailableRoom_sheetRoomBooked, currentBooking.bookedBy?.name ?: unknownVppId(context))
                            else if (selectedLessonTime != null) {
                                context.getString(R.string.searchAvailableRoom_sheetBookRoom, selectedLessonTime.lessonNumber.toLocalizedString())
                            } else text
                        }

                        Text(text = text)
                    }
                }
            }

            Spacer(Modifier)

        }
        Spacer(modifier = Modifier.height(maxOf(paddingBottom, 16.dp)))
    }
}

@OptIn(PreviewFunction::class)
@Preview(showBackground = true)
@Composable
private fun TimeInfoPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val group = GroupPreview.generateGroup(school)
    val vppId = VppIdPreview.generateVppId(group).toActiveVppId()
    val profile = ProfilePreview.generateClassProfile(group, vppId)
    TimeInfo(
        data = RoomState(
            room = es.jvbabi.vplanplus.ui.preview.RoomPreview.generateRoom(school),
            lessons = Lessons.generateLessons(2, true),
            bookings = emptyList()
        ),
        selectedTime = ZonedDateTime.now().withHour(19).withMinute(31),
        selectedLessonTime = es.jvbabi.vplanplus.util.LessonTime.fallbackTime(0, 1),
        onClosed = {},
        isBookingRelatedOperationInProgress = true,
        onRequestBookingForSelectedContext = {},
        currentProfile = profile,
        onRequestBookingForCancellation = { _ -> }
    )
}