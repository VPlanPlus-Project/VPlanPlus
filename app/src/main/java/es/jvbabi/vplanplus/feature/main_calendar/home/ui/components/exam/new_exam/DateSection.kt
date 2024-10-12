package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSection(
    selectedDate: LocalDate?,
    isVisible: Boolean = true,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_until),
                subtitle = selectedDate?.let { date ->
                    buildString {
                        DateUtils.localizedRelativeDate(LocalContext.current, date, false)?.let {
                            append(it)
                            append(", ")
                        }
                        append(date.format(DateTimeFormatter.ofPattern("EEEE, dd. MMM yyyy")))
                    }
                } ?: stringResource(R.string.examsNew_until_noDate),
                icon = Icons.Default.CalendarToday,
                onClick = onHeaderClicked
            )
        },
        isVisible = isVisible,
        isContentExpanded = isContentExpanded
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer8Dp()
            val context = LocalContext.current
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = DateUtils.getDateFromTimestamp(utcTimeMillis / 1000)
                        return date.isAfter(LocalDate.now())
                    }
                },
                initialSelectedDateMillis = selectedDate?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
            )

            LaunchedEffect(key1 = datePickerState.selectedDateMillis) {
                val millis = datePickerState.selectedDateMillis ?: return@LaunchedEffect
                val date = DateUtils.getDateFromTimestamp(millis / 1000)
                if (date == selectedDate) return@LaunchedEffect
                onDateSelected(date)
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer8Dp() }
                items(6) {
                    val date = LocalDate.now().plusDays(it + 1L)
                    FilterChip(
                        onClick = { onDateSelected(date) },
                        label = {
                            Text(text = DateUtils.localizedRelativeDate(context, date, false) ?: date.format(
                                DateTimeFormatter.ofPattern("EEE, dd. MMM yyyy")))
                        },
                        selected = selectedDate?.isEqual(date) == true
                    )
                }
            }
            DatePicker(state = datePickerState)
        }
    }
}