package es.jvbabi.vplanplus.feature.main_timetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun Weekend(date: LocalDate) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.timetable_weekend),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium
        )
        val today = LocalDate.now()
        if (today.getWeekNumber() == date.getWeekNumber() && today.dayOfWeek.value >= DayOfWeek.SATURDAY.value && date.dayOfWeek.value >= DayOfWeek.SATURDAY.value) {
            Text(
                text = stringResource(id = R.string.timetable_weekendToday),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (date.getWeekNumber() < today.getWeekNumber()) {
            Text(
                text = stringResource(id = R.string.timetable_weekendPast),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (date.getWeekNumber() == today.getWeekNumber()) {
            Text(
                text = pluralStringResource(id = R.plurals.timetable_weekendFutureThisWeek, today.dayOfWeek.value - date.dayOfWeek.value, today.dayOfWeek.value - date.dayOfWeek.value),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = stringResource(id = R.string.timetable_weekendFuture),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun LocalDate.getWeekNumber(): Int {
    val firstDayOfYear = LocalDate.of(year, 1, 1)
    val daysFromFirstDay = dayOfYear - firstDayOfYear.dayOfYear
    val firstDayOfYearDayOfWeek = firstDayOfYear.dayOfWeek.value
    val adjustment = when {
        firstDayOfYearDayOfWeek <= 4 -> firstDayOfYearDayOfWeek - 1
        else -> 8 - firstDayOfYearDayOfWeek
    }
    return (daysFromFirstDay + adjustment) / 7 + 1
}
