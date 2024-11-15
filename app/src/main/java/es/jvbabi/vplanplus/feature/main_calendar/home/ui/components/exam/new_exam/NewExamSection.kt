package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.toTransparent

@Composable
fun AddExamItem(
    icon: @Composable () -> Unit = {},
    iconContainerSize: Dp = 48.dp,
    willBeHorizontalScrollable: Boolean = false,
    content: @Composable () -> Unit
) {
    val surface = MaterialTheme.colorScheme.surfaceContainer
    var height by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = if (willBeHorizontalScrollable) 0.dp else 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .onSizeChanged { height = it.height }
        ) {
            if (!willBeHorizontalScrollable) Spacer(Modifier.size(iconContainerSize))
            content()
        }
        Box(
            modifier = Modifier
                .width(iconContainerSize)
                .height(LocalDensity.current.run { height.toDp() })
                .then(if (willBeHorizontalScrollable) Modifier.drawWithContent {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(surface, surface, surface.toTransparent()),
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                        )
                    )
                    drawContent()
                } else Modifier),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                Modifier.size(iconContainerSize),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}