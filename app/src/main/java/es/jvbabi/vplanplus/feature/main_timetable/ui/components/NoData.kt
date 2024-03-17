package es.jvbabi.vplanplus.feature.main_timetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NoData(date: LocalDate) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(id = R.string.timetable_noDataTitle),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium
        )
        if (date.isBefore(LocalDate.now())) {
            Text(
                text = stringResource(id = R.string.timetable_noDataPastText, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = stringResource(id = R.string.timetable_noDataText, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoDataPreview() {
    NoData(LocalDate.now())
}
