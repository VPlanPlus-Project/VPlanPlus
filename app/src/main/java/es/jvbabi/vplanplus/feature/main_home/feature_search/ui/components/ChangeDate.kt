package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDate(
    selectedDate: LocalDate,
    onSetDate: (LocalDate?) -> Unit = {},
) {
    val context = LocalContext.current
    var showDateDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.search_resultDate, DateUtils.localizedRelativeDate(context, selectedDate, true) ?: "?"),
            style = MaterialTheme.typography.labelMedium,
        )
        TextButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { showDateDialog = true }
        ) {
            Text(text = stringResource(id = R.string.search_resultDateChange))
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
    ChangeDate(LocalDate.now())
}