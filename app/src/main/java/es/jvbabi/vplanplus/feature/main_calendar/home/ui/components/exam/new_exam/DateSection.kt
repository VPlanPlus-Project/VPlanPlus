package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.dialog.ui.SelectDateDialog
import es.jvbabi.vplanplus.util.formatDayDuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddExamDateSection(
    selectedDate: LocalDate?,
    onDateSelected: (date: LocalDate) -> Unit,
) {
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    if (showDatePickerDialog) SelectDateDialog(
        title = "Test",
        onDismiss = { showDatePickerDialog = false },
        onSelectDate = { showDatePickerDialog = false; onDateSelected(it) },
        allowedDays = { it.date.isAfter(LocalDate.now()) }
    )
    AddExamItem(
        icon = {
            AnimatedContent(
                targetState = selectedDate == null,
                label = "date section icon"
            ) { isDateNotSelected ->
                Icon(
                    imageVector = if (isDateNotSelected) Icons.Outlined.CalendarMonth else Icons.Filled.CalendarMonth,
                    modifier = Modifier.size(24.dp),
                    contentDescription = null,
                    tint = if (isDateNotSelected) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { showDatePickerDialog = true }
                .padding(start = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                targetState = selectedDate,
                label = "date section text"
            ) { displayDate ->
                if (displayDate == null) Text(
                        text = stringResource(R.string.examsNew_until),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                else Text(
                    text = buildString {
                        append(displayDate.format(DateTimeFormatter.ofPattern("EEEE, dd. MMMM yyyy")))
                        append(" $DOT ")
                        append(LocalDate.now().formatDayDuration(displayDate))
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DateSectionPreview() {
    AddExamDateSection(null) {}
}

@Composable
@Preview(showBackground = true)
private fun DateSectionPreview2() {
    AddExamDateSection(LocalDate.now().plusDays(1)) {}
}

@Composable
@Preview(showBackground = true)
private fun DateSelectionPreview3() {
    AddExamDateSection(LocalDate.now().plusDays(50)) {}
}