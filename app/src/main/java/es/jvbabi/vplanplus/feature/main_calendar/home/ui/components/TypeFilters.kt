package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.DayViewFilter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun TypeFilters(
    hasLessons: Boolean,
    hasHomework: Boolean,
    hasGrades: Boolean,
    enabledFilters: List<DayViewFilter>,
    toggleFilter: (DayViewFilter) -> Unit,
) {
    AnimatedVisibility(
        visible = hasLessons || hasHomework || hasGrades,
        enter = expandHorizontally(),
        exit = shrinkVertically()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            item { Spacer16Dp() }
            item {
                AnimatedVisibility(
                    visible = hasLessons,
                    enter = expandHorizontally(),
                    exit = shrinkVertically()
                ) {
                    Row {
                        FilterChip(
                            selected = DayViewFilter.LESSONS in enabledFilters,
                            onClick = { toggleFilter(DayViewFilter.LESSONS) },
                            label = { Text(text = stringResource(id = R.string.calendar_dayFilterLessons)) },
                            leadingIcon = {
                                Icon(imageVector = if (DayViewFilter.LESSONS in enabledFilters) Icons.Default.Check else Icons.Default.School, contentDescription = null)
                            }
                        )
                        Spacer8Dp()
                    }
                }
            }
            item {
                AnimatedVisibility(
                    visible = hasHomework,
                    enter = expandHorizontally(),
                    exit = shrinkVertically()
                ) {
                    Row {
                        FilterChip(
                            selected = DayViewFilter.HOMEWORK in enabledFilters,
                            onClick = { toggleFilter(DayViewFilter.HOMEWORK) },
                            label = { Text(text = stringResource(id = R.string.calendar_dayFilterHomework)) },
                            leadingIcon = {
                                Icon(imageVector = if (DayViewFilter.HOMEWORK in enabledFilters) Icons.Default.Check else Icons.AutoMirrored.Default.MenuBook, contentDescription = null)
                            }
                        )
                        Spacer8Dp()
                    }
                }
            }
            item {
                AnimatedVisibility(
                    visible = hasGrades,
                    enter = expandHorizontally(),
                    exit = shrinkVertically()
                ) {
                    Row {
                        FilterChip(
                            selected = DayViewFilter.GRADES in enabledFilters,
                            onClick = { toggleFilter(DayViewFilter.GRADES) },
                            label = { Text(text = stringResource(id = R.string.calendar_dayFilterGrades)) },
                            leadingIcon = {
                                if (DayViewFilter.GRADES in enabledFilters) Icon(Icons.Default.Check, contentDescription = null)
                                else Icon(painterResource(id = R.drawable.order_approve), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        )
                        Spacer8Dp()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun TypeFiltersPreview() {
    TypeFilters(
        hasLessons = true,
        hasHomework = true,
        hasGrades = true,
        enabledFilters = listOf(DayViewFilter.HOMEWORK, DayViewFilter.GRADES),
        toggleFilter = {}
    )
}

@Composable
@Preview
private fun TypeFiltersNoLessonsPreview() {
    TypeFilters(
        hasLessons = false,
        hasHomework = true,
        hasGrades = true,
        enabledFilters = listOf(DayViewFilter.HOMEWORK, DayViewFilter.GRADES),
        toggleFilter = {}
    )
}

@Composable
@Preview
private fun TypeFiltersNoHomeworkPreview() {
    TypeFilters(
        hasLessons = true,
        hasHomework = false,
        hasGrades = true,
        enabledFilters = listOf(DayViewFilter.HOMEWORK, DayViewFilter.GRADES),
        toggleFilter = {}
    )
}