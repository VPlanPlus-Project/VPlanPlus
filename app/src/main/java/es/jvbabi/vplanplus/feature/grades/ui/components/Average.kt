package es.jvbabi.vplanplus.feature.grades.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import java.math.RoundingMode

@Composable
fun Average(
    modifier: Modifier = Modifier,
    avg: Double
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .padding(16.dp)
            .size(128.dp)
            .clip(RoundedCornerShape(50))
            .drawWithContent {
                val percentage = (6 - avg) / 5f
                drawRect(
                    color = colorScheme.secondary,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            colorScheme.primary,
                            colorScheme.tertiary
                        )
                    ),
                    topLeft = Offset(
                        0f,
                        size.height * (1 - percentage.toFloat())
                    ),
                    size = Size(size.width, size.height * percentage.toFloat())
                )
                drawContent()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ø ${
                avg.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
            }",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}