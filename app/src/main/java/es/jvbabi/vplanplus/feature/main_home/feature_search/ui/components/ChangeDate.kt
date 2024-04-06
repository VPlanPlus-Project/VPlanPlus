package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDate(
    selectedDate: LocalDate,
    onSetDate: (LocalDate?) -> Unit = {},
) {
    val context = LocalContext.current
    var showDateDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onSetDate(selectedDate.minusDays(1L)) }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, true)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.search_resultDate, DateUtils.localizedRelativeDate(context, selectedDate, true) ?: "?"),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Row {
                AnimatedVisibility(
                    enter = fadeIn(tween(250)),
                    exit = fadeOut(tween(250)),
                    visible = !selectedDate.isEqual(LocalDate.now())
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp),
                        onClick = { onSetDate(LocalDate.now()) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.undo),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(id = R.string.home_backToToday)
                        )
                    }
                }
                IconButton(
                    onClick = { showDateDialog = true },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.EditCalendar, contentDescription = stringResource(id = R.string.search_resultDateChange))
                }
                VerticalDivider(Modifier.padding(start = 8.dp).height(32.dp))
            }
        }
        IconButton(
            onClick = { onSetDate(selectedDate.plusDays(1L)) },
        ) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
        }
    }

    if (showDateDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.getDateFromTimestamp(selectedDate.toEpochDay() * 24 * 60 * 60).toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val date =
                        if (datePickerState.selectedDateMillis == null) null
                        else DateUtils.getDateFromTimestamp(datePickerState.selectedDateMillis!! / 1000)
                    onSetDate(date)
                    showDateDialog = false
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ChangeDatePreview() {
    ChangeDate(LocalDate.now().plusDays(Random.nextLong(-10, 10)))
}