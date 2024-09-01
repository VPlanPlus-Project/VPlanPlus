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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import es.jvbabi.vplanplus.util.blendColor
import java.time.LocalDate
import java.time.Month

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
    var widthDp by remember(width) { mutableStateOf(localDensity.run { width.toDp() }) }
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
                },

                )
        }
        Box(
            modifier = Modifier
                .then(
                    if (state == DayDisplayState.DETAILED) Modifier.weight(16f)
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
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Top,

            ) {
                repeat(homework) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .then(
                                if (state == DayDisplayState.DETAILED) Modifier
                                    .height(4.dp + progress * 8.dp)
                                    .width(4.dp + (widthDp - 4.dp) * progress)
                                else Modifier.size(4.dp)
                            )
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                    )
                }
                repeat(exams) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .then(
                                if (state == DayDisplayState.DETAILED) Modifier
                                    .height(4.dp + progress * 8.dp)
                                    .width(4.dp + (widthDp - 4.dp) * progress)
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