package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import es.jvbabi.vplanplus.util.blendColor
import java.time.LocalDate
import java.time.Month

private const val DOT_WIDTH_DP = 4
private const val DOT_HEIGHT_DP = 4
private const val CAPSULE_THICKNESS_DP = 12
private const val CAPSULE_TEXT_PADDING_DP = 4

/**
 * A single day in the calendar.
 * @param displayMonth The month that is currently displayed primarily. If the day is not in this month, it will be grayed out.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Day(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    homework: Int,
    exams: Int,
    displayMonth: Month?,
    state: DayDisplayState,
    progress: Float,
    onClick: () -> Unit = {}
) {
    val localDensity = LocalDensity.current
    var width by remember { mutableFloatStateOf(0f) }
    val widthDp by remember(width) { mutableStateOf(localDensity.run { width.toDp() }) }

    val dpToPx: Dp.() -> Float = { localDensity.run { toPx() } }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .onSizeChanged { width = it.width.toFloat() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state == DayDisplayState.DETAILED) Spacer(modifier = Modifier.weight(8f))
        else Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .then(
                    if (state == DayDisplayState.DETAILED) Modifier.weight(32f)
                    else Modifier.height(32.dp)
                )
                .clip(RoundedCornerShape(50))
                .then(
                    if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                    else Modifier
                )
                .then(
                    if (isToday && !isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(50))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            val normalColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface

            val colorRegardingSelectedMonth =
                if (displayMonth != null && displayMonth != date.month) Color.Gray
                else normalColor
            Text(
                text = date.dayOfMonth.toString(),
                color =
                when (state) {
                    DayDisplayState.SMALL -> normalColor
                    DayDisplayState.REGULAR -> blendColor(normalColor, colorRegardingSelectedMonth, progress)
                    else -> colorRegardingSelectedMonth
                }
            )
        }
        Box(
            modifier = Modifier
                .then(
                    if (state == DayDisplayState.DETAILED) Modifier
                        .height(16.dp + (widthDp - 16.dp) * progress)
                        .rotate(90f * progress)
                    else Modifier.height(16.dp)
                )
        ) {
            FlowRow(
                modifier = Modifier
                    .then(
                        if (state == DayDisplayState.DETAILED) Modifier.width(32.dp + (widthDp - 32.dp) * progress)
                        else Modifier.width(32.dp)
                    )
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top
            ) {
                repeat(homework) {
                    val textMeasurer = rememberTextMeasurer()
                    val measuredText =
                        textMeasurer.measure(
                            AnnotatedString("HA"),
                            constraints = Constraints.fixedWidth((width - (2 * CAPSULE_TEXT_PADDING_DP.dp.dpToPx())).toInt().coerceAtLeast(0)),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .then(
                                if (state == DayDisplayState.DETAILED) Modifier
                                    .width(DOT_WIDTH_DP.dp + progress * (CAPSULE_THICKNESS_DP - DOT_WIDTH_DP).dp)
                                    .height(DOT_HEIGHT_DP.dp + (widthDp - DOT_HEIGHT_DP.dp) * progress)
                                    .drawWithContent {
                                        drawContent()
                                        rotate(
                                            degrees = -90f,
                                            pivot = Offset(0f, 0f)
                                        ) {
                                            scale(progress, progress, Offset(-size.height + CAPSULE_TEXT_PADDING_DP.dp.dpToPx(), size.width / 2 - measuredText.size.height / 2)) {
                                                translate(left = -size.height + CAPSULE_TEXT_PADDING_DP.dp.dpToPx(), top = size.width / 2 - measuredText.size.height / 2) {
                                                    drawText(measuredText, color = blendColor(Color.Transparent, Color.White, progress))
                                                }
                                            }
                                        }
                                    }
                                else Modifier.size(4.dp)
                            )
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                    )
                }
                repeat(exams) {
                    val textMeasurer = rememberTextMeasurer()
                    val measuredText =
                        textMeasurer.measure(
                            AnnotatedString("Test/KA"),
                            constraints = Constraints.fixedWidth((width - (2 * CAPSULE_TEXT_PADDING_DP.dp.dpToPx())).toInt().coerceAtLeast(0)),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .then(
                                if (state == DayDisplayState.DETAILED) Modifier
                                    .width(DOT_WIDTH_DP.dp + progress * (CAPSULE_THICKNESS_DP - DOT_WIDTH_DP).dp)
                                    .height(DOT_HEIGHT_DP.dp + (widthDp - DOT_HEIGHT_DP.dp) * progress)
                                    .drawWithContent {
                                        drawContent()
                                        rotate(
                                            degrees = -90f,
                                            pivot = Offset(0f, 0f)
                                        ) {
                                            scale(progress, progress, Offset(-size.height + CAPSULE_TEXT_PADDING_DP.dp.dpToPx(), size.width / 2 - measuredText.size.height / 2)) {
                                                translate(left = -size.height + CAPSULE_TEXT_PADDING_DP.dp.dpToPx(), top = size.width / 2 - measuredText.size.height / 2) {
                                                    drawText(measuredText, color = blendColor(Color.Transparent, Color.White, progress))
                                                }
                                            }
                                        }
                                    }
                                else Modifier.size(4.dp)
                            )
                            .clip(RoundedCornerShape(50))
                            .background(Color.Red)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun WeekendPreview() {
    Day(
        date = LocalDate.of(2024, 1, 14),
        isSelected = false,
        isToday = true,
        homework = 0,
        exams = 0,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.SMALL,
        progress = 1f
    )
}

@Composable
@Preview
private fun SelectedPreview() {
    Day(
        date = LocalDate.of(2024, 1, 14),
        isSelected = true,
        isToday = false,
        homework = 2,
        exams = 0,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.SMALL,
        progress = 1f
    )
}

@Composable
@Preview
private fun HomeworkPreview() {
    Day(
        date = LocalDate.of(2024, 2, 14),
        isSelected = false,
        isToday = false,
        homework = 3,
        exams = 2,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.SMALL,
        progress = 1f
    )
}

@Composable
@Preview
private fun DetailedPreview() {
    Day(
        date = LocalDate.of(2024, 2, 14),
        isSelected = false,
        isToday = false,
        homework = 3,
        exams = 2,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.DETAILED,
        progress = 1f
    )
}

enum class DayDisplayState {
    SMALL,
    REGULAR,
    DETAILED
}