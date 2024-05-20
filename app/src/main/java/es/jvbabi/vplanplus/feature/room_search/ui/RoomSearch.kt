package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.feature.room_search.ui.components.TimeInfo
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.atDate
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Composable
fun RoomSearch(
    navHostController: NavHostController,
    viewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    RoomSearchContent(
        onBack = { navHostController.popBackStack() },
        onTapOnMatrix = viewModel::onTapOnMatrix,
        onBookRoomRequested = { navHostController.navigate(Screen.BookRoomScreen.route + "/${state.selectedRoom?.name}") },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomSearchContent(
    onBack: () -> Unit = {},
    onTapOnMatrix: (time: ZonedDateTime?, room: Room?) -> Unit = { _, _ -> },
    onBookRoomRequested: () -> Unit = {},
    state: RoomSearchState
) {
    var displayStartTime by remember { mutableStateOf(ZonedDateTime.now().atStartOfDay()) }
    LaunchedEffect(key1 = state.lessonTimes.hashCode()) updateUiStartTime@{
        if (state.lessonTimes.isEmpty()) return@updateUiStartTime
        displayStartTime = state.lessonTimes.values.minBy { it.lessonNumber }.start.atDate(state.currentTime)
    }

    val localDensity = LocalDensity.current
    val roomNameWidth = with(localDensity) { 48.dp.toPx() }
    val offset = 20f
    val verticalPadding = 4.dp
    val headerHeightDp = 64.dp
    val totalHeight = headerHeightDp + (48.dp + verticalPadding) * state.data.size
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
        Box(
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

            Box(Modifier.fillMaxSize()) {
                var scale by rememberSaveable { mutableFloatStateOf(4f) }
                var scrollOffset by remember { mutableStateOf(Offset(0f, 0f)) }

                val textMeasurer = rememberTextMeasurer()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(state.data) {
                            detectTransformGestures(
                                onGesture = { centroid, pan, zoom, _ ->
                                    val targetScale = (scale * zoom).coerceIn(1f, 10f)
                                    val realZoom = targetScale / scale
                                    val center = size.toSize().center
                                    val targetX = scrollOffset.x * realZoom - (centroid.x - center.x) * (realZoom - 1) + pan.x
                                    val targetY = scrollOffset.y + pan.y

                                    scale = targetScale
                                    scrollOffset = Offset(targetX.coerceAtMost(0f), targetY.coerceAtMost(0f))
                                }
                            )
                        }
                        .pointerInput(state.data) {
                            detectTapGestures { tapOffset ->
                                val roomIndex = ceil((tapOffset.y - scrollOffset.y - headerHeightDp.toPx()) / (48.dp + verticalPadding).toPx()).toInt() - 1
                                val room = state.data.getOrNull(roomIndex) ?: return@detectTapGestures

                                val minutesOffset = ((tapOffset.x - scrollOffset.x - roomNameWidth - offset) / scale)
                                if (minutesOffset < 0) return@detectTapGestures

                                val time = displayStartTime.plusMinutes(minutesOffset.toLong())

                                onTapOnMatrix(time, room.room)
                            }
                        }
                ) {
                    val headerHeight = headerHeightDp.toPx()
                    val calculator = OffsetCalculator(scale, scrollOffset.x, roomNameWidth + offset)

                    translate(top = scrollOffset.y + headerHeight) {
                        state.lessonTimes.forEach lessonTimeMarker@{ (_, lessonTime) ->
                            val startOffset = calculator.calculateOffset(displayStartTime, lessonTime.start.atDate(state.currentTime))
                            val endOffset = calculator.calculateOffset(displayStartTime, lessonTime.end.atDate(state.currentTime))

                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(startOffset, 0f),
                                end = Offset(startOffset, totalHeight.toPx()),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )

                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(endOffset, 0f),
                                end = Offset(endOffset, totalHeight.toPx()),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )
                        }

                        state.data.forEachIndexed { roomIndex, (room, lessons, bookings) ->
                            val verticalOffset = if (roomIndex == 0) 0.dp else verticalPadding

                            lessons.forEach { lesson ->
                                val offsetStart = calculator.calculateOffset(displayStartTime, lesson.start)
                                val width = calculator.calculateWidth(lesson.start, lesson.end)

                                drawRect(
                                    color = colorScheme.secondaryContainer,
                                    topLeft = Offset(offsetStart, roomIndex * (48.dp + verticalOffset).toPx()),
                                    size = Size(width, 48.dp.toPx())
                                )

                                val classText = buildAnnotatedString { append(lesson.`class`.name) }
                                val measuredClass = textMeasurer.measure(
                                    classText,
                                    style = typography.bodyMedium
                                )
                                val textSize = measuredClass.size
                                drawText(
                                    measuredClass,
                                    topLeft = Offset(
                                        offsetStart + (width/2- textSize.width/2),
                                        (roomIndex * (48.dp + verticalOffset).toPx()) + (48.dp.toPx() / 2 - textSize.height / 2)
                                    ),
                                    color = colorScheme.onSecondaryContainer
                                )
                            }

                            bookings.forEach { booking ->
                                val offsetStart = calculator.calculateOffset(displayStartTime, booking.from)
                                val width = calculator.calculateWidth(booking.from, booking.to)

                                drawRect(
                                    color = colorScheme.tertiaryContainer,
                                    topLeft = Offset(offsetStart, roomIndex * (48.dp + verticalOffset).toPx()),
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
                                        offsetStart + (width/2- textSize.width/2),
                                        (roomIndex * (48.dp + verticalOffset).toPx()) + (48.dp.toPx() / 2 - textSize.height / 2)
                                    ),
                                    color = colorScheme.onTertiaryContainer
                                )
                            }

                            drawRect(
                                color = colorScheme.secondaryContainer,
                                topLeft = Offset(0f, roomIndex * (48.dp + verticalOffset).toPx()),
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
                                    roomIndex * (48.dp + verticalOffset).toPx() + (48.dp.toPx() / 2 - measuredRoomName.size.height / 2)
                                ),
                                color = colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    if (state.selectedTime != null) {
                        val selectedTimeOffset = calculator.calculateOffset(displayStartTime, state.selectedTime)
                        if (selectedTimeOffset >= roomNameWidth) drawLine(
                            color = colorScheme.primary,
                            start = Offset(selectedTimeOffset, 0f),
                            end = Offset(selectedTimeOffset, totalHeight.toPx()),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                        )
                    }

                    val currentTimeOffset = calculator.calculateOffset(displayStartTime, state.currentTime)
                    if (currentTimeOffset >= roomNameWidth) drawLine(
                        color = colorScheme.error,
                        start = Offset(currentTimeOffset, 0f),
                        end = Offset(currentTimeOffset, totalHeight.toPx()),
                        strokeWidth = 2.dp.toPx(),
                    )

                    drawRect(
                        color = colorScheme.background,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, headerHeight)
                    )

                    state.lessonTimes.forEach lessonTimeHeader@{ (lessonNumber, lessonTime) ->
                        val startOffset = calculator.calculateOffset(displayStartTime, lessonTime.start.atDate(state.currentTime))

                        val measuredLessonNumber = textMeasurer.measure(
                            buildAnnotatedString {
                                withStyle(typography.bodyMedium.toSpanStyle()) {
                                    append(lessonNumber.toString())
                                    append(".")
                                }
                                withStyle(typography.labelSmall.toSpanStyle()) {
                                    append("\n" + lessonTime.start.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    append("→\n" + lessonTime.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                                }
                            },
                            constraints = Constraints.fixedWidth(48.dp.roundToPx()),
                            style = typography.bodyMedium
                        )
                        drawText(
                            measuredLessonNumber,
                            topLeft = Offset(startOffset, 0f)
                        )
                    }

                    // Fade lesson time information
                    drawRect(
                        brush = Brush.horizontalGradient(colors = listOf(colorScheme.background, colorScheme.background.copy(alpha = 0f))),
                        topLeft = Offset(0f, 0f),
                        size = Size(roomNameWidth, headerHeight)
                    )
                }
            }

            val alpha = animateFloatAsState(targetValue = if (state.selectedRoom == null) 0f else 1f, label = "RoomInfo")
            if (state.selectedRoom != null && state.selectedTime != null) Box(Modifier.align(Alignment.BottomCenter)) wrapper@{
                TimeInfo(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                        )
                        .alpha(alpha.value),
                    selectedTime = state.selectedTime,
                    currentTime = ZonedDateTime.now(),
                    data = state.data.firstOrNull { it.room == state.selectedRoom } ?: return@wrapper,
                    onClosed = { onTapOnMatrix(null, null) },
                    onBookRoomClicked = onBookRoomRequested,
                    hasVppId = state.currentIdentity?.profile?.vppId != null,
                    paddingBottom = paddingValues.calculateBottomPadding()
                )
            }
        }
    }
}

@Composable
@Preview
private fun RoomSearchPreview() {
    val school = School.generateRandomSchools(1).first()
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