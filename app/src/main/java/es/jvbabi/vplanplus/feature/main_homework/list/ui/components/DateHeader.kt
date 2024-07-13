package es.jvbabi.vplanplus.feature.main_homework.list.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.util.formatDayDuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateHeader(
    date: LocalDate,
    onOpenInHome: () -> Unit = {},
    onAddHomework: () -> Unit = {}
) {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .5f))
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val bg =
            if (date.isBefore(LocalDate.now())) MaterialTheme.colorScheme.errorContainer
            else if (date.isAfter(LocalDate.now())) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.primaryContainer

        val fg =
            if (date.isBefore(LocalDate.now())) MaterialTheme.colorScheme.onErrorContainer
            else if (date.isAfter(LocalDate.now())) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onPrimaryContainer
        RowVerticalCenter(modifier = Modifier.align(Alignment.CenterStart), horizontalArrangement = Arrangement.spacedBy(8.dp)) date@{
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = fg
                )
            }
            Text(
                text = date.format(DateTimeFormatter.ofPattern("LLL yy")),
                style = MaterialTheme.typography.bodyMedium,
                color = fg
            )
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(text = LocalDate.now().formatDayDuration(compareTo = date), style = MaterialTheme.typography.labelMedium)
        }
        RowVerticalCenter(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(onClick = onOpenInHome) {
                Icon(imageVector = Icons.AutoMirrored.Default.OpenInNew, contentDescription = null)
            }
            IconButton(onClick = onAddHomework) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
@Preview
private fun DateHeaderYesterdayPreview() {
    DateHeader(LocalDate.now().minusDays(1))
}

@Composable
@Preview
private fun DateHeaderTodayPreview() {
    DateHeader(LocalDate.now())
}

@Composable
@Preview
private fun DateHeaderTomorrowPreview() {
    DateHeader(LocalDate.now().plusDays(1))
}