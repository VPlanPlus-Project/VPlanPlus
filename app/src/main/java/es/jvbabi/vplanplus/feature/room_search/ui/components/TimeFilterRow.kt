package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun TimeFilterRow(
    showCurrentLesson: Boolean,
    isCurrentLessonEnabled: Boolean,
    onToggleCurrentLesson: () -> Unit,

    showNextLesson: Boolean,
    isNextLessonEnabled: Boolean,
    onToggleNextLesson: () -> Unit,
) {
    AnimatedVisibility(
        visible = showCurrentLesson || showNextLesson,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        LazyRow(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
}