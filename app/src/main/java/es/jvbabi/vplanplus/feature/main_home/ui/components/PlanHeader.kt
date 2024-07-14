package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import es.jvbabi.vplanplus.util.formatDayDuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PlanHeader(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    currentDate: LocalDate,
    onSetSelectedDate: (date: LocalDate) -> Unit = {}
) {
    var isMonthSelectorOpen by rememberSaveable { mutableStateOf(false) }
    Column(modifier) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) header@{
            Column information@{
                Row(verticalAlignment = Alignment.CenterVertically) title@{
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 4.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.home_planHeader),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
                val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
                Text(
                    text = formattedDate + " $DOT " + currentDate.formatDayDuration(compareTo = selectedDate),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(end = 16.dp),
            ) {
                TextButton(
                    onClick = { isMonthSelectorOpen = true }
                ) {
                    Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isMonthSelectorOpen,
                    onDismissRequest = { isMonthSelectorOpen = false }
                ) {
                    repeat(12) { month ->
                        val date = LocalDate.now().plusMonths(month - 6L).withDayOfMonth(1)
                        DropdownMenuItem(
                            text = { Text(text = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))) },
                            onClick = { onSetSelectedDate(date); isMonthSelectorOpen = false }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    onSetSelectedDate(selectedDate.minusWeeks(1L).withDayOfWeek(1))
                },
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.home_calendarWeek, selectedDate.minusWeeks(1L).format(DateTimeFormatter.ofPattern("w")).toInt())
                )
            }
            FilledTonalButton(
                onClick = { onSetSelectedDate(currentDate) },
                enabled = !selectedDate.isEqual(currentDate),
            ) {
                Text(text = stringResource(id = R.string.home_backToToday))
            }
            TextButton(
                onClick = {
                    onSetSelectedDate(selectedDate.plusWeeks(1L).withDayOfWeek(1))
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_calendarWeek, selectedDate.plusWeeks(1L).format(DateTimeFormatter.ofPattern("w")).toInt())
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanHeaderPreview() {
    PlanHeader(selectedDate = LocalDate.now().plusDays(1L), currentDate = LocalDate.now())
}