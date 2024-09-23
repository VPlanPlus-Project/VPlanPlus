package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.CalendarViewAction
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.CalendarViewState
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.DayViewFilter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun LessonsSection(
    state: CalendarViewState,
    day: SchoolDay,
    doAction: (action: CalendarViewAction) -> Unit,
    onTimetableInfoBannerClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = (state.enabledFilters.isEmpty() || DayViewFilter.LESSONS in state.enabledFilters) && day.lessons.isNotEmpty(),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Spacer8Dp()
                Title()
                Sp24TimetableWarning(
                    onDismiss = { doAction(CalendarViewAction.DismissTimetableInfoBanner) },
                    onTimetableInfoBannerClicked = onTimetableInfoBannerClicked,
                    hasTimetableLessons = day.lessons.any { it is Lesson.TimetableLesson },
                    canShowTimetableInfoBanner = state.canShowTimetableInfoBanner
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val lessonsGroupedByLessonNumber =
                    day.lessons.groupBy { it.lessonNumber }.toList()
                        .sortedBy { it.first }
                lessonsGroupedByLessonNumber.forEach { (lessonNumber, lessons) ->
                    LessonBlock(
                        lessonNumber,
                        lessons,
                    )
                }
            }
            Spacer8Dp()
        }
    }
}

@Composable
private fun ColumnScope.Title() {
    Spacer8Dp()
    RowVerticalCenter(
        modifier = Modifier.align(CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(id = R.string.calendar_dayFilterLessons),
            style = MaterialTheme.typography.bodySmall
        )
    }
    Spacer4Dp()
}