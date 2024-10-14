package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReminderSection(
    selectedDays: Set<Int>?,
    selectedDate: LocalDate,
    selectedType: ExamType,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onRemindDaysBeforeSelected: (days: Set<Int>) -> Unit
) {
    Section(
        title = {
            TitleRow(
                title = "Reminder",
                subtitle = "Select how many days in advance you want to be reminded",
                icon = Icons.Default.NotificationsActive,
                onClick = onHeaderClicked
            )
        },
        isVisible = true,
        isContentExpanded = isContentExpanded,
    ) {
        ExamReminderSelector(selectedDate, selectedDays, selectedType, onRemindDaysBeforeSelected)
    }
}

@Composable
fun ExamReminderSelector(
    selectedDate: LocalDate,
    selectedDays: Set<Int>?,
    selectedType: ExamType,
    onRemindDaysBeforeSelected: (days: Set<Int>) -> Unit,
    onExamDateClicked: (() -> Unit)? = null
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { Spacer8Dp() }
        repeat(7) {
            val date = selectedDate.minusDays(7 - it.toLong())
            if (date.isBefore(LocalDate.now())) return@repeat
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = date.format(DateTimeFormatter.ofPattern("EE")))
                    AnimatedContent(
                        targetState = (selectedDays
                            ?: selectedType.remindDaysBefore).contains(7 - it),
                        label = "isDaySelected"
                    ) { isDaySelected ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDaySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    onRemindDaysBeforeSelected(
                                        (selectedDays
                                            ?: selectedType.remindDaysBefore)
                                            .toMutableSet()
                                            .apply { if (isDaySelected) remove(7 - it) else add(7 - it) })
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDaySelected) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                modifier = Modifier.size(16.dp),
                                contentDescription = null,
                                tint = if (isDaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(text = date.format(DateTimeFormatter.ofPattern("d. MM")))
                }
            }
        }
        item {
            VerticalDivider(Modifier.height(48.dp))
        }
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Termin",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .then(onExamDateClicked?.let { Modifier.clickable { onExamDateClicked() } } ?: Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Text(selectedDate.format(DateTimeFormatter.ofPattern("d. MM")))
            }
            Spacer8Dp()
        }
    }
}
