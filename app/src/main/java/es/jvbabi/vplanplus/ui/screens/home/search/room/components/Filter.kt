package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
@ExperimentalMaterial3Api
fun SearchField(
    value: String,
    onRoomFilterValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null
            )
        },
        placeholder = { Text(text = stringResource(id = R.string.searchAvailableRoom_findPlaceholder)) },
        value = value,
        onValueChange = { onRoomFilterValueChanged(it) }
    )
}

@Composable
fun FilterChips(
    filterNowActive: Boolean,
    filterNextActive: Boolean,
    filterNowToggled: () -> Unit,
    filterNextToggled: () -> Unit,
    filterNowTimespan: Pair<LocalDateTime, LocalDateTime>?,
    filterNextTimespan: Pair<LocalDateTime, LocalDateTime>?,
    showNowFilter: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.searchAvailableRoom_labelAvailability),
            modifier = Modifier.padding(end = 4.dp)
        )

        var labelNow = stringResource(id = R.string.searchAvailableRoom_filterNow)
        if (filterNowTimespan != null) {
            labelNow += " (${filterNowTimespan.first.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${filterNowTimespan.second.format(DateTimeFormatter.ofPattern("HH:mm"))})"
        }
        if (showNowFilter) FilterChip(
            enabled = true,
            selected = filterNowActive,
            leadingIcon = {
                Icon(
                    imageVector = if (filterNowActive) Icons.Default.Check else Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { filterNowToggled() },
            modifier = Modifier.padding(horizontal = 4.dp),
            label = { Text(text = labelNow) },
        )

        var labelNext = stringResource(id = R.string.searchAvailableRoom_filterNext)
        if (filterNextTimespan != null) {
            labelNext += " (${filterNextTimespan.first.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${filterNextTimespan.second.format(DateTimeFormatter.ofPattern("HH:mm"))})"
        }
        FilterChip(
            enabled = true,
            selected = filterNextActive,
            leadingIcon = {
                Icon(
                    imageVector = if (filterNextActive) Icons.Default.Check else Icons.Default.MoreTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { filterNextToggled() },
            modifier = Modifier.padding(horizontal = 4.dp),
            label = { Text(text = labelNext) },
        )
    }
}