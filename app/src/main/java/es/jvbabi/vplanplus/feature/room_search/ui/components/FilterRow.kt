package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun FilterRow(
    showCurrentLesson: Boolean,
    isCurrentLessonEnabled: Boolean,
    onToggleCurrentLesson: () -> Unit,

    showNextLesson: Boolean,
    isNextLessonEnabled: Boolean,
    onToggleNextLesson: () -> Unit,

    onToggleMyBookings: () -> Unit,
    isMyBookingsEnabled: Boolean
) {
    LazyRow(
        modifier = Modifier
            .padding(start = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            FilterChip(
                selected = isMyBookingsEnabled,
                onClick = onToggleMyBookings,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.BookmarkAdded, contentDescription = null)
                },
                label = { Text(text = stringResource(id = R.string.searchAvailableRoom_filterMyBookings)) }
            )
        }

        if (showCurrentLesson || showNextLesson) item { VerticalDivider(Modifier.height(32.dp)) }

        if (showCurrentLesson) {
            item {
                FilterChip(
                    selected = isCurrentLessonEnabled,
                    onClick = onToggleCurrentLesson,
                    label = { Text(text = stringResource(id = R.string.searchAvailableRoom_filterNow)) }
                )
            }
        }
        if (showNextLesson) {
            item {
                FilterChip(
                    selected = isNextLessonEnabled,
                    onClick = onToggleNextLesson,
                    label = { Text(text = stringResource(id = R.string.searchAvailableRoom_filterNext)) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilterRowPreview() {
    FilterRow(
        showCurrentLesson = true,
        isCurrentLessonEnabled = true,
        onToggleCurrentLesson = {},
        showNextLesson = true,
        isNextLessonEnabled = false,
        onToggleNextLesson = {},
        onToggleMyBookings = {},
        isMyBookingsEnabled = false
    )
}