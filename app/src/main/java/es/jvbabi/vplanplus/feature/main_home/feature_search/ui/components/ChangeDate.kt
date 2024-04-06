package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                AssistChip(
                    onClick = { showDateDialog = true },
                    label = {
                        Row {
                            Text(text = stringResource(id = R.string.search_resultDateChange))
                            AnimatedVisibility(
                                visible = selectedDate.isAfter(LocalDate.now().plusDays(2)) || selectedDate.isBefore(LocalDate.now()),
                                enter = expandHorizontally(tween(250)),
                                exit = shrinkHorizontally(tween(250))
                            ) {
                                Text(text = " $DOT ${selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
                            }
                        }
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.EditCalendar, contentDescription = null) },
                    trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                VerticalDivider(
                    Modifier
                        .padding(start = 8.dp)
                        .height(32.dp))
            }
            items(3) {
                val date = LocalDate.now().plusDays(it.toLong())
                FilterChip(
                    onClick = { onSetDate(date) },
                    label = { Text(text = DateUtils.localizedRelativeDate(context, date, true) ?: "?") },
                    selected = selectedDate.isEqual(date)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ChangeDatePreview() {
    ChangeDate(LocalDate.now().plusDays(Random.nextLong(-10, 10)))
}