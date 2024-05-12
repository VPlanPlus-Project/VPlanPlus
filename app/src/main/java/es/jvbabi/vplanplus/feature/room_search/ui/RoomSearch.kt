package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.feature.room_search.ui.components.TimeInfo
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import java.time.ZonedDateTime
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
                .padding(paddingValues)
        ) {
            val verticalPadding = 4.dp
            val colorScheme = MaterialTheme.colorScheme
            val typography = MaterialTheme.typography

            BoxWithConstraints(Modifier.fillMaxSize()) {
                var scale by rememberSaveable { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val boxState = rememberTransformableState { zoomChange, panChange, _ ->
                    scale = (scale * zoomChange).coerceIn(1f, 5f)

                    val extraWidth = scale * constraints.maxWidth
                    val extraHeight = scale * constraints.maxHeight

                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2

                    offset = Offset(
                        x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                        y = minOf(0f, (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(verticalPadding),
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(boxState),
                ) {
                    val textMeasurer = rememberTextMeasurer()
                    val roomNameWidthDp = 100.dp
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(state.map) {
                                detectTapGestures {
                                    val roomIndex = ceil((it.y - offset.y) / (48.dp + verticalPadding).toPx()).toInt() - 1
                                    val room = state.map
                                        .toList()
                                        .getOrNull(roomIndex)?.first

                                    val minutesOffset = ((it.x / 2) / scale) - ((offset.x / 2) / scale)
                                    if (minutesOffset < 0 || room == null) return@detectTapGestures

                                    val time = ZonedDateTime
                                        .now()
                                        .atStartOfDay()
                                        .plusMinutes(minutesOffset.toLong())

                                    onTapOnMatrix(time, room)
                                }
                            }
                    ) {
                        state.map.toList().forEachIndexed { roomIndex, (room, lessons) ->
                            translate(top = offset.y) {
                                val verticalOffset = if (roomIndex == 0) 0.dp else verticalPadding

                                lessons.forEach { lesson ->
                                    val offsetStart = (lesson.start.atStartOfDay().until(lesson.start, ChronoUnit.MINUTES) * scale * 2) + offset.x
                                    val width = lesson.start.until(lesson.end, ChronoUnit.MINUTES) * scale * 2

                                    drawRect(
                                        color = colorScheme.secondaryContainer,
                                        topLeft = Offset(offsetStart, roomIndex * (48.dp + verticalOffset).toPx()),
                                        size = Size(width, 48.dp.toPx())
                                    )
                                }

                                drawRect(
                                    color = colorScheme.primaryContainer,
                                    topLeft = Offset(0f, roomIndex * (48.dp + verticalOffset).toPx()),
                                    size = Size(roomNameWidthDp.toPx(), 48.dp.toPx())
                                )

                                val measuredRoomName = textMeasurer.measure(
                                    buildAnnotatedString { append(room.name) },
                                    constraints = Constraints.fixedWidth(roomNameWidthDp.roundToPx()),
                                    style = typography.bodyMedium
                                )

                                drawText(
                                    measuredRoomName,
                                    topLeft = Offset(0f, roomIndex * (48.dp + verticalOffset).toPx())
                                )

                            }
                        }
                    }
                }
            }

            val alpha = animateFloatAsState(targetValue = if (state.selectedRoom == null) 0f else 1f, label = "RoomInfo")
            if (state.selectedRoom != null && state.selectedTime != null) Box(Modifier.align(Alignment.BottomCenter)) {
                TimeInfo(
                    modifier = Modifier
                        .padding(16.dp)
                        .alpha(alpha.value),
                    room = state.selectedRoom,
                    selectedTime = state.selectedTime,
                    currentTime = ZonedDateTime.now(),
                    lessons = state.map[state.selectedRoom] ?: emptyList(),
                    onClosed = { onTapOnMatrix(null, null) },
                    onBookRoomClicked = onBookRoomRequested,
                    hasVppId = state.currentIdentity?.profile?.vppId != null
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