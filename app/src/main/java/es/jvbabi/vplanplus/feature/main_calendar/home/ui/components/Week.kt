package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.time.Month
import kotlin.math.abs

@Composable
fun ColumnScope.Week(
    days: List<SchoolDay>,
    selectedDay: LocalDate?,
    onDayClicked: (date: LocalDate) -> Unit = {},
    checkIfDayIsSelectable: (day: SchoolDay) -> Boolean = { true },
    state: DayDisplayState,
    progress: Float,
    smallMaxHeight: Dp,
    mediumMaxHeight: Dp,
    largeMaxHeight: Dp?,
    displayMonth: Month?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (largeMaxHeight == null) Modifier.weight(1f, true) else Modifier.height(
                    when (state) {
                        DayDisplayState.SMALL -> smallMaxHeight
                        DayDisplayState.REGULAR -> smallMaxHeight + (mediumMaxHeight - smallMaxHeight) * abs(progress)
                        DayDisplayState.DETAILED -> mediumMaxHeight + (largeMaxHeight - mediumMaxHeight) * abs(progress)
                    }
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Day(
                    date = day.date,
                    homework = day.homework.filter { it.homework.until.toLocalDate() == day.date },
                    exams = day.exams.filter { it.date == day.date },
                    isToday = day.date == LocalDate.now(),
                    isSelected = day.date == selectedDay,
                    isClickable = checkIfDayIsSelectable(day),
                    displayMonth = displayMonth,
                    onClick = { onDayClicked(day.date) },
                    state = state,
                    progress = progress
                )
            }
        }
    }
}

@Composable
@Preview
private fun WeekPreview() {
    Column {
        Week(
            days = List(7) { index ->
                SchoolDay(
                    date = LocalDate.now().atStartOfWeek().plusDays(index.toLong()),
                )
            },
            selectedDay = LocalDate.now(),
            onDayClicked = {},
            state = DayDisplayState.SMALL,
            progress = 1f,
            smallMaxHeight = 64.dp,
            mediumMaxHeight = 120.dp,
            largeMaxHeight = 200.dp,
            displayMonth = LocalDate.now().atStartOfWeek().month
        )
    }
}