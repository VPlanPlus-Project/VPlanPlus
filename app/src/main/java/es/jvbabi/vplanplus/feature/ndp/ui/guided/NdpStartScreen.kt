package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.util.blendColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.ceil

@Composable
fun NdpStartScreenContent(
    date: LocalDate,
    enabled: Boolean,
    homework: Int,
    assessments: Int,
    onStart: () -> Unit
) {
    val animationOffset = rememberInfiniteTransition(label = "animation")
    val offset by animationOffset.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animationOffset"
    )
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(clip = true)
            .drawWithContent {
                val color = blendColor(colorScheme.surface, colorScheme.surfaceVariant, .5f)
                val rect = Size(16.dp.toPx(), 32.dp.toPx())
                val xGap = 6.dp.toPx()
                val yGap = 10.dp.toPx()
                repeat(ceil(size.width/rect.width).toInt()) { x ->
                    repeat(ceil(size.height/rect.height).toInt() + 1) { y ->
                        drawLine(
                            color = color,
                            start = Offset(
                                x = (x + offset -1) * (rect.width+xGap) + rect.width / 2,
                                y = y * (rect.height+yGap)
                            ),
                            end = Offset(
                                x = (x + offset) * (rect.width + xGap),
                                y = y * (rect.height + yGap) + rect.height / 2
                            ),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = color,
                            start = Offset(
                                x = (x + offset) * (rect.width + xGap),
                                y = y * (rect.height + yGap) + rect.height / 2
                            ),
                            end = Offset(
                                x = (x + offset - 1) * (rect.width + xGap) + rect.width / 2,
                                y = y * (rect.height + yGap) + rect.height
                            ),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                drawContent()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.ndp_guidedWelcomeHeadline),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer4Dp()
            Text(
                text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer4Dp()
            Text(
                text = pluralStringResource(R.plurals.ndp_guidedWelcomeHomework, homework, homework),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = pluralStringResource(R.plurals.ndp_guidedWelcomeAssessment, assessments, assessments),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onStart,
            enabled = enabled,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
            Spacer4Dp()
            Text(stringResource(R.string.ndp_guidedStartButton))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NdpStartScreenPreview() {
    NdpStartScreenContent(
        date = LocalDate.now().plusDays(1),
        enabled = false,
        homework = 2,
        assessments = 1
    ) {}
}