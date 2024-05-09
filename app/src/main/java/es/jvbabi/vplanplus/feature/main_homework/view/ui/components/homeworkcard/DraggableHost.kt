package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.homeworkcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DraggableHost(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    iconLeft: ImageVector,
    colorLeft: Color = MaterialTheme.colorScheme.error,
    onColorLeft: Color = MaterialTheme.colorScheme.onError,
    iconRight: ImageVector,
    colorRight: Color = Color.Gray,
    onColorRight: Color,
    onDragToLeft: () -> Unit,
    onDragToRight: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box {
        var offsetX by remember { mutableFloatStateOf(0f) }
        val displayOffset = animateFloatAsState(targetValue = offsetX, label = "Drag Card")
        var isDragging by remember { mutableStateOf(false) }
        var width by remember { mutableFloatStateOf(0f) }
        Box(
            modifier
                .padding(start = 4.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .drawWithContent {
                    width = size.width
                    val blendFactor = (offsetX / (width / 3))
                    val backgroundColour =
                        if (blendFactor > 0) Color(
                            ColorUtils.blendARGB(
                                background.toArgb(),
                                colorLeft.toArgb(),
                                minOf(blendFactor, 1f)
                            )
                        ) else Color(
                            ColorUtils.blendARGB(
                                background.toArgb(),
                                colorRight.toArgb(),
                                minOf(abs(blendFactor), 1f)
                            )
                        )
                    drawRect(
                        color = backgroundColour,
                        topLeft = Offset(0f, 0f),
                        size = Size(this.size.width, this.size.height)
                    )
                    drawContent()
                }) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(62.dp)
                    .padding(start = 20.dp)
                    .alpha(minOf(maxOf(offsetX / (width / 3), 0f), 1f)),
                imageVector = iconLeft,
                contentDescription = null,
                tint = onColorLeft
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(62.dp)
                    .padding(end = 20.dp)
                    .alpha(minOf(maxOf(-offsetX / (width / 3), 0f), 1f)),
                imageVector = iconRight,
                contentDescription = null,
                tint = onColorRight
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        if (isDragging) IntOffset(offsetX.roundToInt(), 0)
                        else IntOffset(displayOffset.value.roundToInt(), 0)
                    }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetX += delta
                        },
                        onDragStopped = {
                            isDragging = false
                            if (offsetX in width / 3..width) {
                                onDragToLeft()
                            }
                            if (offsetX in -width..-width / 3) {
                                onDragToRight()
                            }
                            offsetX = 0f
                        },
                        onDragStarted = {
                            isDragging = true
                        }
                    )
            ) {
                content()
            }
        }
    }
}