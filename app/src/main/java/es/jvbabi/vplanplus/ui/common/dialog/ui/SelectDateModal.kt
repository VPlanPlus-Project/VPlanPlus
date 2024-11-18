package es.jvbabi.vplanplus.ui.common.dialog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.DateSelectCause
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.FullMonthPager
import es.jvbabi.vplanplus.ui.common.SmallDragHandler
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateModal(
    selectDateViewModel: SelectDateViewModel = hiltViewModel(),
    sheetState: SheetState,
    allowedDays: (day: SchoolDay) -> Boolean = { true },
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    onSelectDate: (date: LocalDate) -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 16.dp)
            .fillMaxSize(),
        dragHandle = { SmallDragHandler() },
        contentWindowInsets = { BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom) },
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                subtitle?.let {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {}
                items(5) { offset ->
                    val date = LocalDate.now().plusDays(offset.toLong())
                    val day = selectDateViewModel.state.days[date] ?: SchoolDay(date = date)
                    if (!allowedDays(day)) return@items
                    AssistChip(
                        onClick = {
                            scope.launch {
                                onSelectDate(date)
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                        label = {
                            Text(DateUtils.relativeDateStringResource(LocalDate.now(), date)?.let { stringResource(it) } ?: date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
            FullMonthPager(
                calendarSelectHeightLarge = null,
                setFirstVisibleDate = {},
                selectDate = { date, selectCause ->
                    scope.launch {
                        if (selectCause == DateSelectCause.CALENDAR_SWIPE) selectDateViewModel.doAction(SelectDateDialogEvent.SelectDate(date))
                        else onSelectDate(date)
                        sheetState.hide()
                        onDismiss()
                    }
                },
                checkIfDayIsSelectable = allowedDays,
                days = selectDateViewModel.state.days,
                selectedDate = selectDateViewModel.state.selectedDate
            )
        }
    }
}