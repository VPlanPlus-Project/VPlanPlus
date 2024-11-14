package es.jvbabi.vplanplus.feature.exams.ui.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectorSheet(
    selectedDate: LocalDate,
    sheetState: SheetState,
    onDismiss: () -> Unit = {},
    onSetDate: (date: LocalDate) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer8Dp()
            val context = LocalContext.current
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = DateUtils.getDateFromTimestamp(utcTimeMillis / 1000)
                        return date.isAfter(LocalDate.now())
                    }
                },
                initialSelectedDateMillis = selectedDate.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
            )

            LaunchedEffect(key1 = datePickerState.selectedDateMillis) {
                val millis = datePickerState.selectedDateMillis ?: return@LaunchedEffect
                val date = DateUtils.getDateFromTimestamp(millis / 1000)
                if (date == selectedDate) return@LaunchedEffect
                onSetDate(date)
                sheetState.hide()
                onDismiss()
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer8Dp() }
                items(6) {
                    val date = LocalDate.now().plusDays(it + 1L)
                    FilterChip(
                        onClick = {
                            onSetDate(date)
                            scope.launch {
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                        label = {
                            Text(text = DateUtils.localizedRelativeDate(context, date, false) ?: date.format(
                                DateTimeFormatter.ofPattern("EEE, dd. MMM yyyy")))
                        },
                        selected = selectedDate.isEqual(date)
                    )
                }
            }
            DatePicker(state = datePickerState)
        }
    }
}