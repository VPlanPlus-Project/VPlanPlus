package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddExamReminderSection(
    selectedDays: Set<Int>?,
    selectedDate: LocalDate?,
    selectedCategory: ExamCategory?,
    onRemindDaysBeforeSelected: (days: Set<Int>) -> Unit
) {
    AddExamItem(
        willBeHorizontalScrollable = true,
        icon = {
            AnimatedContent(
                targetState = selectedDays != null,
                label = "userHasChanged"
            ) { userHasChanged ->
                Icon(
                    imageVector = if (userHasChanged) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = if (userHasChanged || (selectedDate != null && selectedCategory != null)) MaterialTheme.colorScheme.onSurface else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        AnimatedContent(
            targetState = selectedDate != null && selectedCategory != null,
            label = "is reminder enabled"
        ) { isEnabled ->
            if (isEnabled) ExamReminderSelector(
                selectedDate = selectedDate ?: LocalDate.now(),
                selectedDays = selectedDays,
                selectedType = selectedCategory ?: ExamCategory.Other,
                startPadding = 40.dp,
                onRemindDaysBeforeSelected = onRemindDaysBeforeSelected
            ) else {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(start = 56.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = stringResource(id = R.string.examsNew_reminderDisabled),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ExamReminderSelector(
    selectedDate: LocalDate,
    selectedDays: Set<Int>?,
    selectedType: ExamCategory,
    startPadding: Dp? = null,
    onRemindDaysBeforeSelected: (days: Set<Int>) -> Unit,
    onExamDateClicked: (() -> Unit)? = null
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { startPadding?.let { Spacer(Modifier.size(it)) } ?: Spacer8Dp() }
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
