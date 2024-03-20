package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NextWeek
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ToggleButtons(
    isTodaySelected: Boolean,
    nextDate: LocalDate?,
    onTodaySelect: () -> Unit,
    onNextDaySelect: () -> Unit
) {
    SegmentedButtons(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                    )
                )
            )
            .padding(8.dp)
    ) {
        SegmentedButtonItem(
            selected = isTodaySelected,
            onClick = onTodaySelect,
            label = { Text(text = stringResource(id = R.string.home_planTodayToggle)) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Work,
                    contentDescription = null
                )
            }
        )
        SegmentedButtonItem(
            selected = !isTodaySelected,
            onClick = onNextDaySelect,
            label = {
                Text(
                    nextDate?.format(DateTimeFormatter.ofPattern("EEEE"))
                        ?: "-"
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.NextWeek,
                    contentDescription = null
                )
            }
        )
    }
}