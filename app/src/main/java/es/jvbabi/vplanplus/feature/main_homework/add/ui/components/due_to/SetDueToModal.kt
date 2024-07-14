package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.RectangleShape
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
fun SetDueToModal(
    sheetState: SheetState,
    selectedDate: LocalDate?,
    onSelectDate: (date: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = DateUtils.getDateFromTimestamp(utcTimeMillis / 1000)
                return date.isAfter(LocalDate.now())
            }
        },
        initialSelectedDateMillis = selectedDate?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
    )

    LaunchedEffect(key1 = datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis ?: return@LaunchedEffect
        onSelectDate(DateUtils.getDateFromTimestamp(millis / 1000))
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RectangleShape
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            DueToModalTitle()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer8Dp() }
                items(6) {
                    val date = LocalDate.now().plusDays(it + 1L)
                    FilterChip(
                        onClick = {
                            onSelectDate(date)
                            scope.launch {
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                        label = {
                            Text(text = DateUtils.localizedRelativeDate(context, date, false) ?: date.format(DateTimeFormatter.ofPattern("EEE, dd. MMM yyyy")))
                        },
                        selected = selectedDate?.isEqual(date) == true
                    )
                }
            }
            DatePicker(state = datePickerState)
        }
    }
}