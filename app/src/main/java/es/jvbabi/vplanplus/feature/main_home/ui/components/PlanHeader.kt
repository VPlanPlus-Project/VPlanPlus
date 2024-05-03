package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun PlanHeader(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    currentDate: LocalDate,
    onOpenDateSelector: () -> Unit = {},
    onSetSelectedDate: (date: LocalDate) -> Unit = {}
) {
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
            TextButton(
                modifier = Modifier.padding(end = 16.dp),
                onClick = onOpenDateSelector
            ) {
                Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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

@Composable
private fun LocalDate.formatDayDuration(compareTo: LocalDate): String {
    return DateUtils.localizedRelativeDate(LocalContext.current, compareTo, false) ?: run {
        if (compareTo.isAfter(this)) return stringResource(id = R.string.home_inNDays, this.until(compareTo, ChronoUnit.DAYS))
        else return stringResource(id = R.string.home_NdaysAgo, compareTo.until(this, ChronoUnit.DAYS))
    }
}