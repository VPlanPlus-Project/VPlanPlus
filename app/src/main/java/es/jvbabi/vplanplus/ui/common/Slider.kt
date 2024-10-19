package es.jvbabi.vplanplus.ui.common

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.Size
import es.jvbabi.vplanplus.util.nearest
import es.jvbabi.vplanplus.util.size

@Composable
fun Slider(
    orientation: Orientation,
    currentValue: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Float,
    trackThickness: Dp = 2.dp,
    puckSize: Size,
    puckPadding: Dp = 2.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    puckColor: Color = MaterialTheme.colorScheme.primaryContainer,

    puckContent: (@Composable (value: Float) -> Unit)? = null,
    onValueChange: (Float) -> Unit,
) {
    var sliderSize by remember { mutableStateOf(IntSize(0, 0)) }
    var currentDraggingValue by rememberSaveable { mutableFloatStateOf(currentValue) }
    var isDragging by rememberSaveable { mutableStateOf(false) }
    val animatedPuckPosition by animateFloatAsState(if (isDragging) currentDraggingValue else currentValue, label = "animated slider")
    Box(
        modifier = Modifier
            .then(
                when (orientation) {
                    Orientation.Horizontal -> Modifier
                        .fillMaxWidth()
                        .padding(horizontal = puckSize.width / 2 + puckPadding)

                    Orientation.Vertical -> Modifier
                        .fillMaxHeight()
                        .padding(vertical = puckSize.height / 2 + puckPadding)
                }
            )
    ) sliderContainer@{
        Box(
            modifier = Modifier
                .then(
                    when (orientation) {
                        Orientation.Horizontal -> Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .height(48.dp)

                        Orientation.Vertical -> Modifier
                            .fillMaxHeight()
                            .align(Alignment.TopCenter)
                            .width(48.dp)
                    }
                )
                .onSizeChanged { sliderSize = it }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            onValueChange(
                                (
                                        when (orientation) {
                                            Orientation.Horizontal -> (offset.x / sliderSize.width)
                                            Orientation.Vertical -> (offset.y / sliderSize.height)
                                        } * (range.endInclusive - range.start) + range.start
                                        )
                                    .nearest(steps)
                                    .coerceIn(range.start, range.endInclusive)
                            )
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .then(
                        when (orientation) {
                            Orientation.Horizontal -> Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = puckSize.width / 2)
                                .height(trackThickness)
                                .fillMaxWidth()

                            Orientation.Vertical -> Modifier
                                .align(Alignment.TopCenter)
                                .padding(vertical = puckSize.height / 2)
                                .width(trackThickness)
                                .fillMaxHeight()
                        }
                    )
                    .clip(RoundedCornerShape(50))
                    .background(trackColor)
            )
            Box(
                modifier = Modifier
                    .then(
                        when (orientation) {
                            Orientation.Horizontal -> Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = puckSize.width / 2)
                                .height(trackThickness)
                                .fillMaxWidth(((if (isDragging) currentDraggingValue else animatedPuckPosition) - range.start) / (range.endInclusive - range.start))

                            Orientation.Vertical -> Modifier
                                .align(Alignment.TopCenter)
                                .padding(vertical = puckSize.height / 2)
                                .width(trackThickness)
                                .fillMaxHeight(((if (isDragging) currentDraggingValue else animatedPuckPosition) - range.start) / (range.endInclusive - range.start))
                        }
                    )
                    .clip(RoundedCornerShape(50))
                    .background(activeTrackColor)
            )
            Box(
                modifier = Modifier
                    .size(puckSize.withMinSize(30.dp))
                    .then(
                        when (orientation) {
                            Orientation.Horizontal -> Modifier.align(Alignment.CenterStart)
                            Orientation.Vertical -> Modifier.align(Alignment.TopCenter)
                        }
                    )
                    .offset {
                        when (orientation) {
                            Orientation.Horizontal -> IntOffset(
                                x = ((sliderSize.width / (range.endInclusive - range.start)) * (
                                        if (isDragging) currentDraggingValue - range.start else animatedPuckPosition - range.start
                                        ).toInt() - (puckSize.withMinSize(
                                    30.dp
                                ).width.toPx() / 2)).toInt(),
                                y = 0
                            )

                            Orientation.Vertical -> IntOffset(
                                x = 0,
                                y = ((sliderSize.height / (range.endInclusive - range.start)) * (
                                        if (isDragging) currentDraggingValue - range.start else animatedPuckPosition - range.start
                                        ).toInt() - (puckSize.withMinSize(
                                    30.dp
                                ).height.toPx() / 2)).toInt()
                            )
                        }

                    }
                    .pointerInput(currentValue) {
                        detectDragGestures(
                            onDragStart = { currentDraggingValue = currentValue; isDragging = true },
                            onDragEnd = {
                                isDragging = false; currentDraggingValue =
                                currentDraggingValue.nearest(steps); onValueChange(
                                currentDraggingValue
                            )
                            },
                            onDragCancel = {
                                isDragging = false; currentDraggingValue =
                                currentDraggingValue.nearest(steps)
                            }
                        ) { change, dragAmount ->
                            Log.d("Slider", "DragAmount: $dragAmount, currentDraggingValue: $currentDraggingValue, currentValue: $currentValue")
                            change.consume()
                            currentDraggingValue =
                                (currentDraggingValue + (when (orientation) {
                                    Orientation.Horizontal -> (dragAmount.x / sliderSize.width)
                                    Orientation.Vertical -> (dragAmount.y / sliderSize.height)
                                }) * (range.endInclusive - range.start)
                                        )
                                    .coerceIn(
                                        range.start,
                                        range.endInclusive
                                    )
                        }
                    },
                contentAlignment = Alignment.Center
            ) puckDrag@{
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(puckSize.withPadding(8.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) puckVisiblePadding@{
                    Box(
                        modifier = Modifier
                            .size(puckSize)
                            .clip(RoundedCornerShape(50))
                            .background(puckColor),
                        contentAlignment = Alignment.Center
                    ) {
                        puckContent?.invoke(if (isDragging) currentDraggingValue else animatedPuckPosition)
                    }
                }
            }
        }
    }
}