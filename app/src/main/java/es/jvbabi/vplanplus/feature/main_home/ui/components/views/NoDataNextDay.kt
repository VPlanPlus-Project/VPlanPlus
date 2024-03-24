package es.jvbabi.vplanplus.feature.main_home.ui.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NoDataNextDay(
    modifier: Modifier = Modifier,
    date: LocalDate
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface,
            imageVector = Icons.Default.HourglassEmpty,
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.home_noDataNextDayTitle),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.timetable_noDataText, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))),
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}