package es.jvbabi.vplanplus.feature.room_search.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.room_search.ui.components.FilterRow
import es.jvbabi.vplanplus.feature.room_search.ui.components.RoomSearchField
import es.jvbabi.vplanplus.feature.room_search.ui.components.TimeInfo
import es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs.CancelBookingDialog
import es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs.RoomBookingRequestDialogHost
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.atDate
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Composable
fun RoomSearch(
    navHostController: NavHostController,
    viewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = viewModel.state

    RoomSearchContent(
        onBack = { navHostController.popBackStack() },
        onTapOnMatrix = viewModel::onTapOnMatrix,
        onQueryChanged = viewModel::onRoomNameQueryChanged,
        onToggleNowFilter = viewModel::onToggleNowFilter,
        onToggleNextFilter = viewModel::onToggleNextFilter,
        onToggleMyBookingsFilter = viewModel::onToggleMyBookingsFilter,

        onRequestBookingForSelectedContext = viewModel::onRequestBookingForSelectedContext,
        onConfirmBooking = viewModel::onConfirmBooking,
        onCancelBookingProgress = viewModel::onCancelBookingProgress,

        onRequestBookingCancellation = viewModel::onRequestBookingCancellation,
        onCancelBookingConfirmed = viewModel::onCancelBookingConfirmed,
        onCancelBookingAborted = viewModel::onCancelBookingAborted,

        state = state
    )
}

@SuppressLint("ReturnFromAwaitPointerEventScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomSearchContent(
    onBack: () -> Unit = {},
    onTapOnMatrix: (time: ZonedDateTime?, room: Room?) -> Unit = { _, _ -> },
    onQueryChanged: (query: String) -> Unit = {},
    onToggleNowFilter: () -> Unit = {},
    onToggleNextFilter: () -> Unit = {},
    onToggleMyBookingsFilter: () -> Unit = {},

    onRequestBookingForSelectedContext: () -> Unit = {},

    onConfirmBooking: (context: Context) -> Unit = {},
    onCancelBookingProgress: () -> Unit = {},

    onRequestBookingCancellation: (booking: RoomBooking) -> Unit = { _ -> },
    onCancelBookingAborted: () -> Unit = {},
    onCancelBookingConfirmed: (context: Context) -> Unit = {},

    state: RoomSearchState
) {
    var displayStartTime by remember { mutableStateOf(ZonedDateTime.now().atStartOfDay()) }
    val displayEndTime by remember(state.data) { mutableStateOf(state.data.flatMap { it.lessons }.maxOfOrNull { it.end } ?: displayStartTime) }

    val context = LocalContext.current
    val localDensity = LocalDensity.current
    val roomNameWidth = with(localDensity) { 48.dp.toPx() }
    val offset = 20f
    val verticalPadding = with (localDensity) { 4.dp.toPx() }
    val rowHeight = with(localDensity) { 48.dp.toPx() }
    var lessonTimeHeaderHeightTarget by remember { with(localDensity) { mutableFloatStateOf(0.dp.toPx()) } }
    val lessonTimeHeaderHeight by animateFloatAsState(targetValue = lessonTimeHeaderHeightTarget, label = "LessonTimeHeaderHeight")
    val totalHeight by remember(lessonTimeHeaderHeight, state.data.hashCode()) { mutableFloatStateOf(lessonTimeHeaderHeight + (rowHeight + verticalPadding) * state.data.count { it.isExpanded }) }
    var bottomSheetHeight by rememberSaveable { mutableFloatStateOf(0f) }

    var counter = 0
    var index = 0
    val modifierMap = state.data.associate {
        val modifierState = animateFloatAsState(targetValue = if (it.isExpanded) 1f else 0f, label = "")
        val roomIndex = if (it.isExpanded) run { counter++; counter - 1 } else -1
        val verticalOffset = (if (roomIndex == -1) 0f else verticalPadding) * (modifierState.value)
        index++
        it.room to RoomRowAnimatorState(
            alpha = modifierState,
            y = animateFloatAsState(targetValue = (verticalOffset + rowHeight) * roomIndex, label = ""),
            visualIndex = roomIndex
        )
    }

    LaunchedEffect(key1 = state.lessonTimes.hashCode()) updateUiStartTime@{
        if (state.lessonTimes.isEmpty()) return@updateUiStartTime
        displayStartTime = state.lessonTimes.values.minBy { it.lessonNumber }.start.atDate(state.currentTime)
    }

    if (state.cancelBookingRequest != null) CancelBookingDialog({ onCancelBookingConfirmed(context) }, onCancelBookingAborted)

    var scale by rememberSaveable { mutableFloatStateOf(4f) }
    val translation = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val calculator = OffsetCalculator(scale, translation.value.x, roomNameWidth + offset)
    val detailAlphaByScale = animateFloatAsState(targetValue = if (scale >= 3f) 1f else 0f, label = "LessonTimeAlpha")


    if (state.newRoomBookingRequest != null) {
        RoomBookingRequestDialogHost(
            bookingAbility = state.canBookRoom,
            group = (state.currentProfile as? ClassProfile)?.group,
            bookingRequest = state.newRoomBookingRequest,
            onConfirmBooking = { onConfirmBooking(context) },
            onCancelBooking = onCancelBookingProgress
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.searchAvailableRoom_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { BackIcon() }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    top = paddingValues.calculateTopPadding(),
                )
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val typography = MaterialTheme.typography

            RoomSearchField(onQueryChanged, state.roomNameQuery)
            FilterRow(
                showCurrentLesson = state.currentLessonTime != null,
                isCurrentLessonEnabled = state.filterRoomsAvailableNowActive,
                onToggleCurrentLesson = onToggleNowFilter,
                showNextLesson = state.nextLessonTime != null,
                isNextLessonEnabled = state.filterRoomsAvailableNextLessonActive,
                onToggleNextLesson = onToggleNextFilter,
                onToggleMyBookings = onToggleMyBookingsFilter,
                isMyBookingsEnabled = state.filterMyBookingsEnabled
            )

            Box(Modifier.fillMaxSize()) {
                val textMeasurer = rememberTextMeasurer()
                val scope = rememberCoroutineScope()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(state.data, scale) {
                            val decay = splineBasedDecay<Offset>(this)
                            val velocityTracker = VelocityTracker()
                            var isPress = false

                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.lastOrNull() ?: continue
                                    when (event.type) {
                                        PointerEventType.Press -> {
                                            isPress = true
                                        }

                                        PointerEventType.Move -> {
                                            isPress = false
                                            val previousCentroid = event.calculateCentroid(false)
                                            val currentCentroid = event.calculateCentroid(true)
                                            val targetOffset = Offset(
                                                translation.value.x + (currentCentroid.x - previousCentroid.x),
                                                translation.value.y + (currentCentroid.y - previousCentroid.y)
                                            )
                                            scope.launch {
                                                translation.snapTo(targetOffset)
                                            }
                                            if (event.changes.size > 2) velocityTracker.resetTracking()
                                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                                            scale = (scale * event.calculateZoom()).coerceIn(2f, 5f)

                                            run {
                                                val upperBound = Offset(0f, 0f)
                                                val lowerBound = Offset(
                                                    (-(calculator.calculateWidth(
                                                        displayStartTime,
                                                        maxOf(state.lessonTimes.values.maxByOrNull { it.lessonNumber }?.end?.atDate(state.currentTime) ?: displayStartTime, displayEndTime)
                                                    ) - size.width + roomNameWidth + offset)).coerceAtMost(0f),
                                                    (-(totalHeight - size.height + bottomSheetHeight)).coerceAtMost(0f)
                                                )
                                                translation.updateBounds(lowerBound, upperBound)
                                            }
                                        }

                                        PointerEventType.Release -> {
                                            val velocity = velocityTracker.calculateVelocity()
                                            velocityTracker.resetTracking()
                                            scope.launch {
                                                translation.animateDecay(Offset(velocity.x, velocity.y), decay)
                                            }

                                            if (isPress) {
                                                val roomIndex = ceil((change.position.y - translation.value.y - lessonTimeHeaderHeight) / (rowHeight + verticalPadding)).toInt() - 1
                                                val room = modifierMap.filterValues { it.visualIndex == roomIndex }.keys.firstOrNull() ?: continue

                                                val minutesOffset = ((change.position.x - translation.value.x - roomNameWidth - offset) / scale)
                                                if (minutesOffset < 0) continue

                                                val time = displayStartTime.plusMinutes(minutesOffset.toLong())

                                                onTapOnMatrix(time, room)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        .clipToBounds()
                ) {
                    translate(top = translation.value.y + lessonTimeHeaderHeight) {

                        state.data.forEach { roomData ->
                            val room = roomData.room
                            if (state.selectedRoom == room) {

                                // Draw selected room background
                                drawRect(
                                    color = colorScheme.surfaceVariant,
                                    topLeft = Offset(0f, modifierMap[room]?.y?.value ?: 0f),
                                    size = Size(size.width, 48.dp.toPx())
                                )


                                if (state.selectedLessonTime != null && roomData.getOccupiedTimes().none { it.overlaps(state.selectedLessonTime.toTimeSpan(state.currentTime)) }) {
                                    val startOffset = calculator.calculateOffset(displayStartTime.atBeginningOfTheWorld(), state.selectedLessonTime.start)
                                    val width = calculator.calculateWidth(state.selectedLessonTime.start, state.selectedLessonTime.end)
                                    drawRoundRect(
                                        color = colorScheme.primary,
                                        topLeft = Offset(startOffset, modifierMap[room]?.y?.value ?: 0f),
                                        size = Size(width, rowHeight),
                                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                    )

                                    val plusText = textMeasurer.measure(
                                        "+",
                                        style = typography.headlineMedium
                                    )

                                    drawText(
                                        plusText,
                                        topLeft = Offset(
                                            startOffset + (width / 2 - plusText.size.width / 2),
                                            (modifierMap[room]?.y?.value ?: 0f) + (48.dp.toPx() / 2 - plusText.size.height / 2)
                                        ),
                                        color = colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        state.lessonTimes.forEach lessonTimeMarker@{ (_, lessonTime) ->
                            val startOffset = calculator.calculateOffset(displayStartTime, lessonTime.start.atDate(state.currentTime))
                            val endOffset = calculator.calculateOffset(displayStartTime, lessonTime.end.atDate(state.currentTime))

                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(startOffset, 0f),
                                end = Offset(startOffset, totalHeight),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )

                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(endOffset, 0f),
                                end = Offset(endOffset, totalHeight),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )
                        }

                        state.data.forEach { (room, lessons, bookings, _) ->
                            lessons.forEach { lesson ->
                                val offsetStart = calculator.calculateOffset(displayStartTime, lesson.start)
                                val width = calculator.calculateWidth(lesson.start, lesson.end)

                                drawRect(
                                    color = colorScheme.secondaryContainer.copy(alpha = (modifierMap[room]?.alpha?.value ?: 1f)),
                                    topLeft = Offset(offsetStart, modifierMap[room]?.y?.value ?: 0f),
                                    size = Size(width, rowHeight)
                                )

                                val classText = buildAnnotatedString { append(lesson.`class`.name) }
                                val measuredClass = textMeasurer.measure(
                                    classText,
                                    style = typography.bodyMedium,
                                )
                                val textSize = measuredClass.size
                                drawText(
                                    measuredClass,
                                    topLeft = Offset(
                                        offsetStart + (width / 2 - textSize.width / 2),
                                        (modifierMap[room]?.y?.value ?: 0f) + (48.dp.toPx() / 2 - textSize.height / 2)
                                    ),
                                    color = colorScheme.onSecondaryContainer.copy(alpha = (modifierMap[room]?.alpha?.value ?: 1f) * detailAlphaByScale.value)
                                )
                            }

                            bookings.forEach { booking ->
                                val offsetStart = calculator.calculateOffset(displayStartTime, booking.from)
                                val width = calculator.calculateWidth(booking.from, booking.to)

                                drawRect(
                                    color = colorScheme.tertiaryContainer.copy(alpha = modifierMap[room]?.alpha?.value ?: 1f),
                                    topLeft = Offset(offsetStart, modifierMap[room]?.y?.value ?: 0f),
                                    size = Size(width, 48.dp.toPx())
                                )

                                val classText = buildAnnotatedString { append(booking.`class`.name) }
                                val measuredClass = textMeasurer.measure(
                                    classText,
                                    style = typography.bodyMedium
                                )
                                val textSize = measuredClass.size
                                drawText(
                                    measuredClass,
                                    topLeft = Offset(
                                        offsetStart + (width / 2 - textSize.width / 2),
                                        (modifierMap[room]?.y?.value ?: 0f) + (48.dp.toPx() / 2 - textSize.height / 2)
                                    ),
                                    color = colorScheme.onTertiaryContainer.copy(alpha = (modifierMap[room]?.alpha?.value ?: 1f) * detailAlphaByScale.value)
                                )
                            }

                            drawRect(
                                color = colorScheme.primaryContainer.copy(alpha = modifierMap[room]?.alpha?.value ?: 1f),
                                topLeft = Offset(0f, (modifierMap[room]?.y?.value ?: 0f)),
                                size = Size(roomNameWidth, 48.dp.toPx())
                            )

                            val measuredRoomName = textMeasurer.measure(
                                buildAnnotatedString { append(room.name) },
                                style = typography.bodyMedium,
                            )

                            drawText(
                                measuredRoomName,
                                topLeft = Offset(
                                    roomNameWidth / 2 - measuredRoomName.size.width / 2,
                                    (modifierMap[room]?.y?.value ?: 0f) + (48.dp.toPx() / 2 - measuredRoomName.size.height / 2)
                                ),
                                color = colorScheme.onPrimaryContainer.copy(alpha = modifierMap[room]?.alpha?.value ?: 1f)
                            )
                        }
                    }
                    if (state.selectedTime != null) {
                        val selectedTimeOffset = calculator.calculateOffset(displayStartTime, state.selectedTime)
                        if (selectedTimeOffset >= roomNameWidth) {
                            drawLine(
                                color = colorScheme.tertiary,
                                start = Offset(selectedTimeOffset, 0f),
                                end = Offset(selectedTimeOffset, totalHeight),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                            )

                            val selectedTimeMeasurer = textMeasurer.measure(
                                buildAnnotatedString {
                                    withStyle(typography.bodyMedium.toSpanStyle()) {
                                        append(state.selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    }
                                },
                                style = typography.bodyMedium,
                                softWrap = false,
                                maxLines = 1
                            )
                            drawRoundRect(
                                color = colorScheme.tertiary,
                                topLeft = Offset(selectedTimeOffset + 20f, 20f + lessonTimeHeaderHeight),
                                size = Size(selectedTimeMeasurer.size.width + 20f, selectedTimeMeasurer.size.height + 20f),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                            drawText(
                                selectedTimeMeasurer,
                                topLeft = Offset(selectedTimeOffset + 30f, lessonTimeHeaderHeight + 30f),
                                color = colorScheme.onTertiary
                            )
                        }
                    }

                    val currentTimeOffset = calculator.calculateOffset(displayStartTime, state.currentTime)
                    if (currentTimeOffset >= roomNameWidth) drawLine(
                        color = colorScheme.error,
                        start = Offset(currentTimeOffset, 0f),
                        end = Offset(currentTimeOffset, totalHeight),
                        strokeWidth = 2.dp.toPx(),
                    )

                    drawRect(
                        color = colorScheme.background,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, lessonTimeHeaderHeight)
                    )

                    state.lessonTimes.forEach lessonTimeHeader@{ (lessonNumber, lessonTime) ->
                        val startOffset = calculator.calculateOffset(displayStartTime, lessonTime.start.atDate(state.currentTime))

                        val measuredLessonNumber = textMeasurer.measure(
                            buildAnnotatedString {
                                withStyle(typography.bodyMedium.toSpanStyle()) {
                                    append(lessonNumber.toString())
                                    append(".")
                                }
                                val alphaHeaderHeight = run {
                                    val value = lessonTimeHeaderHeight / lessonTimeHeaderHeightTarget
                                    if (value.isNaN()) 0f
                                    else value.coerceIn(0f, 1f)
                                }
                                withStyle(typography.labelSmall.toSpanStyle().copy(color = colorScheme.outlineVariant.copy(alpha = detailAlphaByScale.value * alphaHeaderHeight))) {
                                    append("\n" + lessonTime.start.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    append("\n" + lessonTime.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                                }
                            },
                            constraints = Constraints.fixedWidth(48.dp.roundToPx()),
                            style = typography.bodyMedium,
                            softWrap = false
                        )
                        if (lessonTimeHeaderHeightTarget != measuredLessonNumber.size.height.toFloat()) lessonTimeHeaderHeightTarget = measuredLessonNumber.size.height.toFloat()
                        drawText(
                            measuredLessonNumber,
                            topLeft = Offset(startOffset, 0f),
                            color = colorScheme.onBackground
                        )
                    }

                    // Fade lesson time information
                    drawRect(
                        brush = Brush.horizontalGradient(colors = listOf(colorScheme.background, colorScheme.background.copy(alpha = 0f))),
                        topLeft = Offset(0f, 0f),
                        size = Size(roomNameWidth, lessonTimeHeaderHeight)
                    )
                }

                val alpha = animateFloatAsState(targetValue = if (state.selectedRoom == null) 0f else 1f, label = "RoomInfo")
                if (state.selectedRoom != null && state.selectedTime != null) Box(Modifier.align(Alignment.BottomCenter)) wrapper@{
                    TimeInfo(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .alpha(alpha.value)
                            .onSizeChanged { bottomSheetHeight = it.height.toFloat() },
                        selectedTime = state.selectedTime,
                        selectedLessonTime = state.selectedLessonTime,
                        currentTime = ZonedDateTime.now(),
                        currentProfile = state.currentProfile ?: return@wrapper,
                        data = state.data.firstOrNull { it.room == state.selectedRoom } ?: return@wrapper,
                        isBookingRelatedOperationInProgress = state.isBookingRelatedOperationInProgress,
                        onClosed = { onTapOnMatrix(null, null) },
                        paddingBottom = paddingValues.calculateBottomPadding(),
                        onRequestBookingForSelectedContext = onRequestBookingForSelectedContext,
                        onRequestBookingForCancellation = onRequestBookingCancellation
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun RoomSearchPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    RoomSearchContent(
        state = RoomSearchState(
            selectedRoom = es.jvbabi.vplanplus.ui.preview.Room.generateRoom(school),
            selectedTime = ZonedDateTime.now()
        )
    )
}

private class OffsetCalculator(
    val scale: Float,
    val scrollOffset: Float,
    val staticOffset: Float,
) {
    fun calculateOffset(from: ZonedDateTime, to: ZonedDateTime): Float {
        return (from.until(to, ChronoUnit.MINUTES) * scale) + scrollOffset + staticOffset
    }

    fun calculateWidth(from: ZonedDateTime, to: ZonedDateTime): Float {
        return from.until(to, ChronoUnit.MINUTES) * scale
    }
}

private data class RoomRowAnimatorState(
    val alpha: State<Float>,
    val y: State<Float>,
    val visualIndex: Int
)