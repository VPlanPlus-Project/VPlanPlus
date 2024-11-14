package es.jvbabi.vplanplus.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.toDp
import kotlin.math.roundToInt

@Composable
fun MultiFab(
    isVisible: Boolean,
    items: List<MultiFabItem>,
    fabPosition: Offset,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            // Full screen dimensions
            val fullWidth = maxWidth
            val fullHeight = maxHeight

            Box(
                modifier = Modifier
                    .width(fullWidth)
                    .height(fullHeight)
                    .background(Color.Black.copy(alpha = .75f)) // Fills the entire screen with red
                    .noRippleClickable { onDismiss() }
            ) {
                var fabsSize by remember { mutableStateOf(IntSize(width = 0, height = 0)) }
                var mainFabSize by remember { mutableStateOf(IntSize(width = 0, height = 0)) }
                Column(
                    modifier = Modifier
                        .onSizeChanged {
                            fabsSize = it
                        }
                        .offset {
                            IntOffset(
                                x = (fabPosition.x - fabsSize.width + mainFabSize.width).roundToInt(),
                                y = (fabPosition.y - fabsSize.height).roundToInt()
                            )
                        }
                        .padding(bottom = 16.dp, end = 4.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEachIndexed { i, item ->
                        var isItemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = isVisible) {
                            isItemVisible = isVisible
                        }
                        AnimatedVisibility(
                            visible = isItemVisible,
                            enter = fadeIn(tween(delayMillis = (DefaultDurationMillis/4) * (items.size - i))),
                            exit = fadeOut()
                        ) {
                            RowVerticalCenter {
                                Text(
                                    text = item.text,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(4.dp)
                                )
                                item.textSuffix?.let {
                                    Spacer4Dp()
                                    it()
                                }
                                Spacer8Dp()
                                SmallFloatingActionButton(
                                    onClick = {
                                        onDismiss()
                                        item.onClick()
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ) {
                                    item.icon()
                                }
                            }
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .offset(x = fabPosition.x.toDp(), y = fabPosition.y.toDp())
                        .onSizeChanged { mainFabSize = it }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.rotate(animateFloatAsState(if (isVisible) 180+45f else 0f,  label = "close button").value)
                    )
                }
            }
        }
    }
}

data class MultiFabItem(
    val icon: @Composable () -> Unit,
    val text: String,
    val textSuffix: (@Composable () -> Unit)? = null,
    val onClick: () -> Unit
)