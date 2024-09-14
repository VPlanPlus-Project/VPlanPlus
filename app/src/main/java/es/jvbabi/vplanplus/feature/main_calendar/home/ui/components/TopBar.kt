package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun TopBar(
    onBack: () -> Unit = {},
    selectDate: (date: LocalDate) -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.calendar_title)) },
        navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
        actions = {
            TodayButton(selectDate)
        }
    )
}

@Composable
fun TodayButton(selectDate: (date: LocalDate) -> Unit) {
    Box(
        modifier = Modifier.height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxHeight()) {
            Spacer(Modifier.weight(.17f, true))
            Box(
                modifier = Modifier.weight(.83f, true),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = LocalDate.now().dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        IconButton(onClick = { selectDate(LocalDate.now()) }) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
        }
    }
}