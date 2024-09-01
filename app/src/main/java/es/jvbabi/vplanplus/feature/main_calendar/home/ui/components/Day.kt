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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
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
        Box(Modifier.height(16.dp)) {
            FlowRow(
                modifier = Modifier
                    .width(32.dp)
                    .height(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Top
            ) {
                repeat(homework) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                    )
                }
                repeat(exams) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(4.dp)
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

enum class DayDisplayState {
    SMALL,
    REGULAR,
    DETAILED
}