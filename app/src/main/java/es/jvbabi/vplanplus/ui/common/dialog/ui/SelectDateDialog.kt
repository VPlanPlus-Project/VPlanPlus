package es.jvbabi.vplanplus.ui.common.dialog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.DateSelectCause
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.FullMonthPager
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SelectDateDialog(
    selectDateViewModel: SelectDateViewModel = hiltViewModel(),
    allowedDays: (day: SchoolDay) -> Boolean = { true },
    title: String,
    onDismiss: () -> Unit,
    onSelectDate: (date: LocalDate) -> Unit
) {
    ComposableDialog(
        icon = Icons.Default.CalendarMonth,
        title = title,
        content = {
            Column(Modifier.fillMaxSize()) {
                LazyRow(Modifier.fillMaxWidth()) {
                    items(5) { offset ->
                        val date = LocalDate.now().plusDays(offset.toLong())
                        val day = selectDateViewModel.state.days[date] ?: SchoolDay(date = date)
                        if (!allowedDays(day)) return@items
                        AssistChip(
                            onClick = { onSelectDate(date) },
                            label = {
                                Text(DateUtils.relativeDateStringResource(LocalDate.now(), date)?.let { stringResource(it) } ?: date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                FullMonthPager(
                    calendarSelectHeightLarge = null,
                    setFirstVisibleDate = {},
                    selectDate = { date, selectCause ->
                        if (selectCause == DateSelectCause.CALENDAR_SWIPE) selectDateViewModel.doAction(SelectDateDialogEvent.SelectDate(date))
                        else onSelectDate(date)
                    },
                    checkIfDayIsSelectable = allowedDays,
                    days = selectDateViewModel.state.days,
                    selectedDate = selectDateViewModel.state.selectedDate
                )
            }
        },
        okEnabled = selectDateViewModel.state.selectedDate != null,
        onOk = { onSelectDate(selectDateViewModel.state.selectedDate!!) },
        onCancel = onDismiss,
        onDismiss = onDismiss,
    )
}