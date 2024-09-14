package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer2Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.util.blendColor
import java.time.LocalDate
import java.time.Month

/**
 * A single day in the calendar.
 * @param displayMonth The month that is currently displayed primarily. If the day is not in this month, it will be grayed out.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Day(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    homework: List<PersonalizedHomework>,
    exams: Int,
    displayMonth: Month?,
    state: DayDisplayState,
    progress: Float,
    onClick: () -> Unit = {}
) {
    val isSelectedModifier by animateFloatAsState(if (isSelected) 1f else 0f, label = "isSelectedModifier")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f, true)
        )
        Box(
            modifier = Modifier
                .weight(1.2f, false)
                .defaultMinSize(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50))
                    .background(blendColor(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0f), MaterialTheme.colorScheme.primaryContainer, isSelectedModifier))
                    .then(
                        if (isToday && !isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50))
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                val normalColor = blendColor(if (isToday) MaterialTheme.colorScheme.secondary
                    else if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    isSelectedModifier
                )

                val colorRegardingSelectedMonth =
                    if (displayMonth != null && displayMonth != date.month) Color.Gray
                    else normalColor
                Text(
                    text = date.dayOfMonth.toString(),
                    color =
                    when (state) {
                        DayDisplayState.SMALL -> normalColor
                        DayDisplayState.REGULAR -> blendColor(normalColor, colorRegardingSelectedMonth, progress)
                        else -> colorRegardingSelectedMonth
                    }
                )
            }
        }
        Spacer4Dp()
        Box(
            modifier = Modifier.weight(if (state == DayDisplayState.DETAILED) 1f + 3*progress else 1f, true),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state == DayDisplayState.DETAILED) Column(
                modifier = Modifier.alpha((progress-.5f)*2),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                homework.forEach { hw ->
                    RowVerticalCenter (
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(hw.getBackgroundColor()),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer2Dp()
                        Icon(if (hw.allDone()) Icons.Default.Check else Icons.AutoMirrored.Default.MenuBook, null, modifier = Modifier.size(12.dp), tint = hw.getTextColor())
                        Spacer2Dp()
                        Text(
                            text = hw.homework.defaultLesson?.subject ?: stringResource(id = R.string.homework_noSubject),
                            color = hw.getTextColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textDecoration = if (hw.allDone()) TextDecoration.LineThrough else null,
                        )
                    }
                }

                repeat(exams) {
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(Color.Red),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Test/KA",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
            FlowRow(
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(if (state == DayDisplayState.DETAILED) (1 - progress*2) else 1f)
            ) {
                homework.forEach { hw ->
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(hw.getBackgroundColor()),
                    )
                }
                repeat(exams) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Red)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun WeekendPreview() {
    Day(
        date = LocalDate.of(2024, 1, 14),
        isSelected = false,
        isToday = true,
        homework = emptyList(),
        exams = 0,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.SMALL,
        progress = 1f
    )
}

@Composable
@Preview
private fun SelectedPreview() {
    Day(
        date = LocalDate.of(2024, 1, 14),
        isSelected = true,
        isToday = false,
        homework = emptyList(),
        exams = 0,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.SMALL,
        progress = 1f
    )
}

@Composable
@Preview
private fun DetailedPreview() {
    Day(
        date = LocalDate.of(2024, 2, 14),
        isSelected = false,
        isToday = false,
        homework = emptyList(),
        exams = 2,
        displayMonth = Month.JANUARY,
        state = DayDisplayState.DETAILED,
        progress = 1f
    )
}

enum class DayDisplayState {
    SMALL,
    REGULAR,
    DETAILED
}

@Composable
private fun PersonalizedHomework.getBackgroundColor(): Color {
    val homeworkDone = MaterialTheme.colorScheme.surfaceContainer
    val homeworkOverdue = MaterialTheme.colorScheme.errorContainer
    val homeworkNotDone = MaterialTheme.colorScheme.primary
    val overdue = homework.until.toLocalDate().isBefore(LocalDate.now())
    return when {
        allDone() -> homeworkDone
        overdue -> homeworkOverdue
        else -> homeworkNotDone
    }
}

@Composable
private fun PersonalizedHomework.getTextColor(): Color {
    val homeworkDone = MaterialTheme.colorScheme.onSurface
    val homeworkOverdue = MaterialTheme.colorScheme.onErrorContainer
    val homeworkNotDone = MaterialTheme.colorScheme.onPrimary
    val overdue = homework.until.toLocalDate().isBefore(LocalDate.now())
    return when {
        allDone() -> homeworkDone
        overdue -> homeworkOverdue
        else -> homeworkNotDone
    }
}